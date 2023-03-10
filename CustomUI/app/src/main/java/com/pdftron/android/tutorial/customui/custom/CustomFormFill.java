package com.pdftron.android.tutorial.customui.custom;

import android.view.Gravity;
import android.view.View;
import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.FormFill;
import com.pdftron.pdf.widget.AutoScrollEditor;

public class CustomFormFill extends FormFill {
    public CustomFormFill(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    protected void styleAutoScrollEditorBeforeSetText(AutoScrollEditor autoScrollEditor) throws PDFNetException {
        super.styleAutoScrollEditorBeforeSetText(autoScrollEditor);

        // test code start
        if (mAnnot != null) {
            String verticalAlign = mAnnot.getCustomData("demo-custom-align");
            if (verticalAlign != null && verticalAlign.equals("top-center")) {
                autoScrollEditor.getEditText().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                autoScrollEditor.getEditText().setVerticalTextAlignment(Gravity.TOP);
            }
        }
        // test code end
    }
}
