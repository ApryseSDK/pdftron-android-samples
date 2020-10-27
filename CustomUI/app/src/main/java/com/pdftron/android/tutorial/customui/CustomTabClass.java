package com.pdftron.android.tutorial.customui;

import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.utils.UserCropUtilities;

public class CustomTabClass extends PdfViewCtrlTabFragment {

    @Override
    public void userCropDialogDismiss() {
        if (hasUserCropBox()) {
            permanentlyCrop(mPdfViewCtrl);
        }
        super.userCropDialogDismiss();
    }

    private static void permanentlyCrop(@NonNull PDFViewCtrl mPdfViewCtrl) {
        boolean shouldUnlock = false;
        try {
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            UserCropUtilities.cropDoc(mPdfViewCtrl.getDoc());
            mPdfViewCtrl.clearSelection();
            mPdfViewCtrl.update(true);
        } catch (PDFNetException e) {
            e.printStackTrace();
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
    }
}
