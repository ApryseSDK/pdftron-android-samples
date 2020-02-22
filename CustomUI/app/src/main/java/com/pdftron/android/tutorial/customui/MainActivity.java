package com.pdftron.android.tutorial.customui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomLinkClick;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.Field;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.FreeText;
import com.pdftron.pdf.annots.Widget;
import com.pdftron.pdf.config.ViewerBuilder;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        PdfViewCtrlTabHostFragment.TabHostListener,
        com.pdftron.pdf.tools.ToolManager.AnnotationModificationListener {

    private PdfViewCtrlTabHostFragment mPdfViewCtrlTabHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a PdfViewCtrlTabHostFragment with a document Uri
        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
        Uri uri = Uri.fromFile(f);
        ViewerConfig viewerConfig = new ViewerConfig.Builder()
                .toolbarTitle("٩(◕‿◕｡)۶")
                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder.withUri(uri)
                .usingCustomToolbar(new int[]{R.menu.my_custom_options_toolbar})
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
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

    @Override
    public void onTabDocumentLoaded(String s) {
        if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
            ToolManager tm = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getToolManager();
            tm.addAnnotationModificationListener(this);
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
    public void onOpenDocError() {

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

    @Override
    public void onAnnotationsAdded(Map<Annot, Integer> map) {

    }

    @Override
    public void onAnnotationsPreModify(Map<Annot, Integer> map) {

    }

    @Override
    public void onAnnotationsModified(Map<Annot, Integer> map, Bundle bundle) {
        for (Map.Entry<Annot, Integer> entry : map.entrySet()) {
            try {
                Annot annot = entry.getKey();
                final int pageNum = entry.getValue();
                if (annot != null && annot.isValid()) {
                    if (annot.getType() == Annot.e_Widget) {
                        Widget w = new Widget(annot);
                        Field field = w.getField();
                        int field_type = field.getType();
                        if (field_type == Field.e_text) {
                            // bottom align starts here
                            final PDFViewCtrl pdfViewCtrl = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPDFViewCtrl();
                            Rect annotRect = annot.getRect();
                            annotRect.normalize();

                            // calc new annot rect, this is the box from start of the annotation to the end of the page
                            Page p = pdfViewCtrl.getDoc().getPage(pageNum);
                            Rect pageRect = p.getCropBox();
                            Rect textRect = new Rect();
                            textRect.setX1(annotRect.getX1());
                            textRect.setX2(pageRect.getX2());
                            textRect.setY1(pageRect.getY1());
                            textRect.setY2(annotRect.getY2());

                            // create new temp doc and temp page
                            PDFDoc tempDoc = new PDFDoc();
                            Page newPage = tempDoc.pageCreate(pageRect);
                            tempDoc.pagePushBack(newPage);

                            final String text = "THIS IS A TEST\nTHIS IS A LONGER LINE TEST\nTHIS IS A EVEN LONGERRRRRRR LINE TEST";

                            // add the free text that you will be adding to the actual document
                            FreeText txtannot = createFreeText(tempDoc, textRect, text);
                            newPage.annotPushBack(txtannot);
                            txtannot.flatten(newPage);

                            // crop out the visible content, which in this case is a tight bounding box around the text
                            Rect visibleRect = newPage.getVisibleContentBox();
                            newPage.setCropBox(visibleRect);
                            newPage.setMediaBox(visibleRect);

                            // create the actual annotation with the tight bounding box in the actual document
                            Rect finalRect = new Rect();
                            finalRect.setX1(annotRect.getX1());
                            finalRect.setX2(annotRect.getX1() + visibleRect.getWidth());
                            finalRect.setY1(annotRect.getY1());
                            finalRect.setY2(annotRect.getY1() + visibleRect.getHeight());

                            FreeText toAddToPage = createFreeText(pdfViewCtrl.getDoc(), finalRect, text);
                            p.annotPushBack(toAddToPage);
                            pdfViewCtrl.update(toAddToPage, pageNum);
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static FreeText createFreeText(PDFDoc doc, Rect bbox, String contents) throws PDFNetException {
        FreeText txtannot = FreeText.create(doc, bbox);
        txtannot.setContents(contents);

        //fill
        ColorPt emptyColorPt = new ColorPt(0, 0, 0, 0);
        txtannot.setColor(emptyColorPt, 0);

        // border
        txtannot.setLineColor(new ColorPt(0, 0, 0, 0), 0);
        Annot.BorderStyle border = txtannot.getBorderStyle();
        border.setWidth(0);
        txtannot.setBorderStyle(border);
        txtannot.getSDFObj().erase("AP");

        // text color
        ColorPt textColorPt = Utils.color2ColorPt(Color.BLACK);
        txtannot.setTextColor(textColorPt, 3);

        // text size
        txtannot.setFontSize(8);

        txtannot.refreshAppearance();
        return txtannot;
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
}
