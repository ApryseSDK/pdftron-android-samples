package com.pdftron.android.tutorial.customui;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.FreeText;
import com.pdftron.pdf.tools.TextFieldCreate;
import com.pdftron.pdf.tools.ToolManager;

public class CustomTool extends TextFieldCreate {
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.FORM_TEXT_FIELD_CREATE;

    public CustomTool(PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    protected ColorPt getBackgroundColorPt() throws PDFNetException {
        return new ColorPt(1, 0, 0);
    }

    @Override
    protected double getBackgroundOpacity() {
        return 0.5d;
    }
}
