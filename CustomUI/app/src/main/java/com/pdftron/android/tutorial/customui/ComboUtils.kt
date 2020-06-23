package com.pdftron.android.tutorial.customui

import android.graphics.Color
import com.pdftron.common.PDFNetException
import com.pdftron.pdf.Annot
import com.pdftron.pdf.PDFDoc
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.pdf.Rect
import com.pdftron.pdf.annots.ComboBoxWidget
import com.pdftron.pdf.utils.Utils
import java.util.*

fun createCombo(pdfDoc: PDFDoc, pdfViewCtrl: PDFViewCtrl) {
    try {
        val dropDownAnnotation = ComboBoxWidget.create(pdfDoc, Rect(0.0,0.0,100.0,50.0))
        pdfViewCtrl.setHighlightFields(true)
//            if (annotationHolder.required) {
        dropDownAnnotation.setBackgroundColor(Utils.color2ColorPt(Color.YELLOW), 3)
        pdfViewCtrl.setFieldHighlightColor(Utils.color2ColorPt(Color.YELLOW))
//            } else {
//                dropDownAnnotation.setBackgroundColor(optionalColorPt, 3)
//                getPDFViewCtrl().setFieldHighlightColor(optionalColorPt)
//            }
//            if (focused) {
        dropDownAnnotation.setBorderColor(Utils.color2ColorPt(Color.BLUE), 3)
//            } else {
//                dropDownAnnotation.setBorderColor(requiredColorPt, 3)
//            }
//            if (error) {
//                dropDownAnnotation.setBorderColor(errorColorPt, 3)
//            }
        dropDownAnnotation.setTextColor(Utils.color2ColorPt(Color.BLACK), 3)
        dropDownAnnotation.borderStyle =
                Annot.BorderStyle(Annot.BorderStyle.e_solid, 5, 2, 2)
        dropDownAnnotation.fontSize = 16.0
        val optionsTextList = ArrayList<String>()
        optionsTextList.add("Option1")
        optionsTextList.add("Option2")
        optionsTextList.add("Option3")
        val optionsTexts = optionsTextList.toTypedArray()
        if (dropDownAnnotation.options != null && dropDownAnnotation.options.isNotEmpty()) {
            dropDownAnnotation.replaceOptions(optionsTexts)
        } else {
            dropDownAnnotation.addOptions(optionsTexts)
        }
        dropDownAnnotation.refreshAppearance()
        val page1 = pdfDoc.getPage(1)
        page1.annotPushBack(dropDownAnnotation)
        pdfViewCtrl.update(dropDownAnnotation, 1)
    } catch (exception: PDFNetException) {
        exception.printStackTrace()
    }
}