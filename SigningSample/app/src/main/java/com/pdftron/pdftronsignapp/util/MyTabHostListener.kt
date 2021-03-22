package com.pdftron.pdftronsignapp.util

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2
import com.pdftron.pdf.model.FileInfo

class MyTabHostListener: PdfViewCtrlTabHostFragment2.TabHostListener {
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
        if (item?.itemId == CustomButtonId.PROFILE) {
            Toast.makeText(item.actionView.context, "ToDo: change user", Toast.LENGTH_SHORT).show()
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

    }
}