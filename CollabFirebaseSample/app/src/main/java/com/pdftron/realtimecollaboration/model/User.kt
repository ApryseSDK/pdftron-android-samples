package com.pdftron.realtimecollaboration.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    var authorName: String? = ""
)