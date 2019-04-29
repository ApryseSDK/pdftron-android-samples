package com.pdftron.realtimecollaboration

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pdftron.collab.db.CollabDatabase
import com.pdftron.collab.db.entity.AnnotationEntity
import com.pdftron.collab.service.CustomService
import com.pdftron.collab.utils.XfdfUtils
import com.pdftron.pdf.utils.Utils
import com.pdftron.realtimecollaboration.model.Annotation
import com.pdftron.realtimecollaboration.model.User
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class Server(applicationContext: Context) : CustomService {

    private var mDatabase: CollabDatabase? = null

    init {
        mDatabase = CollabDatabase.getInstance(applicationContext)
    }

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
    private val mInitialAnnotMap = HashMap<String, AnnotationEntity>()

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
        mDisposables.add(
            nukeDB()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { this.destroy() },
                    { Log.e(TAG, "nukeDB failed") })
        )
    }

    private fun destroy() {
        Log.d(TAG, "nukeDB done")
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

            mDisposables.add(handleChildChanged(key, newAnnot).subscribeOn(Schedulers.io()).subscribe())
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

            mDisposables.add(handleChildAdded(key, newAnnot).subscribeOn(Schedulers.io()).subscribe())
        }

        override fun onChildRemoved(p0: DataSnapshot) {
            // delete
            val key = p0.key
            Log.d(TAG, "delete: {$key}")

            mDisposables.add(handleChildRemoved(key).subscribeOn(Schedulers.io()).subscribe())
        }
    }

    private fun initDB() {
        val database = FirebaseDatabase.getInstance()
        annotationsRef = database.getReference("annotations")
        authorsRef = database.getReference("authors")
    }

    private fun nukeDB(): Completable {
        return Completable.fromAction {
            cleanup(mDatabase)
        }
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
            mDisposables.add(addUserAndDocument(authorId, authorName).subscribeOn(Schedulers.io()).subscribe())

            // subscribe to authors
            authorsRef!!.addChildEventListener(authorChildEventListener)

            // subscribe to annotations
            annotationsRef!!.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (child in p0.children) {
                        val key = child.key
                        val newAnnot = child.getValue(Annotation::class.java)
                        mInitialAnnotMap[key!!] = convAnnotationToAnnotationEntity(key, newAnnot!!)
                    }
                    mDisposables.add(handleChildrenAdded(mInitialAnnotMap).subscribeOn(Schedulers.io()).subscribe())
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

    private fun addUserAndDocument(authorId: String, authorName: String): Completable {
        return Completable.fromAction {
            addUser(mDatabase, authorId, authorName)

            addDocument(mDatabase, DOCUMENT_ID)
        }
    }

    private fun convAnnotationToAnnotationEntity(annotId: String, annotationData: Annotation): AnnotationEntity {
        val userName = mUserMap[annotationData.authorId]
        val annotationEntity = AnnotationEntity().apply {
            id = annotId
            documentId = DOCUMENT_ID
            authorId = annotationData.authorId
            authorName = userName
            xfdf = annotationData.xfdf
            at = "create"

        }
        XfdfUtils.fillAnnotationEntity(annotationEntity)
        return annotationEntity
    }

    fun handleChildrenAdded(map: HashMap<String, AnnotationEntity>): Completable {
        return Completable.fromAction { handleChildrenAddedImpl(map) }
    }

    private fun handleChildrenAddedImpl(map: HashMap<String, AnnotationEntity>) {
        addAnnotations(mDatabase, map)
    }

    fun handleChildAdded(annotId: String?, annotationData: Annotation?): Completable {
        return Completable.fromAction { handleChildAddedImpl(annotId, annotationData) }
    }

    private fun handleChildAddedImpl(annotId: String?, annotationData: Annotation?) {
        if (null == annotId || null == annotationData) {
            return
        }

        addAnnotation(mDatabase, convAnnotationToAnnotationEntity(annotId, annotationData))
    }

    fun handleChildChanged(annotId: String?, annotationData: Annotation?): Completable {
        return Completable.fromAction { handleChildChangedImpl(annotId, annotationData) }
    }

    private fun handleChildChangedImpl(annotId: String?, annotationData: Annotation?) {
        if (null == annotId || null == annotationData) {
            return
        }
        val annotationEntity = AnnotationEntity().apply {
            id = annotId
            documentId = DOCUMENT_ID
            authorId = annotationData.authorId
            xfdf = annotationData.xfdf
            at = "modify"

        }
        XfdfUtils.fillAnnotationEntity(annotationEntity)
        modifyAnnotation(mDatabase, annotationEntity)
    }

    fun handleChildRemoved(annotId: String?): Completable {
        return Completable.fromAction { handleChildRemovedImpl(annotId) }
    }

    private fun handleChildRemovedImpl(annotId: String?) {
        if (null == annotId) {
            return
        }
        deleteAnnotation(mDatabase, annotId)
    }

    private val OP_ADD = "add"
    private val OP_MODIFY = "modify"
    private val OP_REMOVE = "remove"

    // CustomService start
    override fun sendAnnotation(
        action: String?,
        annotations: ArrayList<AnnotationEntity>?,
        documentId: String?,
        userName: String?
    ) {
        if (Utils.isNullOrEmpty(action)) {
            return
        }
        for (entity in annotations!!) {
            val op = entity.at
            val annotId = entity.id
            val authorId = auth.currentUser!!.uid
            val xfdf = entity.xfdf
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