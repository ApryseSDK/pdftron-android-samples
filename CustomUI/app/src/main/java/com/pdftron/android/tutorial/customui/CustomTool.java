package com.pdftron.android.tutorial.customui;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.tools.RectCreate;
import com.pdftron.pdf.tools.ToolManager;

public class CustomTool extends RectCreate {

    // Since this tool creates rect annotation, use Annot.e_Square as parameter.
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.addNewMode(Annot.e_Square);

    public CustomTool(@androidx.annotation.NonNull PDFViewCtrl ctrl) {
        super(ctrl);
        mPageBoundaryRestricted = false;
    }

    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return MODE;
    }

    @Override
    protected Annot createMarkup(PDFDoc doc, Rect bbox) throws PDFNetException {
        bbox.normalize();

        double x1 = bbox.getX1();
        double y1 = bbox.getY1();
        double x2 = bbox.getX2();
        double y2 = bbox.getY2();
        if (y1 < 0) { // case where annot is created below bottom of page and onto the next page
            if (mDownPageNum < mPdfViewCtrl.getPageCount()) {
                int nextPageNum = mDownPageNum + 1;
                Page nextPage = mPdfViewCtrl.getDoc().getPage(nextPageNum);

                // Create new annot and calculate it's rect for next page
                double[] screenPt1 = mPdfViewCtrl.convPagePtToScreenPt(x1, y1, mDownPageNum);
                double[] screenPt2 = mPdfViewCtrl.convPagePtToScreenPt(x2, y2, mDownPageNum);
                double[] newPagePt1 = mPdfViewCtrl.convScreenPtToPagePt(screenPt1[0], screenPt1[1], nextPageNum);
                double[] newPagePt2 = mPdfViewCtrl.convScreenPtToPagePt(screenPt2[0], screenPt2[1], nextPageNum);
                Rect newbbox = new Rect(newPagePt1[0], newPagePt1[1], newPagePt2[0], newPagePt2[1]);
                Annot newAnnot = super.createMarkup(doc, newbbox);
                setStyle(newAnnot);
                newAnnot.refreshAppearance();
                nextPage.annotPushBack(newAnnot);
                mPdfViewCtrl.update(newAnnot, nextPageNum);
                raiseAnnotationAddedEvent(newAnnot, nextPageNum);
            }
            //... other cases
            // ...
        }

        return super.createMarkup(doc, bbox);
    }
}