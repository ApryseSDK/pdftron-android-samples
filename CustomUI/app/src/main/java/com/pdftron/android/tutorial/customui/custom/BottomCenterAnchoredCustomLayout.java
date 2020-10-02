package com.pdftron.android.tutorial.customui.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.CustomRelativeLayout;

/**
 * A CustomRelativeLayout that is anchored on the bottom middle if zoom with parent is false.
 */
public class BottomCenterAnchoredCustomLayout extends CustomRelativeLayout {

    public BottomCenterAnchoredCustomLayout(Context context, PDFViewCtrl parent, double x, double y, int page_num) {
        super(context, parent, x, y, page_num);
    }

    public BottomCenterAnchoredCustomLayout(Context context) {
        super(context);
    }

    public BottomCenterAnchoredCustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomCenterAnchoredCustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BottomCenterAnchoredCustomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mParentView == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mScreenPt = mParentView.convPagePtToHorizontalScrollingPt(mPagePosLeft, mPagePosBottom, mPageNum);
        int del = 0;
        if (mZoomWithParent) {
            double[] screenBounds = mParentView.convPagePtToHorizontalScrollingPt(mPagePosRight, mPagePosTop, mPageNum);

            width = (int) (Math.abs(screenBounds[0] - mScreenPt[0]) + .5);
            height = (int) (Math.abs(screenBounds[1] - mScreenPt[1]) + .5);
            int nextWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int nextHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(nextWidthMeasureSpec, nextHeightMeasureSpec);
        } else {
            double[] screenBounds = mParentView.convPagePtToHorizontalScrollingPt(mPagePosRight, mPagePosTop, mPageNum);
            int scaledWidth = (int) (Math.abs(screenBounds[0] - mScreenPt[0]) + .5);
            del = (scaledWidth - width) / 2;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        if (mParentView.isMaintainZoomEnabled()) {
            int dx = (mParentView.isCurrentSlidingCanvas(mPageNum) ? 0 : mScrollOffsetX) - mParentView.getScrollXOffsetInTools(mPageNum);
            int dy = (mParentView.isCurrentSlidingCanvas(mPageNum) ? 0 : mScrollOffsetY) - mParentView.getScrollYOffsetInTools(mPageNum);

            setTranslationX(-dx);
            setTranslationY(dy);
        }

        int l = (int) mScreenPt[0] + del;
        int t = (int) mScreenPt[1] - height;
        int r = (int) mScreenPt[0] + width + del;
        int b = (int) mScreenPt[1];
        layout(l, t, r, b);
    }
}
