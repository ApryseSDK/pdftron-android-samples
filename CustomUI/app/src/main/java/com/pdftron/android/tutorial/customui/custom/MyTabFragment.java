package com.pdftron.android.tutorial.customui.custom;

import android.view.MotionEvent;
import android.widget.Toast;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.tools.Pan;

public class MyTabFragment extends PdfViewCtrlTabFragment {
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        boolean handled = false;

        if (mToolManager != null &&
                mToolManager.getTool() != null &&
                (mToolManager.getTool() instanceof Pan)) {
            // try select text
            boolean shouldUnlockRead = false;
            try {
                //locks the document first as accessing annotation/doc information isn't thread safe.
                mPdfViewCtrl.docLockRead();
                shouldUnlockRead = true;

                float x1, x2, y1, y2;
                x1 = x2 = e.getX();
                y1 = y2 = e.getY();

                float delta = 0.01f;
                x2 += delta;
                y2 += delta;
                delta *= 2;
                x1 = x2 - delta >= 0 ? x2 - delta : 0;
                y1 = y2 - delta >= 0 ? y2 - delta : 0;

                boolean selected = mPdfViewCtrl.selectByRect(x1, y1, x2, y2);

                if (selected) {
                    int sel_pg_begin = mPdfViewCtrl.getSelectionBeginPage();
                    int sel_pg_end = mPdfViewCtrl.getSelectionEndPage();

                    for (int pg = sel_pg_begin; pg <= sel_pg_end; ++pg) {
                        PDFViewCtrl.Selection sel = mPdfViewCtrl.getSelection(pg);
                        String text = sel.getAsUnicode();

                        Toast.makeText(getContext(), "Tapped text: " + text, Toast.LENGTH_SHORT).show();
                        handled = true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (shouldUnlockRead) {
                    mPdfViewCtrl.docUnlockRead();
                }
            }
        }

        if (!handled) {
            return super.onSingleTapConfirmed(e);
        }
        return true;
    }
}
