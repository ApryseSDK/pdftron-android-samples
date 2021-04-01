package com.pdftron.pdftronsignapp.data

import com.google.firebase.Timestamp

data class DocumentToSign(
    var docId: String = "",
    val uid: String,
    val email: String,
    val docRef: String,
    val emails: List<String>,
    val requestedTime: Timestamp,
    var signed: Boolean,
    val signedBy: MutableList<String>,
    var signedTime: Timestamp?,
    val xfdf: MutableList<String>
    )
