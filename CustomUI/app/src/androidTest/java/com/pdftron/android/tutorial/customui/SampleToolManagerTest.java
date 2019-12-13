package com.pdftron.android.tutorial.customui;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFNet;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.tools.ToolManager;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class SampleToolManagerTest {

    private ToolManager toolManager;

    @Test
    public void toolmanager_isNonNull() throws PDFNetException {
        // First initialize PDFTron
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PDFNet.initialize(appContext, R.raw.pdfnet, "test");

        // Use Fragment Scenario to launch our Fragment to the resume state, so all view members are
        // populated.
        //
        // Note any document related tests will probably fail, since we supplied a fake document path
        FragmentScenario<PdfViewCtrlTabFragment> fragmentScenario =
                FragmentScenario.launchInContainer(
                        PdfViewCtrlTabFragment.class,
                        PdfViewCtrlTabFragment.createBasicPdfViewCtrlTabBundle(appContext, Uri.parse("Test.pdf"), ""),
                        R.style.CustomAppTheme,
                        null
                );

        // Perform some tests on the fragment
        fragmentScenario.onFragment(new FragmentScenario.FragmentAction<PdfViewCtrlTabFragment>() {
            @Override
            public void perform(@NonNull PdfViewCtrlTabFragment fragment) {
                toolManager = ToolManagerBuilder.from()
                        .setEditInk(true)
                        .setOpenToolbar(true)
                        .setBuildInPageIndicator(true)
                        .setCopyAnnot(true)
                        .setAnnotPermission(true)
                        .disableToolModes(new ToolManager.ToolMode[]{
                                ToolManager.ToolMode.AREA_MEASURE_CREATE,
                                ToolManager.ToolMode.ARROW_CREATE,
                                ToolManager.ToolMode.CALLOUT_CREATE,
                                ToolManager.ToolMode.FORM_CHECKBOX_CREATE,
                                ToolManager.ToolMode.CLOUD_CREATE,
                                ToolManager.ToolMode.DIGITAL_SIGNATURE,
                                ToolManager.ToolMode.FILE_ATTACHMENT_CREATE,
                                ToolManager.ToolMode.TEXT_CREATE,
                                ToolManager.ToolMode.LINE_CREATE,
                                ToolManager.ToolMode.OVAL_CREATE,
                                ToolManager.ToolMode.PERIMETER_MEASURE_CREATE,
                                ToolManager.ToolMode.POLYGON_CREATE,
                                ToolManager.ToolMode.POLYLINE_CREATE,
                                ToolManager.ToolMode.RECT_CREATE,
                                ToolManager.ToolMode.RECT_LINK,
                                ToolManager.ToolMode.RUBBER_STAMPER,
                                ToolManager.ToolMode.RULER_CREATE,
                                ToolManager.ToolMode.SIGNATURE,
                                ToolManager.ToolMode.FORM_SIGNATURE_CREATE,
                                ToolManager.ToolMode.SOUND_CREATE,
                                ToolManager.ToolMode.FORM_TEXT_FIELD_CREATE,
                                ToolManager.ToolMode.TEXT_LINK_CREATE,
                                ToolManager.ToolMode.TEXT_SQUIGGLY,
                                ToolManager.ToolMode.STAMPER,
                                ToolManager.ToolMode.ANNOT_EDIT_RECT_GROUP,
                                ToolManager.ToolMode.INK_ERASER
                        })
                        .build(fragment);
                assertNotNull(toolManager);
                assertNotNull(fragment.getToolManager());
                // ... other tests for PdfViewCtrlTabFragment
            }
        });
    }
}
