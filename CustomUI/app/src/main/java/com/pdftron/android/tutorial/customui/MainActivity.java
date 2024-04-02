package com.pdftron.android.tutorial.customui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomLinkClick;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.fdf.FDFDoc;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment2.TabHostListener {

    private PdfViewCtrlTabHostFragment2 mPdfViewCtrlTabHostFragment;

    public static final String NOTES_TOOLBAR_TAG = "notes_toolbar";
    public static final String SHAPES_TOOLBAR_TAG = "shapes_toolbar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a PdfViewCtrlTabHostFragment with a document Uri
        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
        Uri uri = Uri.fromFile(f);
        ViewerConfig viewerConfig = new ViewerConfig.Builder()
                .addToolbarBuilder(buildNotesToolbar())
                .addToolbarBuilder(buildShapesToolbar())
                .toolbarTitle("٩(◕‿◕｡)۶")
                .fullscreenModeEnabled(false)
                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder2.withUri(uri)
                .usingCustomToolbar(new int[]{R.menu.my_custom_options_toolbar})
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
                .usingTheme(R.style.MyCustomAppTheme)
                .build(this);
        mPdfViewCtrlTabHostFragment.addHostListener(this);

        // Apply customizations to tab host fragment
        new CustomQuickMenu(MainActivity.this, mPdfViewCtrlTabHostFragment);
        new CustomLinkClick(MainActivity.this, mPdfViewCtrlTabHostFragment);
        new CustomAnnotationToolbar(MainActivity.this, mPdfViewCtrlTabHostFragment);

        // Add the fragment to our activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrlTabHostFragment != null) {
            mPdfViewCtrlTabHostFragment.removeHostListener(this);
        }
    }

    private AnnotationToolbarBuilder buildNotesToolbar() {
        return AnnotationToolbarBuilder.withTag(NOTES_TOOLBAR_TAG) // Identifier for toolbar
                .setToolbarName("Notes Toolbar") // Name used when displaying toolbar
                .addToolButton(ToolbarButtonType.INK, 1)
                .addToolButton(ToolbarButtonType.STICKY_NOTE, 2)
                .addToolButton(ToolbarButtonType.TEXT_HIGHLIGHT, 3)
                .addToolButton(ToolbarButtonType.TEXT_UNDERLINE, 4)
                .addToolButton(ToolbarButtonType.TEXT_STRIKEOUT, 5)
                .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
                .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value());
    }

    private AnnotationToolbarBuilder buildShapesToolbar() {
        return AnnotationToolbarBuilder.withTag(SHAPES_TOOLBAR_TAG) // Identifier for toolbar
                .setToolbarName("Shapes Toolbar") // Name used when displaying toolbar
                .addToolButton(ToolbarButtonType.SQUARE, DefaultToolbars.ButtonId.SQUARE.value())
                .addToolButton(ToolbarButtonType.CIRCLE, DefaultToolbars.ButtonId.CIRCLE.value())
                .addToolButton(ToolbarButtonType.LINE, DefaultToolbars.ButtonId.LINE.value())
                .addToolButton(ToolbarButtonType.POLYGON, DefaultToolbars.ButtonId.POLYGON.value())
                .addToolButton(ToolbarButtonType.POLYLINE, DefaultToolbars.ButtonId.POLYLINE.value())
                .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
                .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value());
    }

    @Override
    public void onTabDocumentLoaded(String s) {
        if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
            ToolManager tm = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getToolManager();
            tm.addAnnotationModificationListener(new ToolManager.AnnotationModificationListener() {
                @Override
                public void onAnnotationsAdded(Map<Annot, Integer> annots) {
                    demoExtraAnnotData("onAnnotationsAdded", annots);
                }

                @Override
                public void onAnnotationsPreModify(Map<Annot, Integer> annots) {

                }

                @Override
                public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra) {
                    demoExtraAnnotData("onAnnotationsModified", annots);
                }

                @Override
                public void onAnnotationsPreRemove(Map<Annot, Integer> annots) {
                    demoExtraAnnotData("onAnnotationsPreRemove", annots);
                }

                @Override
                public void onAnnotationsRemoved(Map<Annot, Integer> annots) {

                }

                @Override
                public void onAnnotationsRemovedOnPage(int pageNum) {

                }

                @Override
                public void annotationsCouldNotBeAdded(String errorMessage) {

                }
            });
        }
    }

    @Override
    public boolean onToolbarOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.action_compare_same_file) {
            File firstFile = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
            File secondFile = Utils.copyResourceToLocal(this, R.raw.sample, "sample2", ".pdf");
            compareFiles(firstFile, secondFile);
        } else if (menuItem.getItemId() == R.id.action_compare_with_changes) {
            File firstFile = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
            File secondFile = Utils.copyResourceToLocal(this, R.raw.sample_with_changes, "sample2", ".pdf");
            compareFiles(firstFile, secondFile);
        } else if (menuItem.getItemId() == R.id.action_compare_with_revert) {
            File firstFile = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
            File secondFile = Utils.copyResourceToLocal(this, R.raw.sample_with_changes_reverted, "sample2", ".pdf");
            compareFiles(firstFile, secondFile);
        } else if (menuItem.getItemId() == R.id.action_compare_with_clone) {
            File firstFile = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
            File secondFile = Utils.copyResourceToLocal(this, R.raw.sample_direct_clone, "sample2", ".pdf");
            compareFiles(firstFile, secondFile);
        }
        return false;
    }

    @Override
    public void onTabHostShown() {

    }

    @Override
    public void onTabHostHidden() {

    }

    @Override
    public void onLastTabClosed() {

    }

    @Override
    public void onTabChanged(String s) {

    }

    @Override
    public boolean onOpenDocError() {
        return false;
    }

    @Override
    public void onNavButtonPressed() {

    }

    @Override
    public void onShowFileInFolder(String s, String s1, int i) {

    }

    @Override
    public boolean canShowFileInFolder() {
        return false;
    }

    @Override
    public boolean canShowFileCloseSnackbar() {
        return false;
    }

    @Override
    public boolean onToolbarCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        return false;
    }

    @Override
    public boolean onToolbarPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onStartSearchMode() {

    }

    @Override
    public void onExitSearchMode() {

    }

    @Override
    public boolean canRecreateActivity() {
        return true;
    }

    @Override
    public void onTabPaused(FileInfo fileInfo, boolean b) {

    }

    @Override
    public void onJumpToSdCardFolder() {

    }

    private void demoExtraAnnotData(String event, Map<Annot, Integer> annots) {
        try {
            for (Annot a : annots.keySet()) {
                if (a.isMarkup()) {
                    Markup mu = new Markup(a);
                    mu.setSubject("New Subject");
                }
                a.setCustomData("contactId", UUID.randomUUID().toString());
            }
            String xfdf = getXfdf(annots);
            Log.d("PDFTron", event + ": " + xfdf);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    private String getXfdf(Map<Annot, Integer> annots) {
        if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
            try {
                PDFDoc pdfDoc = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPdfDoc();
                FDFDoc fdfDoc = pdfDoc.fdfExtract(new ArrayList<>(annots.keySet()));
                return fdfDoc.saveAsXFDF();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private void compareFiles(File firstFile, File secondFile) {
        try {
            try (InputStream is =  new FileInputStream(firstFile.getAbsolutePath())) {
                InputStream is2 =  new FileInputStream(secondFile.getAbsolutePath());
                String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
                String secondMd5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is2);

                if (md5.equals(secondMd5)) {
                    Toast.makeText(this, "This file is the same: " + md5, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "This file is not the same: " + md5 + ", " + secondMd5, Toast.LENGTH_SHORT).show();
                }
                Log.d("LOGMD5", md5);
                Log.d("LOGMD5", secondMd5);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
