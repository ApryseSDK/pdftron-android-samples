package com.pdftron.pdftronsignapp.util

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdftron.common.PDFNetException
import com.pdftron.pdf.Annot
import com.pdftron.pdf.Field
import com.pdftron.pdf.PDFDoc
import com.pdftron.pdf.annots.SignatureWidget
import com.pdftron.pdf.annots.Widget
import java.io.File

fun removeAnnotationsForOtherUsers(file: File): File {
    val currentUser = Firebase.auth.currentUser
    val doc = PDFDoc(file.absolutePath)
    for (pageNumber in  1..doc.pageCount){
        val page = doc.getPage(pageNumber)
        if(page.isValid){
            val annotationCount = page.numAnnots


            for (a in annotationCount downTo 0){
                try {
                    val annotation = page.getAnnot(a)
                    if(annotation == null || !annotation.isValid){
                        continue
                    }
                    if(annotation.getCustomData("email") != currentUser?.email){
                        page.annotRemove(annotation)
                    }
                }
                catch (e: PDFNetException) {
                    // this annotation has some problem, let's skip it and continue with others
                }
            }
        }
    }
    doc.save()

    return File(file.absolutePath)
}

fun areAllSignFieldsComplete(doc: PDFDoc): Boolean{
    for (pageNumber in  1..doc.pageCount){
        val page = doc.getPage(pageNumber)
        if(page.isValid){
            val annotationCount = page.numAnnots
            for (a in annotationCount downTo 0){
                try {
                    val annotation = page.getAnnot(a)
                    if(annotation == null || !annotation.isValid){
                        continue
                    }
                    if(annotation.type == Annot.e_Widget) {
                        val widget = Widget(annotation)
                        val field = widget.field
                        if (field.type == Field.e_signature) {
                            val signatureWidget = SignatureWidget(annotation)
                            if (!signatureWidget.digitalSignatureField.hasVisibleAppearance())
                                return false
                        }
                    }
                }
                catch (e: PDFNetException) {
                    // this annotation has some problem, let's skip it and continue with others
                }
            }
        }
    }
    return true
}