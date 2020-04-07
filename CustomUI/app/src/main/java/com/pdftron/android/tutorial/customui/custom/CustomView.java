package com.pdftron.android.tutorial.customui.custom;

import android.content.Context;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.pdftron.android.tutorial.customui.R;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.tools.CustomRelativeLayout;
import com.pdftron.pdf.tools.ToolManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomView extends CustomizationDelegate {

    // Maps page to custom layout
    HashMap<Integer, List<CustomRelativeLayout>> customLayoutPageMap = new HashMap<>();

    public CustomView(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment tabHostFragment) {
        super(context, tabHostFragment);
    }

    @Override
    public void applyCustomization(@NonNull PdfViewCtrlTabFragment tabFragment) {
        customizeClick(mContext, tabFragment);
    }

    private void customizeClick(@NonNull final Context context,
            @NonNull final PdfViewCtrlTabFragment tabFragment) {

        final PDFViewCtrl pdfViewCtrl = tabFragment.getPDFViewCtrl();
        final ToolManager toolManager = tabFragment.getToolManager();

        if (pdfViewCtrl != null && toolManager != null) {
            pdfViewCtrl.addPageChangeListener(new PDFViewCtrl.PageChangeListener() {
                @Override
                public void onPageChange(int oldPage, int currentPage, PDFViewCtrl.PageChangeState pageChangeState) {
                    // Hide views in other pages
                    List<CustomRelativeLayout> layoutsToHide = customLayoutPageMap.get(oldPage);
                    if (layoutsToHide != null) {
                        for (CustomRelativeLayout customRelativeLayout : layoutsToHide) {
                            customRelativeLayout.setVisibility(View.GONE);
                        }
                    }
                    // Show views in current page
                    List<CustomRelativeLayout> layoutsToShow = customLayoutPageMap.get(currentPage);
                    if (layoutsToShow != null) {
                        for (CustomRelativeLayout customRelativeLayout : layoutsToShow) {
                            customRelativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            toolManager.setPreToolManagerListener(new ToolManager.PreToolManagerListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                    return tabFragment.onSingleTapConfirmed(motionEvent);
                }

                @Override
                public boolean onMove(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                    return tabFragment.onMove(motionEvent, motionEvent1, v, v1);
                }

                @Override
                public boolean onDown(MotionEvent motionEvent) {
                    return tabFragment.onDown(motionEvent);
                }

                @Override
                public boolean onUp(MotionEvent motionEvent, PDFViewCtrl.PriorEventMode priorEventMode) {
                    return tabFragment.onUp(motionEvent, priorEventMode);
                }

                @Override
                public boolean onScaleBegin(float v, float v1) {
                    return tabFragment.onScaleBegin(v, v1);
                }

                @Override
                public boolean onScale(float v, float v1) {
                    return tabFragment.onScale(v, v1);
                }

                @Override
                public boolean onScaleEnd(float v, float v1) {
                    return tabFragment.onScaleEnd(v, v1);
                }

                @Override
                public boolean onLongPress(MotionEvent motionEvent) {
                    float xScreenPt = motionEvent.getX();
                    float yScreenPt = motionEvent.getY();
                    double[] pagePt = pdfViewCtrl.convScreenPtToPagePt(xScreenPt, yScreenPt);
                    addCustomView(context, pdfViewCtrl, pagePt[0], pagePt[1], pdfViewCtrl.getCurrentPage());
                    return true; // event is handled by us
                }

                @Override
                public void onScrollChanged(int i, int i1, int i2, int i3) {
                    tabFragment.onScrollChanged(i, i1, i2, i3);
                }

                @Override
                public boolean onDoubleTap(MotionEvent motionEvent) {
                    return tabFragment.onDoubleTap(motionEvent);
                }

                @Override
                public boolean onKeyUp(int i, KeyEvent keyEvent) {
                    return tabFragment.onKeyUp(i, keyEvent);
                }
            });
        }
    }

    int index = 1;

    // Adds an orange 50x50 rectangle (sized in PDF page point) with text inside. See here for reference:
    // https://www.pdftron.com/documentation/android/guides/basics/coordinates?searchTerm=coordinate
    private void addCustomView(@NonNull Context context,
            @NonNull PDFViewCtrl pdfViewCtrl,
            double x, // in PDF page point
            double y, // in PDF page point
            int pageNum) {
        try {
            CustomRelativeLayout overlay = new CustomRelativeLayout(context);
            TextView textView = new TextView(context);
            textView.setText(String.valueOf(index++));
            overlay.addView(textView);
            overlay.setBackgroundColor(context.getResources().getColor(R.color.orange));
            overlay.setRect(pdfViewCtrl, new Rect(x - 25, y - 25, x + 25, y + 25), pageNum);
            overlay.setZoomWithParent(true);
            pdfViewCtrl.addView(overlay);

            // Cache out custom views so we can cleanup appropriately if needed
            List<CustomRelativeLayout> customRelativeLayouts = customLayoutPageMap.get(pageNum);
            if (customRelativeLayouts == null) {
                customRelativeLayouts = new ArrayList<>();
            }
            customRelativeLayouts.add(overlay);
            customLayoutPageMap.put(pageNum, customRelativeLayouts);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
