package com.pdftron.android.tutorial.customui;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFNet;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.Circle;
import com.pdftron.pdf.utils.Utils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SampleAnnotTest {

    private static final String CUSTOM_DATA_KEY = "CustomData";
    private static final String CUSTOM_DATA_VALUE = "IsSet";
    private static final String TITLE = "This is a title for the circle";

    private PDFDoc mDoc;
    private Page mTestPage;

    @Before
    public void initializeTest() throws PDFNetException, IOException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PDFNet.initialize(appContext, R.raw.pdfnet, "test");

        File file = Utils.copyResourceToLocal(appContext, R.raw.sample, "sample", ".pdf");

        mDoc = new PDFDoc(file.getAbsolutePath());
        mTestPage = mDoc.pageCreate(new Rect(0, 0, 600, 600));
        mDoc.pagePushBack(mTestPage);

        // Create circle annotation with custom data to test
        Circle circle = Circle.create(mDoc, new Rect(10, 110, 100, 200));
        circle.setTitle(TITLE);
        circle.setColor(new ColorPt(0, 1, 0), 3);
        circle.setInteriorColor(new ColorPt(0, 0, 1), 3);
        circle.setContentRect(new Rect(12, 112, 98, 198));
        circle.setOpacity(0.5);
        circle.setCustomData(CUSTOM_DATA_KEY, CUSTOM_DATA_VALUE);

        mTestPage.annotPushBack(circle);
    }

    @Test
    public void annotAttributes_areCorrect() throws PDFNetException {
        // First check that the Circle annotation exists
        Page testPage = mDoc.getPage(mDoc.getPageCount());
        int numAnnots = testPage.getNumAnnots();
        assertEquals(1, numAnnots);

        Annot annot = testPage.getAnnot(0);
        assertTrue(annot.isValid());
        assertEquals(annot.getType(), Annot.e_Circle);

        // Now we're safe to check the attributes for the Circle annotation
        Circle circle = new Circle(annot);
        assertEquals(circle.getOpacity(), 0.5, 0.001);
        assertEquals(circle.getTitle(), TITLE);
        assertEquals(circle.getCustomData(CUSTOM_DATA_KEY), CUSTOM_DATA_VALUE);
    }
}
