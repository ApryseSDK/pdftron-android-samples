package com.pdftron.pdftronsignapp.listeners

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2
import com.pdftron.pdf.model.FileInfo
import com.pdftron.pdf.tools.Tool
import com.pdftron.pdftronsignapp.customtool.SelectDate
import com.pdftron.pdftronsignapp.util.CustomButtonId

class MyTabHostListener(
    private val showBottomBar: () -> Unit,
    private val mPdfViewCtrlTabHostFragment: PdfViewCtrlTabHostFragment2,
    private val mBasicAnnotationListener: MyBasicAnnotationListener
) : PdfViewCtrlTabHostFragment2.TabHostListener {
    override fun onTabHostShown() {

    }

    override fun onTabHostHidden() {

    }

    override fun onLastTabClosed() {

    }

    override fun onTabChanged(p0: String?) {

    }

    override fun onOpenDocError(): Boolean {
        return true
    }

    override fun onNavButtonPressed() {

    }

    override fun onShowFileInFolder(p0: String?, p1: String?, p2: Int) {

    }

    override fun canShowFileInFolder(): Boolean {
        return true
    }

    override fun canShowFileCloseSnackbar(): Boolean {
        return true
    }

    override fun onToolbarCreateOptionsMenu(item: Menu?, p1: MenuInflater?): Boolean {
        return true
    }

    override fun onToolbarPrepareOptionsMenu(p0: Menu?): Boolean {
        return true
    }

    override fun onToolbarOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == CustomButtonId.DATE) {
            val toolManager = mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.toolManager
            val tool = toolManager!!.createTool(SelectDate.MODE, null)
            (tool as Tool).isForceSameNextToolMode = true
            toolManager.tool = tool
            return true
        }
        return false
    }

    override fun onStartSearchMode() {

    }

    override fun onExitSearchMode() {

    }

    override fun canRecreateActivity(): Boolean {
        return true
    }

    override fun onTabPaused(p0: FileInfo?, p1: Boolean) {

    }

    override fun onJumpToSdCardFolder() {

    }

    override fun onTabDocumentLoaded(p0: String?) {
        mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.toolManager.setBasicAnnotationListener(
            mBasicAnnotationListener
        )
        showBottomBar()
    }
}