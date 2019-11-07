package com.pdftron.android.pdfviewctrlviewer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.FieldIterator;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.controls.AnnotationToolbar;
import com.pdftron.pdf.controls.AnnotationToolbarButtonId;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private PDFViewCtrl mPdfViewCtrl;
    private PDFDoc mPdfDoc;
    private ToolManager mToolManager;
    private AnnotationToolbar mAnnotationToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPdfViewCtrl = findViewById(R.id.pdfviewctrl);
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

        mToolManager.addAnnotationModificationListener(new ToolManager.AnnotationModificationListener() {
            @Override
            public void onAnnotationsAdded(Map<Annot, Integer> map) {

            }

            @Override
            public void onAnnotationsPreModify(Map<Annot, Integer> map) {

            }

            @Override
            public void onAnnotationsModified(Map<Annot, Integer> map, Bundle bundle) {
                updateFieldsOnModify(mPdfViewCtrl, map);
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
    }

    private static void updateFieldsOnModify(@NonNull PDFViewCtrl mPdfViewCtrl, @NonNull Map<Annot, Integer> map) {
        for (Map.Entry<Annot, Integer> entry : map.entrySet()) {
            Annot annot = entry.getKey();

            try {
                // Only change text fields
                if (annot.getType() != Annot.e_Widget) {
                    continue;
                }

                Widget modifiedWidget = new Widget(annot);
                Field modifiedField = modifiedWidget.getField();

                if (modifiedField == null || modifiedField.getType() != Field.e_text || !modifiedField.isValid()) {
                    continue;
                }

                // Now update all the text fields with the same name using FieldIterator
                String modifiedFieldName = modifiedField.getName();
                String newFieldValue = modifiedField.getValueAsString();

                PDFDoc doc = mPdfViewCtrl.getDoc();
                FieldIterator itr = doc.getFieldIterator();
                while (itr.hasNext()) {
                    Field currentField = itr.next();
                    if (currentField!= null && currentField.isValid()) {
                        String currentFieldName = currentField.getName();
                        if (currentFieldName.equals(modifiedFieldName)) {
                            String currentFieldValue = currentField.getValueAsString();
                            if (currentFieldValue == null || !currentFieldValue.equals(newFieldValue)) {
                                currentField.setValue(newFieldValue);
                                currentField.refreshAppearance();
                                mPdfViewCtrl.update(currentField);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper method to set up and initialize the AnnotationToolbar.
     */
    public void setupAnnotationToolbar() {
        mAnnotationToolbar = findViewById(R.id.annotationToolbar);
        // Remember to initialize your ToolManager before calling setup
        mAnnotationToolbar.setup(mToolManager);
        mAnnotationToolbar.hideButton(AnnotationToolbarButtonId.CLOSE);
        mAnnotationToolbar.show();
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Handle configuration changes from the toolbar here
        mAnnotationToolbar.onConfigurationChanged(newConfig);
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
