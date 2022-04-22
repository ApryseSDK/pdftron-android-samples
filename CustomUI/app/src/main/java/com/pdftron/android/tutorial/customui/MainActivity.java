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
import com.pdftron.pdf.config.ToolManagerBuilder;
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
        ToolManagerBuilder tmBuilder = ToolManagerBuilder.from().setShowUndoRedo(false);
        ViewerConfig viewerConfig = new ViewerConfig.Builder()
                .useSupportActionBar(false)
                .multiTabEnabled(false)
                .toolManagerBuilder(tmBuilder)
//                .addToolbarBuilder(buildNotesToolbar())
//                .addToolbarBuilder(buildShapesToolbar())
//                .toolbarTitle("٩(◕‿◕｡)۶")
                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder2.withUri(uri)
//                .usingCustomToolbar(new int[]{R.menu.my_custom_options_toolbar})
//                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
                .usingTheme(R.style.MyAppTheme)
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
        if (menuItem.getItemId() == R.id.action_show_toast) {
            Toast.makeText(this, "Show toast is clicked!", Toast.LENGTH_SHORT).show();
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
}
