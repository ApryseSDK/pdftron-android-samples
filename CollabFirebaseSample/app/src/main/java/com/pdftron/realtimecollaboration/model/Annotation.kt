package com.pdftron.realtimecollaboration.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Annotation(
    var authorId: String? = "",
    var parentAuthorId: String? = "",
    var xfdf: String? = ""
)
