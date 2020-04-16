package com.pdftron.android.tutorial.customui.custom;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.LongSparseArray;

import com.pdftron.android.tutorial.customui.R;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.tools.CustomRelativeLayout;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;

import java.util.Map;

/**
 * Delegate class that adds a custom view on top of a link annotation when the annotation
 * is selected.
 */
public class CustomLinkClick extends CustomizationDelegate {
    private LongSparseArray<CustomRelativeLayout> mLinkOverlayMap = new LongSparseArray<>();

    public CustomLinkClick(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment tabHostFragment) {
        super(context, tabHostFragment);
    }

    @Override
    public void applyCustomization(@NonNull PdfViewCtrlTabFragment tabFragment) {
        customizeLinkClick(mContext, tabFragment, mLinkOverlayMap);
    }

    private static void customizeLinkClick(@NonNull final Context context,
            @NonNull final PdfViewCtrlTabFragment tabFragment,
            @NonNull final LongSparseArray<CustomRelativeLayout> mLinkOverlayMap) {

        final PDFViewCtrl pdfViewCtrl = tabFragment.getPDFViewCtrl();
        final ToolManager toolManager = tabFragment.getToolManager();

        if (pdfViewCtrl != null && toolManager != null) {
            toolManager.addAnnotationModificationListener(new ToolManager.AnnotationModificationListener() {
                @Override
                public void onAnnotationsAdded(Map<Annot, Integer> map) {

                }

                @Override
                public void onAnnotationsPreModify(Map<Annot, Integer> map) {

                }

                @Override
                public void onAnnotationsModified(Map<Annot, Integer> map, Bundle bundle) {
                    for (Map.Entry<Annot, Integer> entry : map.entrySet()) {
                        boolean shouldUnlock = false;
                        try {
                            pdfViewCtrl.docLock(true);
                            shouldUnlock = true;

                            Annot annot = entry.getKey();
                            int pageNum = entry.getValue();

                            int newPageNum = pageNum + 1;

                            Page oldPage = pdfViewCtrl.getDoc().getPage(pageNum);
                            Page newPage = pdfViewCtrl.getDoc().getPage(newPageNum);

                            Annot pp = null;
                            if (annot.isMarkup()) {
                                pp = new Markup(annot).getPopup();
                            }
                            oldPage.annotRemove(annot);
                            newPage.annotPushBack(annot);
                            if (pp != null) {
                                oldPage.annotRemove(pp);
                                newPage.annotPushBack(pp);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            if (shouldUnlock) {
                                pdfViewCtrl.docUnlock();
                            }
                        }
                    }
                    toolManager.deselectAll();
                    try {
                        pdfViewCtrl.update(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onAnnotationsPreRemove(Map<Annot, Integer> map) {

                }

                @Override
                public void onAnnotationsRemoved(Map<Annot, Integer> map) {

                }

                @Override
                public void onAnnotationsRemovedOnPage(int i) {

                }

                @Override
                public void annotationsCouldNotBeAdded(String s) {

                }
            });

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
