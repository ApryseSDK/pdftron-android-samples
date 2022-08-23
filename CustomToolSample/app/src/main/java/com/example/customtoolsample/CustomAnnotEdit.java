package com.example.customtoolsample;

import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.FreeText;
import com.pdftron.pdf.tools.AnnotEdit;
import com.pdftron.pdf.utils.AnnotUtils;
import com.pdftron.pdf.widget.AutoScrollEditText;

public class CustomAnnotEdit extends AnnotEdit {
    public CustomAnnotEdit(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    protected boolean editAnnotSize(PDFViewCtrl.PriorEventMode priorEventMode) {
        boolean result = super.editAnnotSize(priorEventMode);
        raiseAnnotationPreModifyEvent(mAnnot, mAnnotPageNum);
        if(mAnnotView!=null) {
            AutoScrollEditText editText = mAnnotView.getTextView();
            if (editText != null) {
                String string = "Left|Top|Right|Bottom\n" + editText.getLeft() + "|" + editText.getTop() + "|" + editText.getRight() + "|" + editText.getBottom();
                editText.setText(string); // for demo purpose only
                editText.setTextSize(16); // Add your logic to set Text size
                try {
                    FreeText freeText = new FreeText(mAnnot);
                    freeText.setContents(string); // for demo purpose only
                    freeText.setFontSize(16); // Add your logic to set Text size
                    AnnotUtils.createCustomFreeTextAppearance(
                            editText,
                            mPdfViewCtrl,
                            mAnnot,
                            mAnnotPageNum,
                            editText.getBoundingRect()
                    );
                    // reload style as content is changed
                    mAnnotStyle = AnnotUtils.getAnnotStyle(mAnnot);
                } catch (PDFNetException e) {
                    e.printStackTrace();
                }
            }
        }
        raiseAnnotationModifiedEvent(mAnnot, mAnnotPageNum);
        return result;
    }
}
