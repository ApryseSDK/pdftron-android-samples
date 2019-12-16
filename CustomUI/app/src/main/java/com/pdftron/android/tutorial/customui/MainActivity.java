package com.pdftron.android.tutorial.customui;

import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomLinkClick;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.PDFViewCtrlConfig;
import com.pdftron.pdf.config.ViewerBuilder;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

    private PdfViewCtrlTabHostFragment mPdfViewCtrlTabHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a PdfViewCtrlTabHostFragment with a document Uri
        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
        Uri uri = Uri.fromFile(f);
//        ViewerConfig viewerConfig = new ViewerConfig.Builder()
//                .toolbarTitle("٩(◕‿◕｡)۶")
//                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder.withUri(uri)
                .usingCustomToolbar(new int[] {R.menu.my_custom_options_toolbar})
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(getViewerConfig())
                .build(this);
        mPdfViewCtrlTabHostFragment.addHostListener(this);

        // Apply customizations to tab host fragment
        new CustomQuickMenu(MainActivity.this, mPdfViewCtrlTabHostFragment);
        new CustomLinkClick(MainActivity.this, mPdfViewCtrlTabHostFragment);
        new CustomAnnotationToolbar(MainActivity.this, mPdfViewCtrlTabHostFragment);

        FloatingActionButton annotationEditorFab = findViewById(R.id.annotation_editor_fab);
        annotationEditorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
                    if (mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().isAnnotationMode()) {
                        mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().hideAnnotationToolbar();
                    } else {
                        mPdfViewCtrlTabHostFragment.onOpenAnnotationToolbar(null);
                    }
                }
            }
        });


        // Add the fragment to our activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
        ft.commit();
    }

    private ViewerConfig getViewerConfig() {
        final PDFViewCtrlConfig pdfViewCtrlConfig = PDFViewCtrlConfig
                .getDefaultConfig(this)
                .setPageRefViewMode(PDFViewCtrl.PageViewMode.FIT_WIDTH);

        return new ViewerConfig.Builder()
                .pdfViewCtrlConfig(pdfViewCtrlConfig)
                .autoHideToolbarEnabled(false)
                .fullscreenModeEnabled(false)
                .multiTabEnabled(false)
                .showShareOption(false)
                .showBottomNavBar(false)
                .showAnnotationToolbarOption(true)
                .documentEditingEnabled(true)
                .longPressQuickMenuEnabled(false)
                .showSearchView(true)
                .toolbarTitle("some name")
                .build();
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
}
