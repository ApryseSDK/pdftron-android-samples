package com.pdftron.android.tutorial.customui.custom;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.pdftron.android.tutorial.customui.R;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment2;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.tools.CustomRelativeLayout;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.util.ArrayList;
import java.util.Map;

public class CustomWidget extends CustomizationDelegate {

    private final LongSparseArray<CustomRelativeLayout> mOverlayMap = new LongSparseArray<>();

    public CustomWidget(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment2 tabHostFragment) {
        super(context, tabHostFragment);
    }

    @Override
    protected void applyCustomization(@NonNull PdfViewCtrlTabFragment2 tabFragment) {
        super.applyCustomization(tabFragment);

        // apply initial red box
        PDFViewCtrl pdfViewCtrl = tabFragment.getPDFViewCtrl();
        boolean shouldUnlockRead = false;
        try {
            pdfViewCtrl.docLockRead();
            shouldUnlockRead = true;

            int pageCount = pdfViewCtrl.getPageCount();
            for (int i = 1; i <= pageCount; i++) {
                ArrayList<Annot> annots = pdfViewCtrl.getAnnotationsOnPage(i);
                for (Annot annot : annots) {
                    if (annot.isValid() && annot.getType() == Annot.e_Widget) {
                        Widget widget = new Widget(annot);
                        Field field = widget.getField();
                        boolean isRequired = field.getFlag(Field.e_required);
                        if (isRequired) {
                            addCustomViewOnWidget(pdfViewCtrl.getContext(), mOverlayMap, pdfViewCtrl, annot, i);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (shouldUnlockRead) {
                pdfViewCtrl.docUnlockRead();
            }
        }

        // green as user fill it
        ToolManager tm = tabFragment.getToolManager();
        tm.addAnnotationModificationListener(new ToolManager.AnnotationModificationListener() {
            @Override
            public void onAnnotationsAdded(Map<Annot, Integer> annots) {

            }

            @Override
            public void onAnnotationsPreModify(Map<Annot, Integer> annots) {

            }

            @Override
            public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra) {
                try {
                    for (Annot annot : annots.keySet()) {
                        if (annot.isValid() && annot.getType() == Annot.e_Widget) {
                            changeBorderColor(mOverlayMap, annot);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onAnnotationsPreRemove(Map<Annot, Integer> annots) {

            }

            @Override
            public void onAnnotationsRemoved(Map<Annot, Integer> annots) {

            }

            @Override
            public void onAnnotationsRemovedOnPage(int pageNum) {

            }

            @Override
            public void annotationsCouldNotBeAdded(String errorMessage) {

            }
        });
    }

    private static void changeBorderColor(@NonNull LongSparseArray<CustomRelativeLayout> linkedOverlayMap, @NonNull Annot annot) throws PDFNetException {
        long annotObjNum = annot.getSDFObj().getObjNum();
        CustomRelativeLayout layout = linkedOverlayMap.get(annotObjNum);
        if (layout != null) {
            MaterialCardView cardView = layout.findViewById(R.id.card_view);
            Widget widget = new Widget(annot);
            Field field = widget.getField();
            String fieldValue = field.getValueAsString();
            if (Utils.isNullOrEmpty(fieldValue)) {
                cardView.setStrokeColor(Color.RED);
            } else {
                cardView.setStrokeColor(Color.GREEN);
            }
        }
    }

    private static void addCustomViewOnWidget(@NonNull Context context,
            @NonNull LongSparseArray<CustomRelativeLayout> linkedOverlayMap,
            @NonNull PDFViewCtrl pdfViewCtrl,
            @NonNull Annot annot,
            int pageNum) {
        try {
            long annotObjNum = annot.getSDFObj().getObjNum();
            if (linkedOverlayMap.get(annotObjNum) != null) {
                // already added
                return;
            }
            CustomRelativeLayout overlay = new CustomRelativeLayout(context);
            View view = LayoutInflater.from(context).inflate(R.layout.widget_border, overlay);
            overlay.setAnnot(pdfViewCtrl, annot, pageNum);
            overlay.setZoomWithParent(true);
            pdfViewCtrl.addView(overlay);
            linkedOverlayMap.put(annotObjNum, overlay);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
