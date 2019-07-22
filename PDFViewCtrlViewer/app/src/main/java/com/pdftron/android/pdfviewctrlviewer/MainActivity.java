package com.pdftron.android.pdfviewctrlviewer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.controls.AnnotationToolbar;
import com.pdftron.pdf.controls.AnnotationToolbarButtonId;
import com.pdftron.pdf.tools.QuickMenu;
import com.pdftron.pdf.tools.QuickMenuItem;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

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
        mToolManager.setExternalAnnotationManagerListener(new ToolManager.ExternalAnnotationManagerListener() {
            @Override
            public String onGenerateKey() {
                return UUID.randomUUID().toString();
            }
        });
        mToolManager.setQuickMenuListener(new ToolManager.QuickMenuListener() {
            @Override
            public boolean onQuickMenuClicked(QuickMenuItem quickMenuItem) {
                if (quickMenuItem.getItemId() == R.id.qm_publish) {
                    // publish clicked
                    Annot annot = ViewerUtils.getAnnotById(mPdfViewCtrl, mToolManager.getSelectedAnnotId(), mToolManager.getSelectedAnnotPageNum());
                    if (null == annot) {
                        return false;
                    }
                    boolean shouldUnlock = false;
                    try {
                        mPdfViewCtrl.docLock(true);
                        shouldUnlock = true;

                        // raise pre-modified event
                        HashMap<Annot, Integer> annots = new HashMap<>(1);
                        annots.put(annot, mToolManager.getSelectedAnnotPageNum());
                        mToolManager.raiseAnnotationsPreModifyEvent(annots);

                        // set print flag
                        annot.setFlag(Annot.e_print, true);

                        mToolManager.raiseAnnotationsModifiedEvent(annots, Tool.getAnnotationModificationBundle(null));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        if (shouldUnlock) {
                            mPdfViewCtrl.docUnlock();
                        }
                    }
                    mToolManager.deselectAll();

                    return true;
                }
                return false;
            }

            @Override
            public boolean onShowQuickMenu(QuickMenu quickMenu, Annot annot) {
//                boolean shouldUnlockRead = false;
//                boolean hasPrint = false;
//                try {
//                    mPdfViewCtrl.docLockRead();
//                    shouldUnlockRead = true;
//
//                    hasPrint = annot != null && annot.getFlag(Annot.e_print);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                } finally {
//                    if (shouldUnlockRead) {
//                        mPdfViewCtrl.docUnlockRead();
//                    }
//                }
//                if (hasPrint) {
//                    // if already has print flag, hide publish menu
//                    QuickMenuItem item = quickMenu.findMenuItem(R.id.qm_publish);
//                    if (item != null) {
//                        item.setVisible(false);
//                    }
//                    // fake an add so we can refresh the quick menu
//                    quickMenu.addMenuEntries(new ArrayList<QuickMenuItem>());
//                }
                return false;
            }

            @Override
            public void onQuickMenuShown() {

            }

            @Override
            public void onQuickMenuDismissed() {

            }
        });
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
