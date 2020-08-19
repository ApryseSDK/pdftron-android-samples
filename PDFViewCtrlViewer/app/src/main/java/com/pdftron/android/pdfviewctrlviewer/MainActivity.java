package com.pdftron.android.pdfviewctrlviewer;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.preset.component.PresetBarComponent;
import com.pdftron.pdf.widget.preset.component.PresetBarViewModel;
import com.pdftron.pdf.widget.preset.component.view.PresetBarView;
import com.pdftron.pdf.widget.toolbar.ToolManagerViewModel;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarComponent;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;
import com.pdftron.pdf.widget.toolbar.component.view.AnnotationToolbarView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private PDFViewCtrl mPdfViewCtrl;
    private PDFDoc mPdfDoc;
    private ToolManager mToolManager;
    private AnnotationToolbarComponent mAnnotationToolbarComponent;
    private PresetBarComponent mPresetBarComponent;
    private FrameLayout mToolbarContainer;
    private FrameLayout mPresetContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPdfViewCtrl = findViewById(R.id.pdfviewctrl);
        mToolbarContainer = findViewById(R.id.annotatio_toolbar_container);
        mPresetContainer = findViewById(R.id.preset_bar);
        setupToolManager();
        setupAnnotationToolbar();
        try {
            AppUtils.setupPDFViewCtrl(mPdfViewCtrl);
            viewFromResource(R.raw.sample, "sample_file");
        } catch (PDFNetException e) {
            Log.e(TAG, "Error setting up PDFViewCtrl");
        }
    }

    /**
     * Helper method to set up and initialize the ToolManager.
     */
    public void setupToolManager() {
        mToolManager = ToolManagerBuilder.from()
                .build(this, mPdfViewCtrl);
    }

    /**
     * Helper method to set up and initialize the AnnotationToolbar.
     */
    public void setupAnnotationToolbar() {
        ToolManagerViewModel toolManagerViewModel = ViewModelProviders.of(this).get(ToolManagerViewModel.class);
        toolManagerViewModel.setToolManager(mToolManager);
        PresetBarViewModel presetViewModel = ViewModelProviders.of(this).get(PresetBarViewModel.class);

        // Create our UI components for the annotation toolbar annd preset bar
        mAnnotationToolbarComponent = new AnnotationToolbarComponent(
                this,
                presetViewModel,
                toolManagerViewModel,
                new AnnotationToolbarView(mToolbarContainer)
        );

        mPresetBarComponent = new PresetBarComponent(
                this,
                getSupportFragmentManager(),
                presetViewModel,
                toolManagerViewModel,
                new PresetBarView(mPresetContainer)
        );

        // Create our custom toolbar and pass it to the annotation toolbar UI component
        mAnnotationToolbarComponent.inflateWithBuilder(
                AnnotationToolbarBuilder.withTag("Custom Toolbar")
                        .addToolButton(ToolbarButtonType.SQUARE, DefaultToolbars.ButtonId.SQUARE.value())
                        .addToolButton(ToolbarButtonType.INK, DefaultToolbars.ButtonId.INK.value())
                        .addToolButton(ToolbarButtonType.FREE_HIGHLIGHT, DefaultToolbars.ButtonId.FREE_HIGHLIGHT.value())
                        .addToolButton(ToolbarButtonType.ERASER, DefaultToolbars.ButtonId.ERASER.value())
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
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.resume();
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
}
