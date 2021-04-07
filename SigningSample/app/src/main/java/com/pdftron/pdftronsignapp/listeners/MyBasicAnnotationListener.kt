package com.pdftron.pdftronsignapp.listeners

import android.app.AlertDialog
import android.os.Bundle
import com.pdftron.pdf.Annot
import com.pdftron.pdf.tools.ToolManager
import com.pdftron.pdftronsignapp.data.User

class MyBasicAnnotationListener: ToolManager.BasicAnnotationListener {
    private lateinit var currentUser: User

    override fun onAnnotationSelected(p0: Annot?, p1: Int) {

    }

    override fun onAnnotationUnselected() {
    }

    override fun onInterceptAnnotationHandling(
        annot: Annot?,
        extra: Bundle?,
        toolMode: ToolManager.ToolMode?
    ): Boolean {
        try {
            val annotEmail = annot?.getCustomData("email")
            if (annotEmail.isNullOrEmpty()) {
                annot?.setCustomData("email", currentUser.email)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // return false so the other events can continue executing
        return false
    }

    override fun onInterceptDialog(p0: AlertDialog?): Boolean {
        return false
    }

    fun setCurrentUser(selectedUser: User){
        currentUser = selectedUser
    }
}