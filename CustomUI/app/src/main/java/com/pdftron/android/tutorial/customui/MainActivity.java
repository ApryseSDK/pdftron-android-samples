package com.pdftron.android.tutorial.customui;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.config.ViewerBuilder;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.dialog.ViewModePickerDialogFragment;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

    private PdfViewCtrlTabHostFragment mPdfViewCtrlTabHostFragment;
    private boolean editingEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Copy the ppt file to our files directory
        int fileRes = R.raw.samplepptx; // test for sample pptx
        String filename = "samplepptx";
        String fileExt = ".pptx";

//        int fileRes = R.raw.sampletxt; // test for sample txt file with emoji filename, currently this does not work
//        String filename = "hdusjd udi 😎😋🤣";
//        String fileExt = ".txt";

        File copiedFile = Utils.copyResourceToLocal(this, fileRes, filename, fileExt);
        File folder = new File(this.getFilesDir(), "root/users/user1/");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File f = new File(folder, filename + fileExt);
        try {
            Utils.copy(copiedFile, f);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get Uri to our pptx file, initialize the viewer, and pass in our file
        String uriString = Uri.fromFile(f).toString();
        // file:///data/user/0/com.pdftron.android.tutorial.customui/files/root/users/user1/samplepptx.pptx
        Uri uri = Uri.parse(uriString);
        ViewerConfig.Builder builder = new ViewerConfig.Builder()
                .multiTabEnabled(false)
                .showAnnotationsList(true)
                .showUserBookmarksList(false)
                .showOutlineList(false)
                .showSaveCopyOption(false)
                .annotationsListEditingEnabled(this.editingEnabled)
                .thumbnailViewEditingEnabled(this.editingEnabled)
                .showEditPagesOption(this.editingEnabled)
                .showAnnotationToolbarOption(this.editingEnabled)
                .showFormToolbarOption(this.editingEnabled)
                .showPrintOption(this.editingEnabled)
                .openUrlCachePath(getCacheDir().getAbsolutePath())
                .documentEditingEnabled(this.editingEnabled)
                .showCloseTabOption(false)
                .showViewLayersToolbarOption(false)
                .toolManagerBuilder(
                        ToolManagerBuilder.from()
                                .disableToolModes(new ToolManager.ToolMode[]{
                                                ToolManager.ToolMode.SOUND_CREATE,
                                                ToolManager.ToolMode.RUBBER_STAMPER,
                                                ToolManager.ToolMode.SIGNATURE,
                                                ToolManager.ToolMode.FORM_SIGNATURE_CREATE,
                                                ToolManager.ToolMode.FILE_ATTACHMENT_CREATE
                                        }
                                ));

        if (!this.editingEnabled) {
            builder.hideViewModeItems(new ViewModePickerDialogFragment.ViewModePickerItems[]{
                    ViewModePickerDialogFragment.ViewModePickerItems.ITEM_ID_USERCROP
            });
        }

        ViewerConfig viewerConfig = builder.build();
        ViewerBuilder viewerBuilder = ViewerBuilder
//                .withUri(uri) // will also work
                .withFile(f)
                .usingConfig(viewerConfig)
                .usingNavIcon(R.drawable.ic_arrow_back_white_24dp);

        mPdfViewCtrlTabHostFragment = PdfViewCtrlTabHostFragment.newInstance(viewerBuilder.createBundle(this));
        mPdfViewCtrlTabHostFragment.addHostListener(this);

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
