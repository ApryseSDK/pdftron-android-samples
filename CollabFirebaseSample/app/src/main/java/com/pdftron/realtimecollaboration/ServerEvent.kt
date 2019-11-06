package com.pdftron.realtimecollaboration

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

sealed class ServerEvent {
    class SignUp(val response: Task<AuthResult>) : ServerEvent()
    class SignIn(val name: String) : ServerEvent()
    class SetUserAndDocument(val authorId: String, val authorName: String, val documentId: String) : ServerEvent()
    class ImportXfdfCommand(val xfdfCommand: String) : ServerEvent()
}