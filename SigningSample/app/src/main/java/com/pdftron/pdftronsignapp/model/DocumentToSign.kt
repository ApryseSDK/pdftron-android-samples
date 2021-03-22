package com.pdftron.pdftronsignapp.model

import com.google.firebase.Timestamp

data class DocumentToSign(
    val uid: String,
    val email: String,
    val docRef: String,
    val emails: List<String>,
    val requestedTime: Timestamp,
    val signed: Boolean,
    val signedBy: List<String>,
    val signedTime: String,
    val xfdf: List<String>
    )
