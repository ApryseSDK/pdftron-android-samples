package com.pdftron.android.tutorial.customui.custom;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.dialog.signature.SavedSignaturePickerFragment;
import com.pdftron.pdf.utils.DialogFragmentTab;
import com.pdftron.pdf.utils.Utils;

import java.util.ArrayList;

public class CustomTabHostFragment extends PdfViewCtrlTabHostFragment {

    @Override
    public void onOutlineOptionSelected(int initialTabIndex) {
        final PdfViewCtrlTabFragment currentFragment = getCurrentPdfViewCtrlFragment();
        if (currentFragment == null) {
            return;
        }
        PDFViewCtrl pdfViewCtrl = currentFragment.getPDFViewCtrl();
        if (pdfViewCtrl == null) {
            return;
        }

        // save the current page state for the back button - when the user
        // changes pages through the annotation list or outline
        currentFragment.updateCurrentPageInfo();

        if (currentFragment.isNavigationListShowing()) {
            // Creates the dialog as side sheet
            currentFragment.closeNavigationList();
            return;
        } else {
            // Creates the dialog in full screen mode
            if (mBookmarksDialog != null) {
                mBookmarksDialog.dismiss();
            }
        }
        mBookmarksDialog = createBookmarkDialogFragmentInstance();

        DialogFragmentTab userBookmarkTab = createUserBookmarkDialogTab();
        DialogFragmentTab outlineTab = createOutlineDialogTab();
        DialogFragmentTab annotationTab = createAnnotationDialogTab();
        DialogFragmentTab customTab = createCustomDialogTab();

        ArrayList<DialogFragmentTab> dialogFragmentTabs = new ArrayList<>(3);
        if (userBookmarkTab != null) {
            boolean canAdd = mViewerConfig == null || mViewerConfig.isShowUserBookmarksList();
            if (canAdd) {
                dialogFragmentTabs.add(userBookmarkTab);
            }
        }
        if (outlineTab != null) {
            boolean canAdd = mViewerConfig == null || mViewerConfig.isShowOutlineList();
            if (canAdd) {
                dialogFragmentTabs.add(outlineTab);
            }
        }
        if (annotationTab != null) {
            boolean canAdd = mViewerConfig == null || mViewerConfig.isShowAnnotationsList();
            if (canAdd) {
                dialogFragmentTabs.add(annotationTab);
            }
        }
        dialogFragmentTabs.add(customTab);

        mBookmarksDialog.setPdfViewCtrl(pdfViewCtrl)
                .setDialogFragmentTabs(dialogFragmentTabs, initialTabIndex)
                .setCurrentBookmark(mCurrentBookmark);
        mBookmarksDialog.setBookmarksDialogListener(this);
        mBookmarksDialog.setBookmarksTabsListener(this);
        mBookmarksDialog.setStyle(DialogFragment.STYLE_NO_TITLE, com.pdftron.pdf.tools.R.style.CustomAppTheme);

        if (canOpenNavigationListAsSideSheet()) {
            currentFragment.openNavigationList(mBookmarksDialog, getToolbarHeight(), mSystemWindowInsetBottom);
            mBookmarksDialog = null;
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager != null) {
                mBookmarksDialog.show(fragmentManager, "bookmarks_dialog");
            }
        }

        stopHideToolbarsTimer();
    }

    private DialogFragmentTab createCustomDialogTab() {
        return new DialogFragmentTab(SavedSignaturePickerFragment.class,
                "tab-my-custom-tab",
                Utils.getDrawable(getContext(), com.pdftron.pdf.tools.R.drawable.ic_star_white_24dp),
                null,
                getString(com.pdftron.pdf.tools.R.string.annot_signature_plural),
                null);
    }
}
