package com.pdftron.android.pdfviewctrlviewer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.controls.AnnotationToolbar;
import com.pdftron.pdf.controls.AnnotationToolbarButtonId;
import com.pdftron.pdf.tools.CustomRelativeLayout;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();

    private PDFViewCtrl mPdfViewCtrl;
    private PDFDoc mPdfDoc;
    private ToolManager mToolManager;
    private AnnotationToolbar mAnnotationToolbar;

    // Prevent adding duplicate views on a single annot
    private LongSparseArray<CustomRelativeLayout> mLinkOverlayMap = new LongSparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPdfViewCtrl = findViewById(R.id.pdfviewctrl);
        try {
            mPdfViewCtrl.setHighlightFields(false);
        } catch (PDFNetException e) {
            e.printStackTrace();
        }

        mPdfViewCtrl.addPageChangeListener(new PDFViewCtrl.PageChangeListener() {

            @Override
            public void onPageChange(int oldPage, int newPage, PDFViewCtrl.PageChangeState pageChangeState) {
                boolean shouldUnlockRead = false;
                try {
                    mPdfViewCtrl.docLockRead();
                    shouldUnlockRead = true;
                    ArrayList<Annot> annots = mPdfViewCtrl.getAnnotationsOnPage(newPage);

                    // Loop through annots and find the signature fields
                    for (Annot annot : annots) {
                        if (annot != null && annot.getType() == Annot.e_Widget) {
                            Field field = new Widget(annot).getField();
                            if (field.isValid() && field.getType() == Field.e_signature) {
                                long annotObjNum = annot.getSDFObj().getObjNum();
                                // Keep a list of already added outlines so we do not add multiple views
                                if (mLinkOverlayMap.get(annotObjNum) != null) {
                                    continue;
                                }
                                // Create the custom border view and add it to PDFViewCtrl
                                Context context = mPdfViewCtrl.getContext();
                                CustomRelativeLayout overlay = new CustomRelativeLayout(context);
                                overlay.setBackgroundResource(R.drawable.field_border);
                                overlay.setAnnot(mPdfViewCtrl, annot, newPage);
                                overlay.setZoomWithParent(true);
                                mPdfViewCtrl.addView(overlay);
                                mLinkOverlayMap.put(annotObjNum, overlay);
                            }
                        }
                    }
                } catch (PDFNetException e) {
                    e.printStackTrace();
                } finally {
                    if (shouldUnlockRead) {
                        mPdfViewCtrl.docUnlockRead();
                    }
                }
            }
        });
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
