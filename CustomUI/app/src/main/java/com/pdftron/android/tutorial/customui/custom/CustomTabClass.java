package com.pdftron.android.tutorial.customui.custom;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.controls.CustomThumbnailsViewFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabBaseFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;

public class CustomTabClass extends PdfViewCtrlTabHostFragment2 {
    @Override
    public void onPageThumbnailOptionSelected(boolean thumbnailEditMode, Integer checkedItem) {
        FragmentActivity activity = getActivity();
        PdfViewCtrlTabBaseFragment currentFragment = getCurrentPdfViewCtrlFragment();
        if (activity == null || currentFragment == null) {
            return;
        }

        final PDFViewCtrl pdfViewCtrl = currentFragment.getPDFViewCtrl();
        if (pdfViewCtrl == null) {
            return;
        }

        // keep previously selected mode
        // display thumbnails view control
        if (checkTabConversionAndAlert(com.pdftron.pdf.tools.R.string.cant_edit_while_converting_message, true)) {
            return;
        }

        currentFragment.save(false, true, false);
        pdfViewCtrl.pause();

        boolean readonly = currentFragment.isTabReadOnly();
        if (!readonly) {
            if (mViewerConfig != null && !mViewerConfig.isThumbnailViewEditingEnabled()) {
                // if document is editable, user can specify if a particular control is editable
                readonly = true;
            }
            if (!pageThumbnailEditingEnabled()) {
                // for extended classes
                readonly = true;
            }
        }
        mThumbFragment = CustomThumbnailsViewFragment.newInstance();
        mThumbFragment.setPdfViewCtrl(pdfViewCtrl);
        mThumbFragment.setOnExportThumbnailsListener(this);
        mThumbFragment.setOnThumbnailsViewDialogDismissListener(this);
        mThumbFragment.setOnThumbnailsEditAttemptWhileReadOnlyListener(this);
        mThumbFragment.setStyle(DialogFragment.STYLE_NO_TITLE, mThemeProvider.getTheme());
        mThumbFragment.setTitle(getString(com.pdftron.pdf.tools.R.string.pref_viewmode_thumbnails_title));
        if (checkedItem != null) {
            mThumbFragment.setItemChecked(checkedItem - 1);
        }

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            mThumbFragment.show(fragmentManager, "thumbnails_fragment");
        }
    }
}
