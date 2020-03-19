package com.pdftron.android.tutorial.customui.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.LongSparseArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdftron.android.tutorial.customui.R;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.tools.CustomRelativeLayout;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;

import java.util.ArrayList;

/**
 * Delegate class that adds a custom view on top of a link annotation when the annotation
 * is selected.
 */
public class CustomLinkClick extends CustomizationDelegate {
    private LongSparseArray<CustomRelativeLayout> mLinkOverlayMap = new LongSparseArray<>();

    private @Nullable
    PDFViewCtrl mPdfViewCtrl;

    public CustomLinkClick(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment tabHostFragment) {
        super(context, tabHostFragment);
    }

    @Override
    public void applyCustomization(@NonNull PdfViewCtrlTabFragment tabFragment) {
//        customizeLinkClick(mContext, tabFragment, mLinkOverlayMap);

        mPdfViewCtrl = tabFragment.getPDFViewCtrl();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.addPageChangeListener(new PDFViewCtrl.PageChangeListener() {
                @Override
                public void onPageChange(int i, int i1, PDFViewCtrl.PageChangeState pageChangeState) {
                    if (pageChangeState == PDFViewCtrl.PageChangeState.END) {
                        try {
                            setSignaturesBorder(i1);
                        } catch (PDFNetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void setSignaturesBorder(int current_page) throws PDFNetException {
        if (mPdfViewCtrl == null) {
            return;
        }

        for (int i = 0; i < mLinkOverlayMap.size(); i++) {
            long annotNum = mLinkOverlayMap.keyAt(i);
            CustomRelativeLayout overlayView = mLinkOverlayMap.get(annotNum);
            mPdfViewCtrl.removeView(overlayView);
        }
        if (mLinkOverlayMap.size() > 0) {
            mLinkOverlayMap.clear();
        }

        ArrayList<Annot> annots = mPdfViewCtrl.getAnnotationsOnPage(current_page);
        for (Annot annot : annots) {
            if (annot == null || !annot.isValid()) {
                return;
            }
            int type = annot.getType();
            if (type == Annot.e_Widget) {
                Widget w = new Widget(annot);
                int fieldType = w.getField().getType();
                if (fieldType == Field.e_signature) {
                    // found signature
                    long annotObjNum = annot.getSDFObj().getObjNum();
                    int signatureBorderWidth = 9;
                    // Create the custom border view and add it to PDFViewCtrl
                    Context context = mPdfViewCtrl.getContext();
                    CustomRelativeLayout overlay = new CustomRelativeLayout(context);
                    GradientDrawable square_drawable = (GradientDrawable) mPdfViewCtrl.getContext().getResources().getDrawable(R.drawable.signature_field_border);
                    square_drawable.setStroke(signatureBorderWidth, Color.RED);
                    overlay.setBackground(square_drawable);
                    overlay.setAnnot(mPdfViewCtrl, annot, current_page);
                    overlay.setZoomWithParent(true);
                    mPdfViewCtrl.addView(overlay);
                    mLinkOverlayMap.put(annotObjNum, overlay);
                }
            }
        }
    }

    private static void customizeLinkClick(@NonNull final Context context,
            @NonNull final PdfViewCtrlTabFragment tabFragment,
            @NonNull final LongSparseArray<CustomRelativeLayout> mLinkOverlayMap) {

        final PDFViewCtrl pdfViewCtrl = tabFragment.getPDFViewCtrl();
        final ToolManager toolManager = tabFragment.getToolManager();

        if (pdfViewCtrl != null && toolManager != null) {
            toolManager.setBasicAnnotationListener(new ToolManager.BasicAnnotationListener() {
                @Override
                public void onAnnotationSelected(Annot annot, int i) {
                    tabFragment.onAnnotationSelected(annot, i);
                }

                @Override
                public void onAnnotationUnselected() {
                    tabFragment.onAnnotationUnselected();
                }

                @Override
                public boolean onInterceptAnnotationHandling(@Nullable Annot annot, Bundle bundle, ToolManager.ToolMode toolMode) {
                    // custom link behaviour
                    // instead of jumping to the destination, let's display a flashing view on top of the link
                    try {
                        if (annot != null && annot.isValid() && annot.getType() == Annot.e_Link) {
                            int pageNum = bundle.getInt(Tool.PAGE_NUMBER);

                            // add custom on top of link
                            addCustomViewOnLink(context, mLinkOverlayMap, pdfViewCtrl, annot, pageNum);

                            toolManager.setTool(toolManager.createTool(ToolManager.ToolMode.PAN, null));
                            pdfViewCtrl.invalidate();
                            return true;
                        }
                    } catch (PDFNetException e) {
                        e.printStackTrace();
                    }

                    return tabFragment.onInterceptAnnotationHandling(annot, bundle, toolMode);
                }

                @Override
                public boolean onInterceptDialog(AlertDialog alertDialog) {
                    return tabFragment.onInterceptDialog(alertDialog);
                }
            });
        }
    }

    private static void addCustomViewOnLink(@NonNull Context context,
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
            overlay.setBackgroundColor(context.getResources().getColor(R.color.orange));
            overlay.setAnnot(pdfViewCtrl, annot, pageNum);
            overlay.setZoomWithParent(true);
            pdfViewCtrl.addView(overlay);
            linkedOverlayMap.put(annotObjNum, overlay);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
