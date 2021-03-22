package com.pdftron.pdftronsignapp.listeners

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import com.pdftron.pdf.Annot
import com.pdftron.pdf.tools.ToolManager

class MyBasicAnnotationListener: ToolManager.BasicAnnotationListener {
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
            // Intercept clicking widget annotation by return true
            if (annot!!.type == Annot.e_Widget) {
                Log.d("InterceptAnnot", "handling widget annotation")
                val userEmail = annot.getCustomData("email")
                if(userEmail.isNullOrEmpty())
                    annot.setCustomData("email", "email@email.com")
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
}