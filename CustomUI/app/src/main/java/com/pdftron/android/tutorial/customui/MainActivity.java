package com.pdftron.android.tutorial.customui;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
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
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.ColorSpace;
import com.pdftron.pdf.Element;
import com.pdftron.pdf.ElementReader;
import com.pdftron.pdf.ElementWriter;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.Point;
import com.pdftron.pdf.annots.Text;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment2;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.AnnotEdit;
import com.pdftron.pdf.tools.QuickMenu;
import com.pdftron.pdf.tools.QuickMenuItem;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

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
                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder2.withUri(uri)
                .usingCustomToolbar(new int[] {R.menu.my_custom_options_toolbar})
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
                .usingTheme(R.style.CustomAppTheme)
                .build(this);
        mPdfViewCtrlTabHostFragment.addHostListener(this);

        // Apply customizations to tab host fragment
//        new CustomQuickMenu(MainActivity.this, mPdfViewCtrlTabHostFragment);
//        new CustomLinkClick(MainActivity.this, mPdfViewCtrlTabHostFragment);
//        new CustomAnnotationToolbar(MainActivity.this, mPdfViewCtrlTabHostFragment);

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
        PdfViewCtrlTabFragment2 currentPdfViewCtrlFragment = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment();
        if (currentPdfViewCtrlFragment != null) {
            ToolManager toolManager = currentPdfViewCtrlFragment.getToolManager();
            toolManager.setBasicAnnotationListener(new ToolManager.BasicAnnotationListener() {
                private Annot lastSelectedAnnot = null;
                private int lastAnnotPage = -1;
                @Override
                public void onAnnotationSelected(Annot annot, int pageNum) {
                    lastSelectedAnnot = annot;
                    lastAnnotPage = pageNum;
                }

                @Override
                public void onAnnotationUnselected() {

                }

                @Override
                public boolean onInterceptAnnotationHandling(@Nullable Annot annot, Bundle extra, ToolManager.ToolMode toolMode) {
                    Object upFromStickyCreate = extra.get("upFromStickyCreate");
                    // Intercept the sticky event to prevent popup
                    if (upFromStickyCreate instanceof Boolean && (!(Boolean) upFromStickyCreate)) {
                        if (lastSelectedAnnot != null & lastAnnotPage != -1) {
                            toolManager.selectAnnot(lastSelectedAnnot, lastAnnotPage);
                        }
                        return true;
                    }
                    return false;
                }

                @Override
                public boolean onInterceptDialog(AlertDialog dialog) {
                    return false;
                }
            });

            // Customize quick menu for sticky note
            currentPdfViewCtrlFragment.addQuickMenuListener(new ToolManager.QuickMenuListener() {
                @Override
                public boolean onQuickMenuClicked(QuickMenuItem menuItem) {
                    return false;
                }

                @Override
                public boolean onShowQuickMenu(QuickMenu quickMenu, @Nullable Annot annot) {
                    // Programmatically change quick menu
                    try {
                        if (annot != null && quickMenu != null) {
                            if (annot.getType() == Annot.e_Text) {
                                ArrayList<QuickMenuItem> menuEntries = new ArrayList<>(quickMenu.getFirstRowMenuItems());
                                Iterator<QuickMenuItem> iterator = menuEntries.iterator();
                                while (iterator.hasNext()) {
                                    if (iterator.next().getItemId() == R.id.qm_delete) {
                                        iterator.remove();
                                    }
                                }
                                quickMenu.removeMenuEntries(menuEntries);
                                quickMenu.removeMenuEntries(new ArrayList<>(quickMenu.getSecondRowMenuItems()));
                                quickMenu.removeMenuEntries(new ArrayList<>(quickMenu.getOverflowMenuItems()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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

        // Add sample sticky note on document loaded
        addCustomStickyAnnotation(100, 100, 1, 2, 0, 1);
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

    private PDFViewCtrl getPdfViewCtrl() {
        return mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPDFViewCtrl();
    }

    public void addCustomStickyAnnotation(double x, double y, int pageNumber, int icon, double anchorPointX, double anchorPointY) {
        try{
            PDFViewCtrl pdfViewCtrl = getPdfViewCtrl();
            PDFDoc pdfDoc = pdfViewCtrl.getDoc();
            final Page page = pdfDoc.getPage(pageNumber);
            Text txt = Text.create(pdfDoc,new Point(x,y));
            InputStream fis = this.getResources().openRawResource(R.raw.stickynote_icons);
            PDFDoc template = new PDFDoc(fis);
            Page iconPage = template.getPage(icon);
            com.pdftron.sdf.Obj contents = iconPage.getContents();
            com.pdftron.sdf.Obj importedContents = txt.getSDFObj().getDoc().importObj(contents, true);
            com.pdftron.pdf.Rect bbox = iconPage.getMediaBox();
            importedContents.putRect("BBox", bbox.getX1(), bbox.getY1(), bbox.getX2(), bbox.getY2());
            importedContents.putName("Subtype", "Form");
            importedContents.putName("Type", "XObject");
            ElementReader reader = new ElementReader();
            ElementWriter writer = new ElementWriter();
            reader.begin(importedContents);
            writer.begin(importedContents, true);
            ColorPt rgbColor = txt.getColorAsRGB();
            double opacity = txt.getOpacity();
            for (Element element = reader.next(); element != null; element = reader.next()) {
                if (element.getType() == Element.e_path && !element.isClippingPath()) {
                    element.getGState().setFillColorSpace(ColorSpace.createDeviceRGB());
                    element.getGState().setFillColor(rgbColor);
                    element.getGState().setFillOpacity(opacity);
                    element.getGState().setStrokeOpacity(opacity);
                    element.setPathStroke(true);
                    element.setPathFill(true);
                }
                writer.writeElement(element);
            }
            reader.end();
            writer.end();

            // set the appearance of sticky note icon to the custom icon
            txt.setAppearance(importedContents);
            txt.setUniqueID("Ticket1");
            txt.setAnchorPosition(new Point(anchorPointX,anchorPointY));
            page.annotPushBack(txt);
            pdfViewCtrl.update(true);
        }catch(PDFNetException | IOException pdfErr){
            pdfErr.printStackTrace();
        }

    }
}
