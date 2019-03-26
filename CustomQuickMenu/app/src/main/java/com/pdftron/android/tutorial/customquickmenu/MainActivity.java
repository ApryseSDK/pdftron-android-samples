package com.pdftron.android.tutorial.customquickmenu;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.QuickMenu;
import com.pdftron.pdf.tools.QuickMenuItem;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.CommonToast;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.util.ArrayList;

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
                quickMenuPressed();
            }
        });


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
        ft.commit();
    }

    private void quickMenuPressed() {
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
                                    QuickMenuItem item = new QuickMenuItem(MainActivity.this, R.id.qm_custom_link, QuickMenuItem.OVERFLOW_ROW_MENU);
                                    item.setTitle(R.string.qm_custom_link);
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
