package com.example.customtoolsample;

import androidx.annotation.NonNull;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.TextFieldCreate;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AnnotUtils;

import java.io.File;

public class CustomStamp extends TextFieldCreate {

    // Since this tool creates polygon annotation, use Annot.e_Polygon as parameter.
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.FORM_TEXT_FIELD_CREATE;

    public CustomStamp(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return MODE;
    }


    @Override
    protected void setAnnot(Annot annot, int pageNum) {
        super.setAnnot(annot, pageNum);
        AnnotUtils.refreshCustomFreeTextAppearance(new File(mPdfViewCtrl.getContext().getFilesDir(), "output.pdf"), mAnnot);
    }
}
