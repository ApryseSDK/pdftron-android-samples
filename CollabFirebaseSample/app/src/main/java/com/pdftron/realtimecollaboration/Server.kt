package com.pdftron.realtimecollaboration

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pdftron.collab.db.entity.AnnotationEntity
import com.pdftron.collab.service.CustomServiceUtils
import com.pdftron.collab.utils.XfdfUtils
import com.pdftron.fdf.FDFDoc
import com.pdftron.pdf.utils.Utils
import com.pdftron.realtimecollaboration.model.Annotation
import com.pdftron.realtimecollaboration.model.User
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

class Server {

    private val TAG = "Server"

    private val DOCUMENT_ID = "pdftron_test"

    private lateinit var mBroadcaster: FlowableEmitter<ServerEvent>
    private var mFlowableDisposable: Disposable? = null
    private val mFlowable = Flowable.create(
        FlowableOnSubscribe<ServerEvent> { emitter -> mBroadcaster = emitter },
        BackpressureStrategy.BUFFER
    )

    private var mDisposables: CompositeDisposable = CompositeDisposable()

    private val mUserMap = HashMap<String, String?>()
    private val mInitialAnnotMap = HashMap<String, String>()

    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var annotationsRef: DatabaseReference? = null
    private var authorsRef: DatabaseReference? = null

    fun signIn(): Flowable<ServerEvent> {
        mFlowableDisposable = mFlowable.subscribe()

        auth.signInAnonymously()
            .addOnCompleteListener {
                initDB()
                // try get user name
                val authorId = auth.currentUser!!.uid
                authorsRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.hasChild(authorId)) {
                            val user = p0.child(authorId).getValue(User::class.java)
                            mBroadcaster.onNext(ServerEvent.SignIn(user!!.authorName!!))
                            fetchAnnotations(authorId, user.authorName!!)
                        } else {
                            mBroadcaster.onNext(ServerEvent.SignUp(it))
                        }
                    }
                })

            }

        return mFlowable
    }

    fun signOut() {
        authorsRef?.removeEventListener(authorChildEventListener)
        annotationsRef?.removeEventListener(annotChildEventListener)

        mFlowableDisposable?.dispose()
        mDisposables.clear()
    }

    private val authorChildEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            val key = p0.key
            val newUser = p0.getValue(User::class.java)
            mUserMap[key!!] = newUser?.authorName
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val key = p0.key
            val newUser = p0.getValue(User::class.java)
            mUserMap[key!!] = newUser?.authorName
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

    }

    private val annotChildEventListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
            // no op
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            // no op
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            // modify
            val key = p0.key
            val newAnnot = p0.getValue(Annotation::class.java)
            Log.d(TAG, "modify {" + key + "}" + newAnnot.toString())

            mBroadcaster.onNext(ServerEvent.ImportXfdfCommand(newAnnot!!.xfdf!!, false))
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            // add
            val key = p0.key
            if (mInitialAnnotMap.containsKey(key)) {
                // already added in initial batch
                return
            }
            val newAnnot = p0.getValue(Annotation::class.java)
            Log.d(TAG, "add: {" + key + "}" + newAnnot.toString())

            mBroadcaster.onNext(ServerEvent.ImportXfdfCommand(newAnnot!!.xfdf!!, false))
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            // delete
            val key = p0.key
            Log.d(TAG, "delete: {$key}")

            val xfdf = XfdfUtils.wrapDeleteXfdf(key)
            mBroadcaster.onNext(ServerEvent.ImportXfdfCommand(xfdf, false))
        }
    }

    private fun initDB() {
        val database = FirebaseDatabase.getInstance()
        annotationsRef = database.getReference("annotations")
        authorsRef = database.getReference("authors")
    }

    fun updateAuthor(authorName: String) {
        if (auth.currentUser != null && authorsRef != null) {
            val authorId = auth.currentUser!!.uid

            authorsRef!!.child(authorId).child("authorName").setValue(authorName)

            fetchAnnotations(authorId, authorName)
        }
    }

    fun fetchAnnotations(authorId: String, authorName: String) {
        if (auth.currentUser != null && authorsRef != null && annotationsRef != null) {
            // add user and sample document
            mBroadcaster.onNext(ServerEvent.SetUserAndDocument(authorId, authorName, DOCUMENT_ID))

            // subscribe to authors
            authorsRef!!.addChildEventListener(authorChildEventListener)

            // subscribe to annotations
            annotationsRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    val fdfDoc = FDFDoc()

                    for (child in p0.children) {
                        val key = child.key
                        val newAnnot = child.getValue(Annotation::class.java)
                        if (key != null && newAnnot != null && newAnnot.xfdf != null && newAnnot.xfdf!!.contains("<add>")) {
                            mInitialAnnotMap[key] = key
                            fdfDoc.mergeAnnots(XfdfUtils.validateXfdf(newAnnot.xfdf!!))
                        }
                    }

                    var xfdfCommands = fdfDoc.saveAsXFDF()
                    // turn into command
                    xfdfCommands = xfdfCommands.replace("<annots>", "<add>")
                    xfdfCommands = xfdfCommands.replace("</annots>", "</add>")

                    mBroadcaster.onNext(ServerEvent.ImportXfdfCommand(xfdfCommands, true))

                    annotationsRef!!.addChildEventListener(annotChildEventListener)
                }
            })
        }
    }

    private fun createAnnotation(annotationId: String, annotationData: Annotation) {
        annotationsRef!!.child(annotationId).setValue(annotationData)
    }

    private fun changeAnnotation(annotationId: String, annotationData: Annotation) {
        annotationsRef!!.child(annotationId).setValue(annotationData)
    }

    private fun removeAnnotation(annotationId: String) {
        annotationsRef!!.child(annotationId).removeValue()
    }

    private val OP_ADD = "add"
    private val OP_MODIFY = "modify"
    private val OP_REMOVE = "remove"

    // CustomService start
    fun sendAnnotation(
        action: String?,
        annotations: ArrayList<AnnotationEntity>?
    ) {
        if (Utils.isNullOrEmpty(action)) {
            return
        }
        for (entity in annotations!!) {
            val op = entity.at
            val annotId = entity.id
            val authorId = auth.currentUser!!.uid
            val xfdf = CustomServiceUtils.getXfdfFromFile(entity.xfdf)
            when (op) {
                OP_ADD -> {
                    val annotation = Annotation(
                        authorId,
                        null,
                        xfdf
                    )
                    createAnnotation(annotId, annotation)
                }
                OP_MODIFY -> {
                    val annotation = Annotation(
                        authorId,
                        null,
                        xfdf
                    )
                    changeAnnotation(annotId, annotation)
                }
                OP_REMOVE -> {
                    removeAnnotation(annotId)
                }
            }
        }
    }
    // CustomService end

}