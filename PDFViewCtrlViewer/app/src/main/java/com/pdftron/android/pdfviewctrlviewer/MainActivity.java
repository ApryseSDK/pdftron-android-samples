package com.pdftron.android.pdfviewctrlviewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.FileAttachment;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.RequestCode;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;
import com.pdftron.pdf.widget.preset.component.PresetBarComponent;
import com.pdftron.pdf.widget.preset.component.PresetBarViewModel;
import com.pdftron.pdf.widget.preset.component.view.PresetBarView;
import com.pdftron.pdf.widget.preset.signature.SignatureViewModel;
import com.pdftron.pdf.widget.toolbar.ToolManagerViewModel;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarComponent;
import com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarViewModel;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;
import com.pdftron.pdf.widget.toolbar.component.view.AnnotationToolbarView;

import java.io.File;

public class MainActivity extends AppCompatActivity implements ToolManager.AdvancedAnnotationListener {
    private static final String TAG = MainActivity.class.getName();

    private PDFViewCtrl mPdfViewCtrl;
    private PDFDoc mPdfDoc;
    private ToolManager mToolManager;
    private AnnotationToolbarComponent mAnnotationToolbarComponent;
    private PresetBarComponent mPresetBarComponent;
    private FrameLayout mToolbarContainer;
    private FrameLayout mPresetContainer;

    protected ToolManager.ToolMode mImageCreationMode;
    protected PointF mAnnotTargetPoint;
    protected android.net.Uri mOutputFileUri;
    protected boolean mImageStampDelayCreation = false;
    protected Intent mAnnotIntentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolManager();
        setupAnnotationToolbar();
        try {
            AppUtils.setupPDFViewCtrl(mPdfViewCtrl);
            viewFromResource(R.raw.sample, "sample_file");
        } catch (PDFNetException e) {
            Log.e(TAG, "Error setting up PDFViewCtrl");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (Activity.RESULT_OK == resultCode) {
            if (requestCode == RequestCode.PICK_PHOTO_CAM) {
                // save the data and process the image stamp
                // after onResume is called.
                if (mImageCreationMode != null) {
                    if (mImageCreationMode == ToolManager.ToolMode.STAMPER) {
                        mImageStampDelayCreation = true;
                        mAnnotIntentData = data;
                    }
                }
            }
        } else {
            if (mToolManager != null && mToolManager.getTool() != null) {
                ((Tool) mToolManager.getTool()).clearTargetPoint();
            }
        }
    }

    /**
     * Helper method to set up and initialize the ToolManager.
     */
    public void setupToolManager() {
        mToolManager.setAdvancedAnnotationListener(this);
    }

    /**
     * Helper method to set up and initialize the AnnotationToolbar.
     */
    public void setupAnnotationToolbar() {
        ToolManagerViewModel toolManagerViewModel = ViewModelProviders.of(this).get(ToolManagerViewModel.class);
        toolManagerViewModel.setToolManager(mToolManager);
        SignatureViewModel signatureViewModel = ViewModelProviders.of(this).get(SignatureViewModel.class);
        PresetBarViewModel presetViewModel = ViewModelProviders.of(this).get(PresetBarViewModel.class);
        AnnotationToolbarViewModel annotationToolbarViewModel = ViewModelProviders.of(this).get(AnnotationToolbarViewModel.class);

        // Create our UI components for the annotation toolbar annd preset bar
        mAnnotationToolbarComponent = new AnnotationToolbarComponent(
                this,
                annotationToolbarViewModel,
                presetViewModel,
                toolManagerViewModel,
                new AnnotationToolbarView(mToolbarContainer)
        );

        mPresetBarComponent = new PresetBarComponent(
                this,
                getSupportFragmentManager(),
                presetViewModel,
                toolManagerViewModel,
                signatureViewModel,
                new PresetBarView(mPresetContainer)
        );

        // Create our custom toolbar and pass it to the annotation toolbar UI component
        mAnnotationToolbarComponent.inflateWithBuilder(
                AnnotationToolbarBuilder.withTag("Custom Toolbar")
                        .addToolButton(ToolbarButtonType.SQUARE, DefaultToolbars.ButtonId.SQUARE.value())
                        .addToolButton(ToolbarButtonType.INK, DefaultToolbars.ButtonId.INK.value())
                        .addToolButton(ToolbarButtonType.FREE_HIGHLIGHT, DefaultToolbars.ButtonId.FREE_HIGHLIGHT.value())
                        .addToolButton(ToolbarButtonType.ERASER, DefaultToolbars.ButtonId.ERASER.value())
                        .addToolButton(ToolbarButtonType.IMAGE, DefaultToolbars.ButtonId.IMAGE.value())
                        .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
                        .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value())
        );
    }

    /**
     * Helper method to view a PDF document from resource
     *
     * @param resourceId of the sample PDF file
     * @param fileName   of the temporary PDF file copy
     * @throws PDFNetException if invalid document path is supplied to PDFDoc
     */
    public void viewFromResource(int resourceId, String fileName) throws PDFNetException {
        File file = Utils.copyResourceToLocal(this, resourceId, fileName, ".pdf");
        mPdfDoc = new PDFDoc(file.getAbsolutePath());
        mPdfViewCtrl.setDoc(mPdfDoc);
        // Alternatively, you can open the document using Uri:
        // Uri fileUri = Uri.fromFile(file);
        // mPdfDoc = mPdfViewCtrl.openPDFUri(fileUri, null);
    }

    /**
     * We need to clean up and handle PDFViewCtrl based on Android lifecycle callback.
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.pause();
            mPdfViewCtrl.purgeMemory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPdfViewCtrl = findViewById(R.id.pdfviewctrl);
        mToolbarContainer = findViewById(R.id.annotation_toolbar_container);
        mPresetContainer = findViewById(R.id.preset_container);

        mToolManager = ToolManagerBuilder.from()
                .build(this, mPdfViewCtrl);

        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.resume();
        }

        if (mImageStampDelayCreation) {
            mImageStampDelayCreation = false;
            ViewerUtils.createImageStamp(this, mAnnotIntentData, mPdfViewCtrl, mOutputFileUri, mAnnotTargetPoint);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.destroy();
            mPdfViewCtrl = null;
        }

        if (mPdfDoc != null) {
            try {
                mPdfDoc.close();
            } catch (Exception e) {
                // handle exception
            } finally {
                mPdfDoc = null;
            }
        }
    }

    @Override
    public void fileAttachmentSelected(FileAttachment attachment) {

    }

    @Override
    public void freehandStylusUsedFirstTime() {

    }

    @Override
    public void imageStamperSelected(PointF targetPoint) {
        mImageCreationMode = ToolManager.ToolMode.STAMPER;
        mAnnotTargetPoint = targetPoint;
        mOutputFileUri = ViewerUtils.openImageIntent(this);
    }

    @Override
    public void imageSignatureSelected(PointF targetPoint, int targetPage, Long widget) {

    }

    @Override
    public void attachFileSelected(PointF targetPoint) {

    }

    @Override
    public void freeTextInlineEditingStarted() {

    }

    @Override
    public boolean newFileSelectedFromTool(String filePath, int pageNumber) {
        return false;
    }

    @Override
    public void fileCreated(String fileLocation, AnnotAction action) {

    }
}