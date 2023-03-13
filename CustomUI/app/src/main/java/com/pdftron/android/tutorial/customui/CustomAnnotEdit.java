package com.pdftron.android.tutorial.customui;

import android.view.MotionEvent;
import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.AnnotEdit;

public class CustomAnnotEdit extends AnnotEdit {
    /**
     * Class constructor
     *
     * @param ctrl
     */
    public CustomAnnotEdit(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public boolean onMove(MotionEvent e1, MotionEvent e2, float x_dist, float y_dist) {
        try {
            if (mAnnot != null && mAnnot.getType() == Annot.e_Ink) {
                return false;
            }
        } catch (PDFNetException e) {
            e.printStackTrace();
        }
        return super.onMove(e1, e2, x_dist, y_dist);
    }
}
