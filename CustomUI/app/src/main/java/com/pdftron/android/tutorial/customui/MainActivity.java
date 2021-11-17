package com.pdftron.android.tutorial.customui;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomLinkClick;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.DigitalSignatureField;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.PageIterator;
import com.pdftron.pdf.annots.SignatureWidget;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment2;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.CustomRelativeLayout;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

    private PdfViewCtrlTabHostFragment2 mPdfViewCtrlTabHostFragment;

    public static final String NOTES_TOOLBAR_TAG = "notes_toolbar";
    public static final String SHAPES_TOOLBAR_TAG = "shapes_toolbar";

    private HashMap<CustomRelativeLayout, Widget> mPlaceholders = new HashMap<>();

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
        PdfViewCtrlTabFragment2 tabFragment = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment();
        PDFViewCtrl pdfViewCtrl = tabFragment.getPDFViewCtrl();
        PDFDoc pdfDoc = tabFragment.getPdfDoc();
        boolean shouldUnlock = false;
        try {
            if (tabFragment != null) {
                pdfDoc.lockRead();
                shouldUnlock = true;
                int pageNum = 1;
                for (PageIterator itr = pdfDoc.getPageIterator(); itr.hasNext(); ) {

                    Page page = itr.next();
                    int numAnnots = page.getNumAnnots();
                    for (int i = 0; i < numAnnots; ++i) {
                        Annot annot = page.getAnnot(i);
                        if (!annot.isValid()) continue;

                        if (annot.getType() == Annot.e_Widget) {
                            Widget widget = new Widget(annot);
                            Field field = widget.getField();

                            if (field != null && field.isValid() &&
                                    (field.getType() == Field.e_signature || field.getType() == Field.e_text)) {
                                // Create placeholder text view
                                TextView placeholder = new TextView(this);
                                placeholder.setText("Placeholder");

                                // Create custom relative layout
                                CustomRelativeLayout overlay = new CustomRelativeLayout(this);
                                overlay.setBackgroundColor(this.getResources().getColor(R.color.orange));
                                overlay.addView(placeholder);
                                overlay.setAnnot(pdfViewCtrl, widget, pageNum);
                                overlay.setZoomWithParent(true);
                                pdfViewCtrl.addView(overlay);

                                // Store a reference to the custom relative layout
                                mPlaceholders.put(overlay, widget);
                            }
                        }
                    }
                    pageNum++;
                }
                // Refresh visibility of placeholders
                refreshPlaceholdersVisibility();
            }
        } catch (PDFNetException e) {
            // handle exception
        } finally {
            if (shouldUnlock) {
                Utils.unlockReadQuietly(pdfDoc);
            }
        }

        ToolManager toolManager = tabFragment.getToolManager();
        toolManager.addAnnotationModificationListener(new ToolManager.AnnotationModificationListener() {
            @Override
            public void onAnnotationsAdded(Map<Annot, Integer> annots) {

            }

            @Override
            public void onAnnotationsPreModify(Map<Annot, Integer> annots) {

            }

            @Override
            public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra) {
                refreshPlaceholdersVisibility();
            }

            @Override
            public void onAnnotationsPreRemove(Map<Annot, Integer> annots) {

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

    void refreshPlaceholdersVisibility() {
        PdfViewCtrlTabFragment2 tabFragment2 = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment();
        if (tabFragment2 == null) return;
        PDFDoc pdfDoc = tabFragment2.getPdfDoc();
        boolean shouldUnlock = false;
        try {
            pdfDoc.lockRead();
            shouldUnlock = true;
            for (Map.Entry<CustomRelativeLayout, Widget> entry : mPlaceholders.entrySet()) {
                CustomRelativeLayout layout = entry.getKey();
                Widget widget = entry.getValue();
                Field field = widget.getField();
                if (field != null && field.isValid()) {
                    switch (field.getType()) {
                        case Field.e_text: {
                            // If no text in text field, then show placeholder
                            if (Utils.isNullOrEmpty(field.getValueAsString())) {
                                layout.setVisibility(View.VISIBLE);
                            } else {
                                layout.setVisibility(View.GONE);
                            }
                            break;
                        }
                        case Field.e_signature: {
                            SignatureWidget signatureWidget = new SignatureWidget(widget);
                            DigitalSignatureField digitalSignatureField = signatureWidget.getDigitalSignatureField();
                            // If signature visible, then show placeholder
                            if (digitalSignatureField.hasVisibleAppearance()) {
                                layout.setVisibility(View.GONE);
                            } else {
                                layout.setVisibility(View.VISIBLE);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (PDFNetException e) {
            // handle exception
        } finally {
            if (shouldUnlock) {
                Utils.unlockReadQuietly(pdfDoc);
            }
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
}
