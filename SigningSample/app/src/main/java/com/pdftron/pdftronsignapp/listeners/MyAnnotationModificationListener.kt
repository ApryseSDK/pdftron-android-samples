package com.pdftron.pdftronsignapp.listeners

import android.os.Bundle
import com.pdftron.pdf.Annot
import com.pdftron.pdf.tools.ToolManager

class MyAnnotationModificationListener(val annotationsAdded:(p0: MutableMap<Annot, Int>?)->Unit): ToolManager.AnnotationModificationListener {
    override fun onAnnotationsAdded(p0: MutableMap<Annot, Int>?) {
        annotationsAdded(p0)
    }

    override fun onAnnotationsPreModify(p0: MutableMap<Annot, Int>?) {

    }

    override fun onAnnotationsModified(p0: MutableMap<Annot, Int>?, p1: Bundle?) {

    }

    override fun onAnnotationsPreRemove(p0: MutableMap<Annot, Int>?) {

    }

    override fun onAnnotationsRemoved(p0: MutableMap<Annot, Int>?) {

    }

    override fun onAnnotationsRemovedOnPage(p0: Int) {

    }

    override fun annotationsCouldNotBeAdded(p0: String?) {

    }
}