package com.pdftron.android.tutorial.customui;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.PageIterator;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.utils.DialogFragmentTab;

import java.util.ArrayList;

public class CustomTabHostClass extends PdfViewCtrlTabHostFragment2{
    private boolean getHasAnnotations() throws PDFNetException {
        int count = 0;
        for (PageIterator itr = getCurrentPdfViewCtrlFragment().getPdfDoc().getPageIterator(); itr.hasNext(); ) {
            Page page = itr.next();
            int num_annots = page.getNumAnnots();
            for (int i = 0; i < num_annots; ++i) {
                Annot annot = page.getAnnot(i);
                if (annot.isValid()) {
                    switch (annot.getType()) {
                        case Annot.e_FreeText:
                            count += 1;
                    }
                }
            }
        }
        return count > 0;
    }
    @Override
    protected ArrayList<DialogFragmentTab> getBookmarksDialogTabs() {
        DialogFragmentTab userBookmarkTab = this.createUserBookmarkDialogTab();
        DialogFragmentTab outlineTab = this.createOutlineDialogTab();
        DialogFragmentTab annotationTab = this.createAnnotationDialogTab();
        ArrayList<DialogFragmentTab> dialogFragmentTabs = new ArrayList(3);
        boolean canAdd;
        if (userBookmarkTab != null) {
            canAdd = this.mViewerConfig == null || this.mViewerConfig.isShowUserBookmarksList();
            if (canAdd) {
                dialogFragmentTabs.add(userBookmarkTab);
            }
        }

        if (outlineTab != null) {
            canAdd = this.mViewerConfig == null || this.mViewerConfig.isShowOutlineList();
            if (canAdd) {
                dialogFragmentTabs.add(outlineTab);
            }
        }

        if (annotationTab != null) {
            canAdd = this.mViewerConfig == null || this.mViewerConfig.isShowAnnotationsList();
            try {
                if (canAdd && getHasAnnotations()) {
                    dialogFragmentTabs.add(annotationTab);
                }
            } catch (PDFNetException e) {
                throw new RuntimeException(e);
            }
        }

        return dialogFragmentTabs;
    }
}
