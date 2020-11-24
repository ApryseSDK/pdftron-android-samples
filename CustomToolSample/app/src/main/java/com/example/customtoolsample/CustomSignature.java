package com.example.customtoolsample;

import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.model.AnnotStyle;
import com.pdftron.pdf.tools.Signature;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.StampManager;

import java.io.File;
import java.util.ArrayList;

public class CustomSignature extends Signature {

    // Since this tool creates polygon annotation, use Annot.e_Polygon as parameter.
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.addNewMode(AnnotStyle.CUSTOM_ANNOT_TYPE_SIGNATURE);

    public CustomSignature(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return MODE;
    }

    @Override
    public int getCreateAnnotType() {
        return AnnotStyle.CUSTOM_ANNOT_TYPE_SIGNATURE;
    }

    public void signLastSavedSignatureToField(int pageNum) {
        // this method will add the last signed signature to all field on the given page
        boolean shouldUnlock = false;
        try {
            mPdfViewCtrl.docLock(true);
            shouldUnlock = true;
            ArrayList<Annot> annots = mPdfViewCtrl.getAnnotationsOnPage(pageNum);
            for (Annot annot : annots) {
                if (annot.isValid() && annot.getType() == Annot.e_Widget) {
                    Widget w = new Widget(annot);
                    Field field = w.getField();
                    int field_type = field.getType();
                    if (field_type == Field.e_signature) {
                        // found signature field
                        File[] files = StampManager.getInstance().getSavedSignatures(mPdfViewCtrl.getContext());
                        File lastFile = files[files.length - 1];
                        create(lastFile.getAbsolutePath(), w);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (shouldUnlock) {
                mPdfViewCtrl.docUnlock();
            }
        }
        mNextToolMode = ToolManager.ToolMode.PAN;
    }
}
