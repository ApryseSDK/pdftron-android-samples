package com.pdftron.android.tutorial.customui.custom;

import android.view.Gravity;
import android.view.View;
import androidx.annotation.NonNull;

import com.pdftron.android.tutorial.customui.R;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.ViewChangeCollection;
import com.pdftron.pdf.annots.TextWidget;
import com.pdftron.pdf.tools.TextFieldCreate;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.AutoScrollEditor;

public class CustomTextFieldCreate extends TextFieldCreate {
    public CustomTextFieldCreate(PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    protected ColorPt getBackgroundColorPt() throws PDFNetException {
        return Utils.color2ColorPt(mPdfViewCtrl.getContext().getResources().getColor(R.color.tools_link_stroke));
    }

    @Override
    protected double getBackgroundOpacity() {
        return 0.5;
    }

    @Override
    protected Annot createMarkup(@NonNull PDFDoc doc, Rect bbox) throws PDFNetException {
        Annot annot = super.createMarkup(doc, bbox);

        // test code start
        TextWidget widget = new TextWidget(annot);
        Field field = widget.getField();

        widget.setCustomData("demo-custom-align", "top-center");

        field.setJustification(Field.e_centered);

        ViewChangeCollection view_change = field.setValue("THIS IS TEST");
        mPdfViewCtrl.refreshAndUpdate(view_change);
        // test code end

        return annot;
    }

    @Override
    protected void styleAutoScrollEditor(AutoScrollEditor editor) throws Exception {
        super.styleAutoScrollEditor(editor);

        // test code start
        TextWidget w = new TextWidget(mAnnot);
        String str = w.getField().getValueAsString();
        editor.getEditText().setText(str);
        editor.getEditText().setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        editor.getEditText().setVerticalTextAlignment(Gravity.TOP);
        // test code end
    }
}
