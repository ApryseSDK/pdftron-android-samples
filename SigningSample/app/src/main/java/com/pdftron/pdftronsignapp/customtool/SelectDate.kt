package com.pdftron.pdftronsignapp.customtool

import com.pdftron.common.PDFNetException
import com.pdftron.pdf.*
import com.pdftron.pdf.tools.TextFieldCreate
import com.pdftron.pdf.tools.ToolManager
import com.pdftron.sdf.Obj

class SelectDate(ctrl: PDFViewCtrl): TextFieldCreate(ctrl) {

    companion object {
        val MODE: ToolManager.ToolModeBase = ToolManager.ToolMode.addNewMode(Annot.e_Widget)
    }

    override fun getToolMode(): ToolManager.ToolModeBase {
        return MODE
    }

    @Throws(PDFNetException::class)
    override fun createMarkup(pdfDoc: PDFDoc, bbox: Rect?): Annot? {
        val widget = super.createMarkup(pdfDoc, bbox)
        val dateAction: Action = Action.createJavaScript(
            pdfDoc,
            "AFDate_FormatEx(\"d/m/yy\");"
        )
        val aaObj: Obj = widget.sdfObj.putDict("AA")
        aaObj.put("K", dateAction.sdfObj)
        aaObj.put("F", dateAction.sdfObj)
        return widget
    }
}