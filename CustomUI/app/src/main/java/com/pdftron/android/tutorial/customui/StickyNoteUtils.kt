package com.pdftron.android.tutorial.customui

import com.pdftron.pdf.*
import com.pdftron.pdf.annots.Text
import com.pdftron.pdf.utils.Utils
import java.io.File

fun applyCustomAppearanceToStickyNote(
    pdfViewCtrl: PDFViewCtrl,
    stickyNote: Text,
    pageNum: Int,
    count: Int
) {
    val doc = pdfViewCtrl.doc
    val writer = ElementWriter()
    val builder = ElementBuilder()
    writer.begin(doc)
    val imageFile: File =
        Utils.copyResourceToLocal(pdfViewCtrl.context, R.raw.logo, "image", ".png")
    val image = Image.create(doc, imageFile.absolutePath)
    val w = image.imageWidth
    val h = image.imageHeight
    val element = builder.createImage(image, 0.0, 0.0, w.toDouble(), h.toDouble())
    writer.writePlacedElement(element)

    val bbox = element.bBox

    val textHeight = h.toDouble() / 2.0
    var textElement = builder.createTextBegin(Font.create(doc, Font.e_times_roman), textHeight)
    writer.writeElement(textElement)
    textElement = builder.createTextRun("$count")
    textElement.setTextMatrix(1.0, 0.0, 0.0, 1.0, w.toDouble() - (textHeight/2.0), h.toDouble() - (textHeight/2.0))
    val gstate = textElement.gState
    gstate.fillColorSpace = ColorSpace.createDeviceRGB()
    gstate.fillColor = ColorPt(1.0, 0.0, 0.0) // red
    writer.writeElement(textElement)
    writer.writeElement(builder.createTextEnd())

    val newAppearanceStream = writer.end()
    newAppearanceStream.putRect(
        "BBox",
        bbox.x1,
        bbox.y1,
        bbox.x2 + (textHeight/2.0),
        bbox.y2 + (textHeight/2.0)
    )
    stickyNote.appearance = newAppearanceStream
    pdfViewCtrl.update(stickyNote, pageNum)
}