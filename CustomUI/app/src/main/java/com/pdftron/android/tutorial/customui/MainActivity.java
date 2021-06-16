package com.pdftron.android.tutorial.customui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomLinkClick;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.fdf.FDFDoc;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFDraw;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

import java.io.File;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

    private PdfViewCtrlTabHostFragment2 mPdfViewCtrlTabHostFragment;

    public static final String NOTES_TOOLBAR_TAG = "notes_toolbar";
    public static final String SHAPES_TOOLBAR_TAG = "shapes_toolbar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a PdfViewCtrlTabHostFragment with a document Uri
        File f = Utils.copyResourceToLocal(this, R.raw.document, "document", ".png");
        Uri uri = Uri.fromFile(f);
        ViewerConfig viewerConfig = new ViewerConfig.Builder()
                .skipReadOnlyCheck(true)
                .addToolbarBuilder(buildNotesToolbar())
                .addToolbarBuilder(buildShapesToolbar())
                .toolbarTitle("٩(◕‿◕｡)۶")
                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder2.withUri(uri)
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
                .usingTheme(R.style.CustomAppTheme)
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
        try {
            String xfdf_string = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xfdf xmlns=\"http://ns.adobe.com/xfdf/\" xml:space=\"preserve\"><annots><circle style=\"solid\" width=\"5\" color=\"#E44234\" opacity=\"1\" creationdate=\"D:20190729202215Z\" flags=\"print\" date=\"D:20190729202215Z\" page=\"0\" rect=\"138.824,653.226,236.28,725.159\" title=\"\" /><circle style=\"solid\" width=\"5\" color=\"#E44234\" opacity=\"1\" creationdate=\"D:20190729202215Z\" flags=\"print\" date=\"D:20190729202215Z\" page=\"0\" rect=\"103.114,501.958,245.067,590.92\" title=\"\" /><circle style=\"solid\" width=\"5\" color=\"#E44234\" opacity=\"1\" creationdate=\"D:20190729202216Z\" flags=\"print\" date=\"D:20190729202216Z\" page=\"0\" rect=\"117.85,336.548,328.935,451.568\" title=\"\" /><freetext TextColor=\"#363636\" style=\"solid\" width=\"0\" opacity=\"1\" creationdate=\"D:20190729202455Z\" flags=\"print\" date=\"D:20190729202513Z\" page=\"0\" rect=\"320.774,646.323,550.446,716.498\" title=\"\"><defaultstyle>font: Roboto 24pt;color: #363636</defaultstyle><defaultappearance> 1 1 1 RG 1 1 1 rg /F0 24 Tf </defaultappearance><contents>HELLO PDFTRON!!!</contents><apref y=\"716.498\" x=\"320.774\" gennum=\"0\" objnum=\"404\" /></freetext><line style=\"solid\" width=\"5\" color=\"#E44234\" opacity=\"1\" creationdate=\"D:20190729202507Z\" flags=\"print\" start=\"278.209,212.495\" end=\"214.177,411.627\" head=\"None\" tail=\"OpenArrow\" date=\"D:20190729202507Z\" page=\"0\" rect=\"206.039,211.73,280.589,416.387\" title=\"\" /></annots><pages><defmtx matrix=\"1.333333,0.000000,0.000000,-1.333333,0.000000,1056.000000\" /></pages><pdf-info version=\"2\" xmlns=\"http://www.pdftron.com/pdfinfo\" /></xfdf>";
            FDFDoc fdf_doc = FDFDoc.createFromXFDF(xfdf_string);
            PDFDoc doc = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPdfDoc();
            doc.fdfMerge(fdf_doc);
            mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPDFViewCtrl().update(true);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    @Override
    public boolean onToolbarOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.menu_export_flattened_copy) {
            try {
                PDFDraw draw = new PDFDraw();  // PDFDraw class is used to rasterize PDF pages.
                PDFDoc doc = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPdfDoc();
                draw.setDPI(92);

                // C) Rasterize the first page in the document and save the result as PNG.
                Page pg = doc.getPage(1);
                draw.export(pg, getCacheDir() + "/annotated.png");
                Toast.makeText(this, "Created annotated image: "+ getCacheDir() + "/annotated.png", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
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
}
