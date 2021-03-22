package com.pdftron.pdftronsignapp.util

import android.util.Log
import com.google.firebase.Timestamp.now
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.pdftron.pdftronsignapp.model.DocumentToSign
import com.pdftron.pdftronsignapp.model.User

class FirebaseControl {

    fun generateUserDocument(user: FirebaseUser) {
        val fireStore = FirebaseFirestore.getInstance()
        val uid = Firebase.auth.currentUser?.uid
        val userRef = fireStore.document("users/${uid}")
        userRef.get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    userRef.set(User(user.displayName,user.email,user.photoUrl.toString()))
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FirebaseControl", "get failed with ", exception)
            }
    }

    fun getUserDocument(updateUi: (data: DocumentSnapshot?) -> Unit) {
        val fireStore = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val userRef = fireStore.document("users/${user?.uid}")
        userRef.get()
            .addOnSuccessListener { snapshot ->
                updateUi(snapshot)
            }
            .addOnFailureListener { exception ->
                Log.d("FirebaseControl", "get failed with ", exception)
            }
    }

    fun addDocumentToSign(docRef: String, emailList: List<String>) {
        val fireStore = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        fireStore.collection("documentsToSign")
            .add(DocumentToSign(
                uid = user.uid,
                email = user.email,
                docRef = docRef,
                emails = emailList,
                requestedTime = now(),
                signed = false,
                signedBy = listOf(),
                signedTime = "",
                xfdf = listOf()
            ))
    }

    fun updateDocumentToSign() {

    }

    fun searchForDocumentToSign() {

    }

    fun searchForDocumentsSigned() {

    }
}