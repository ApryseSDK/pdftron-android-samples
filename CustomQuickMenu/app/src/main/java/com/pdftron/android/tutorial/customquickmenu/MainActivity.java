package com.pdftron.android.tutorial.customquickmenu;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.controls.AnnotationToolbar;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.model.AnnotStyle;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.model.GroupedItem;
import com.pdftron.pdf.tools.QuickMenu;
import com.pdftron.pdf.tools.QuickMenuItem;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.CommonToast;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import static com.pdftron.pdf.controls.AnnotationToolbar.PREF_KEY_LINE;
import static com.pdftron.pdf.controls.AnnotationToolbar.PREF_KEY_NOTE;
import static com.pdftron.pdf.controls.AnnotationToolbar.PREF_KEY_RECT;
import static com.pdftron.pdf.controls.AnnotationToolbar.PREF_KEY_TEXT;

public class MainActivity extends AppCompatActivity {

    private PdfViewCtrlTabHostFragment mPdfViewCtrlTabHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
        Uri uri = Uri.fromFile(f);
        Bundle args = PdfViewCtrlTabFragment.createBasicPdfViewCtrlTabBundle(this, uri, "");
        mPdfViewCtrlTabHostFragment = PdfViewCtrlTabHostFragment.newInstance(args);
        mPdfViewCtrlTabHostFragment.addHostListener(new PdfViewCtrlTabHostFragment.TabHostListener() {
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
            public boolean onToolbarOptionsItemSelected(MenuItem menuItem) {
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
                return false;
            }

            @Override
            public void onTabPaused(FileInfo fileInfo, boolean b) {

            }

            @Override
            public void onJumpToSdCardFolder() {

            }

            @Override
            public void onTabDocumentLoaded(String s) {
                customizeQuickMenu();
                customizeAnnotationToolbar();
            }
        });


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
        ft.commit();
    }

    private void customizeAnnotationToolbar() {
        if (mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
            AnnotationToolbar annotationToolbar = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getAnnotationToolbar();
            if (annotationToolbar == null) {
                mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().createAnnotationToolbar();
                annotationToolbar = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getAnnotationToolbar();
            }
            if (annotationToolbar != null) {
                annotationToolbar.getGroupItems().clear();
                annotationToolbar.getGroupItems().add(new GroupedItem(annotationToolbar, PREF_KEY_LINE, new int[]{Annot.e_Polyline, Annot.e_Line, AnnotStyle.CUSTOM_ANNOT_TYPE_ARROW, AnnotStyle.CUSTOM_ANNOT_TYPE_RULER, AnnotStyle.CUSTOM_ANNOT_TYPE_PERIMETER_MEASURE}));
                annotationToolbar.getGroupItems().add(new GroupedItem(annotationToolbar, PREF_KEY_RECT, new int[]{Annot.e_Circle, Annot.e_Square, Annot.e_Polygon, AnnotStyle.CUSTOM_ANNOT_TYPE_CLOUD, AnnotStyle.CUSTOM_ANNOT_TYPE_AREA_MEASURE}));
                annotationToolbar.getGroupItems().add(new GroupedItem(annotationToolbar, PREF_KEY_TEXT, new int[]{Annot.e_FreeText, AnnotStyle.CUSTOM_ANNOT_TYPE_CALLOUT}));
                annotationToolbar.getGroupItems().add(new GroupedItem(annotationToolbar, PREF_KEY_NOTE, new int[]{Annot.e_Sound, Annot.e_Text}));
            }
        }
    }

    private void customizeQuickMenu() {
        mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment()
                .addQuickMenuListener(new ToolManager.QuickMenuListener() {
                    @Override
                    public boolean onQuickMenuClicked(QuickMenuItem menuItem) {
                        int which = menuItem.getItemId();
                        if (which == R.id.qm_star) {
                            CommonToast.showText(MainActivity.this, "Star pressed");
                            return true;
                        } else if (which == R.id.qm_custom_link) {
                            CommonToast.showText(MainActivity.this, "Link pressed");
                        } else if (which == R.id.qm_custom_unlink) {
                            CommonToast.showText(MainActivity.this, "Unlink pressed");
                        }
                        return false;
                    }

                    @Override
                    public boolean onShowQuickMenu(QuickMenu quickMenu, Annot annot) {
                        // Programmatically change quick menu

                        try {
                            if (annot != null && quickMenu != null) {

                                if (annot.getType() == Annot.e_Square) {
                                    QuickMenuItem item = new QuickMenuItem(MainActivity.this, R.id.qm_custom_link, QuickMenuItem.FIRST_ROW_MENU);
                                    item.setTitle(R.string.qm_custom_link);
                                    item.setIcon(R.drawable.ic_link_black_24dp);
                                    item.setOrder(3);
                                    ArrayList<QuickMenuItem> items = new ArrayList<>(1);
                                    items.add(item);
                                    quickMenu.addMenuEntries(items);
                                } else if (annot.getType() == Annot.e_Circle) {
                                    QuickMenuItem item = new QuickMenuItem(MainActivity.this, R.id.qm_custom_unlink, QuickMenuItem.OVERFLOW_ROW_MENU);
                                    item.setTitle(R.string.qm_custom_unlink);
                                    ArrayList<QuickMenuItem> items = new ArrayList<>(1);
                                    items.add(item);
                                    quickMenu.addMenuEntries(items);
                                }

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        return false;
                    }

                    @Override
                    public void onQuickMenuShown() {
                        // Called when the quick menu is shown
                    }

                    @Override
                    public void onQuickMenuDismissed() {
                        // Called when the quick menu is dismissed
                    }
                });

    }


}
