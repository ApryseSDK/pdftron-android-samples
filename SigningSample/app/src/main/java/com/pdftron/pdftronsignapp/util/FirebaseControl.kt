package com.pdftron.pdftronsignapp.util

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pdftron.fdf.FDFDoc
import com.pdftron.pdf.PDFDoc
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2
import com.pdftron.pdftronsignapp.data.DocumentToSign
import com.pdftron.pdftronsignapp.data.User
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FirebaseControl {

    fun generateUserDocument(user: FirebaseUser) {
        val fireStore = FirebaseFirestore.getInstance()
        val uid = Firebase.auth.currentUser?.uid
        val userRef = fireStore.document("users/${uid}")
        userRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    var userName = user.displayName
                    var photoUrl = user.photoUrl
                    if (userName.isNullOrEmpty())
                        userName = user.email
                    if (photoUrl == null)
                        photoUrl = Uri.EMPTY
                    userRef.set(User(userName!!, user.email!!, photoUrl!!.toString()))
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FirebaseControl", "get failed with ", exception)
            }
    }

    fun addDocumentToSign(file: File, emailList: List<String>, closeFragment: () -> Unit) {
        val referenceString = uploadDocToStorage(file)
        val fireStore = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        fireStore.collection("documentsToSign")
            .add(
                DocumentToSign(
                    uid = user.uid,
                    email = user.email,
                    docRef = referenceString,
                    emails = emailList,
                    requestedTime = now(),
                    signed = false,
                    signedBy = mutableListOf(),
                    signedTime = null,
                    xfdf = mutableListOf()
                )
            ).addOnSuccessListener {
                closeFragment()
            }
    }

    fun updateDocumentToSign(
        pdfViewCtrlFragment: PdfViewCtrlTabHostFragment2,
        docId: String,
        closeFragment: () -> Unit
    ) {
        val fireStore = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val filePath = pdfViewCtrlFragment.currentPdfViewCtrlFragment.filePath
        val pdfDoc = pdfViewCtrlFragment.currentPdfViewCtrlFragment.pdfDoc
        val fdfDoc = pdfDoc.fdfExtract(PDFDoc.e_both)
        val xdfString = fdfDoc.saveAsXFDF()
        var updatedFile: File? = null
        val docRef = fireStore.collection("documentsToSign").document(docId)
        docRef.get().addOnSuccessListener { snapshot ->
            val documentData = snapshot.data
            val document = DocumentToSign(
                docId = snapshot.id,
                uid = documentData?.get("uid") as String,
                email = documentData["email"] as String,
                docRef = documentData["docRef"] as String,
                emails = documentData["emails"] as List<String>,
                requestedTime = documentData["requestedTime"] as Timestamp,
                signed = documentData["signed"] as Boolean,
                signedBy = documentData["signedBy"] as MutableList<String>,
                signedTime = documentData["signedTime"] as Timestamp?,
                xfdf = documentData["xfdf"] as MutableList<String>
            )
            document.xfdf.add(xdfString)
            document.signedBy.add(user.email)
            pdfViewCtrlFragment.currentPdfViewCtrlFragment.pdfViewCtrl.docLock(true) {
                //check if everyone has signed
                if (document.signedBy.count() == document.emails.count()) {
                    document.signed = true
                    document.signedTime = now()
                    updatedFile = flattenAllAnnotationsAndUploadDocument(
                        document.docRef,
                        filePath,
                        pdfDoc,
                        document.xfdf
                    )
                }
            }
            pdfViewCtrlFragment.currentPdfViewCtrlFragment.pdfViewCtrl.update(true)
            val updatedData = hashMapOf(
                "xfdf" to document.xfdf,
                "signedBy" to document.signedBy,
                "signed" to document.signed,
                "signedTime" to document.signedTime
            )
            docRef.update(updatedData).addOnSuccessListener {
                closeFragment()
                if(updatedFile != null)
                    deleteTempFile(updatedFile!!)
            }
        }
    }

    fun searchForDocumentToSign(updateDocList: (List<DocumentToSign>) -> Unit) {
        val signedDocIds = mutableListOf<String>()
        val documentToSignList = mutableListOf<DocumentToSign>()
        val documentsRef = FirebaseFirestore.getInstance().collection("documentsToSign")
        val user = Firebase.auth.currentUser
        val queryAllDocsForThisUser = documentsRef
            .whereArrayContains("emails", user.email)
            .whereEqualTo("signed", false)
        val querySignedByThisUser = documentsRef
            .whereArrayContains("signedBy", user.email)

        querySignedByThisUser.get()
            .addOnSuccessListener {
                it.documents.map { snapshot ->
                    signedDocIds.add(snapshot.id)
                }
            }
        queryAllDocsForThisUser.get()
            .addOnSuccessListener { it ->
                it.documents.forEach { snapshot ->
                    val test = signedDocIds.any { signedDocId -> signedDocId == snapshot.id }
                    if (!test) {
                        val document = snapshot.data
                        documentToSignList.add(
                            DocumentToSign(
                                docId = snapshot.id,
                                uid = document?.get("uid") as String,
                                email = document["email"] as String,
                                docRef = document["docRef"] as String,
                                emails = document["emails"] as List<String>,
                                requestedTime = document["requestedTime"] as Timestamp,
                                signed = document["signed"] as Boolean,
                                signedBy = document["signedBy"] as MutableList<String>,
                                signedTime = document["signedTime"] as Timestamp?,
                                xfdf = document["xfdf"] as MutableList<String>
                            )
                        )
                    }
                }
                updateDocList(documentToSignList)
            }
    }

    fun searchForDocumentsSigned(updateDocSignedList: (List<DocumentToSign>) -> Unit) {
        val documentSignedList = mutableListOf<DocumentToSign>()
        val documentsRef = FirebaseFirestore.getInstance().collection("documentsToSign")
        val user = Firebase.auth.currentUser
        val queryAllDocsSigned = documentsRef
            .whereArrayContains("emails", user.email)
            .whereEqualTo("signed", true)

        queryAllDocsSigned.get()
            .addOnSuccessListener {
                it.documents.forEach { snapshot ->
                    val document = snapshot.data
                    documentSignedList.add(
                        DocumentToSign(
                            docId = snapshot.id,
                            uid = document?.get("uid") as String,
                            email = document["email"] as String,
                            docRef = document["docRef"] as String,
                            emails = document["emails"] as List<String>,
                            requestedTime = document["requestedTime"] as Timestamp,
                            signed = document["signed"] as Boolean,
                            signedBy = document["signedBy"] as MutableList<String>,
                            signedTime = document["signedTime"] as Timestamp?,
                            xfdf = document["xfdf"] as MutableList<String>
                        )
                    )
                }
                updateDocSignedList(documentSignedList)
            }
    }

    fun getDocumentUriFromFb(docRef: String, fbUrlFound: (file: File) -> Unit) {
        val storageRef = Firebase.storage.reference
        val localFile = File.createTempFile("imATest", "pdf")
        storageRef.child(docRef).getFile(localFile).addOnSuccessListener {
            fbUrlFound(localFile)
        }
    }

    private fun uploadDocToStorage(file: File): String {
        val storageRef = Firebase.storage.reference
        val sdf = SimpleDateFormat("ddMMyyyyhhmmss")
        val currentDate = sdf.format(Date())
        val referenceString = "docToSign/${Firebase.auth.uid}${currentDate}.pdf"
        val docRef = storageRef.child(referenceString)
        docRef.putFile(Uri.fromFile(file))

        return referenceString
    }

    private fun flattenAllAnnotationsAndUploadDocument(
        docRefString: String,
        filePath: String,
        pdfDoc: PDFDoc,
        fdfStrings: List<String>
    ): File {
        val storageRef = Firebase.storage.reference
        val docRef = storageRef.child(docRefString)

        for (fdf in fdfStrings) {
            val fdfDoc = FDFDoc.createFromXFDF(fdf)
            pdfDoc.fdfMerge(fdfDoc)
            println(fdf)
        }

        pdfDoc.lock()
        pdfDoc.flattenAnnotationsAdvanced(
            arrayOf(
                PDFDoc.FlattenMode.ANNOTS,
                PDFDoc.FlattenMode.FORMS
            )
        )
        pdfDoc.save()
        pdfDoc.unlock()
        val updatedFile = File(filePath)
        docRef.putFile(Uri.fromFile(updatedFile))
        return updatedFile
    }

    private fun deleteTempFile(file: File) {
        try {
            file.delete()
        } catch (ex: Exception) {
            println(ex.message)
        }
    }
}