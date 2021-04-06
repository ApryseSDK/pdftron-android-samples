package com.pdftron.android.tutorial.customui;

import android.content.Context;

import com.pdftron.pdf.controls.PdfViewCtrlTabBaseFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.utils.DialogFragmentTab;
import com.pdftron.pdf.utils.Utils;

/**
 * Our custom PdfViewCtrlTabHostFragment2 class that will have a custom annotation list
 */
public class CustomPdfViewCtrlTabHostFragment extends PdfViewCtrlTabHostFragment2 {

    public CustomPdfViewCtrlTabHostFragment() {
    }

    /**
     * Creates the annotation dialog fragment tab
     *
     * @return The annotation dialog fragment tab
     */
    @Override
    protected DialogFragmentTab createAnnotationDialogTab() {
        final PdfViewCtrlTabBaseFragment currentFragment = getCurrentPdfViewCtrlFragment();
        final Context context = getContext();
        if (currentFragment == null || context == null) {
            return null;
        }

        return new DialogFragmentTab(CustomFragment.class,
                "CustomFragment",
                Utils.getDrawable(context, com.pdftron.pdf.tools.R.drawable.ic_annotations_white_24dp),
                null,
                "CustomFragment",
                null,  // can supply a bundle to pass information to the fragment
                0); // can supply a custom menu
    }
}
