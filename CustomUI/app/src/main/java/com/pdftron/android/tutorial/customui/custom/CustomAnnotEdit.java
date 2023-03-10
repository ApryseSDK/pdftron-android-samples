package com.pdftron.android.tutorial.customui.custom;

import android.view.Gravity;
import android.view.View;
import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.TextWidget;
import com.pdftron.pdf.tools.AnnotEdit;
import com.pdftron.pdf.utils.InlineEditText;

public class CustomAnnotEdit extends AnnotEdit {
    public CustomAnnotEdit(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    protected void setupTextFieldProperties(@NonNull InlineEditText inlineEditText) throws PDFNetException {
        super.setupTextFieldProperties(inlineEditText);

        // test code start
        if (mAnnot == null) {
            return;
        }
        TextWidget textWidget = new TextWidget(mAnnot);
        String verticalAlign = textWidget.getCustomData("demo-custom-align");
        if (verticalAlign != null && verticalAlign.equals("top-center")) {
            inlineEditText.getEditText().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            inlineEditText.getEditText().setVerticalTextAlignment(Gravity.TOP);
        }
        // test code end
    }
}
