package com.pdftron.android.tutorial.customui.custom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.PageSet;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.Stamper;
import com.pdftron.pdf.tools.Signature;
import com.pdftron.pdf.tools.ToolManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomSignature extends Signature {

    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.SIGNATURE;

    public CustomSignature(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    protected boolean addSignatureStampToWidget(Page stampPage) {
        try {
            Page timestampedSignaturePage = createTimestampSignature(stampPage);

            // Finally stamp this temporary page to the document as a signature annotation
            return super.addSignatureStampToWidget(timestampedSignaturePage);

        } catch (PDFNetException e) {

        }
        return false;
    }

    @Override
    protected void addSignatureStamp(Page stampPage) {
        try {
            Page timestampedSignaturePage = createTimestampSignature(stampPage);

            // Finally stamp this temporary page to the document as a signature annotation
            super.addSignatureStamp(timestampedSignaturePage);

        } catch (PDFNetException e) {

        }
    }

    Page createTimestampSignature(Page stampPage) throws PDFNetException {

        // Create a temporary page that will contain our signature and timestamp. We make this page
        // a little taller so we can fit in the timestamp without overlapping the signature
        PDFDoc tempDoc = new PDFDoc();
        Page timestampedSignaturePage = tempDoc.pageCreate(new
                Rect(0,
                0,
                stampPage.getPageWidth(),
                stampPage.getPageHeight() + 100)
        );
        tempDoc.pagePushBack(timestampedSignaturePage);

        // Stamp our signature to the temp page
        Stamper sigStamp = new Stamper(Stamper.e_relative_scale, 1.0, 1.0);
        sigStamp.setAlignment(Stamper.e_horizontal_center, Stamper.e_vertical_top);
        sigStamp.stampPage(tempDoc, stampPage, new PageSet(1));

        // Stamp our date/time to the temp page
        Stamper timeStamp = new Stamper(Stamper.e_relative_scale, 1.0, 1.0);
        timeStamp.setAlignment(Stamper.e_horizontal_center, Stamper.e_vertical_bottom);

        DateFormat df = new SimpleDateFormat("MMM d yyyy, HH:mm:ss");
        String date = df.format(Calendar.getInstance().getTime());

        timeStamp.stampText(tempDoc, date, new PageSet(1));

        return timestampedSignaturePage;
    }
}
