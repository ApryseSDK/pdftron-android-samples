package com.pdftron.android.tutorial.customui.custom;

import android.content.Context;
import androidx.annotation.NonNull;

import com.pdftron.android.tutorial.customui.MainActivity;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

/**
 * Delegate class that adds a custom annotation toolbar to a PdfViewCtrlTabHostFragment. This sample
 * re-arranges items in the annotation toolbar grouping and manually changes the precedence toolbar.
 */
public class CustomAnnotationToolbar extends CustomizationDelegate {

    public CustomAnnotationToolbar(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment2 tabHostFragment) {
        super(context, tabHostFragment);
    }

    @Override
    public void applyCustomization(@NonNull PdfViewCtrlTabHostFragment2 tabHostFragment2) {
        // When document loaded, launch the shapes toolbar
        tabHostFragment2.openToolbarWithTag(MainActivity.SHAPES_TOOLBAR_TAG);
        // Then automatically select the rectangle tool
        tabHostFragment2.selectToolbarButton(DefaultToolbars.ButtonId.SQUARE);

    }
}
