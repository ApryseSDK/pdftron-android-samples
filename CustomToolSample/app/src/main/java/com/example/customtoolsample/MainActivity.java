package com.example.customtoolsample;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    PdfViewCtrlTabHostFragment2 fr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File resource = Utils.copyResourceToLocal(this, R.raw.gettingstarted, "GettingStarted", "pdf");
        fr = addViewerFragment(R.id.myLayout, this, Uri.fromFile(resource), "");
    }

    // Add a viewer fragment to the layout container in the specified
    // activity, and returns the added fragment
    public PdfViewCtrlTabHostFragment2 addViewerFragment(@IdRes int fragmentContainer,
            @NonNull FragmentActivity activity, @NonNull Uri fileUri, @Nullable String password) {

        ToolManagerBuilder toolManagerBuilder = ToolManagerBuilder
                .from()
                .setRealTimeAnnotEdit(false)
                .addCustomizedTool(CustomCloudSquare.MODE, CustomCloudSquare.class)
                .addCustomizedTool(CustomStamp.MODE, CustomStamp.class)
                .addCustomizedTool(CustomSignature.MODE, CustomSignature.class);

        ViewerConfig config = new ViewerConfig.Builder()
                .toolManagerBuilder(toolManagerBuilder)
                .build();

        // Create the viewer fragment
        PdfViewCtrlTabHostFragment2 fragment =
                ViewerBuilder2.withUri(fileUri, password)
                        .usingConfig(config)
                        .build(activity);

        // Add the fragment to the layout fragment container
        activity.getSupportFragmentManager().beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();

        fragment.addHostListener(new PdfViewCtrlTabHostFragment2.TabHostListener() {
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
                if (fr != null && fr.getCurrentPdfViewCtrlFragment() != null) {
                    fr.getCurrentPdfViewCtrlFragment().getPDFViewCtrl().setCurrentPage(8);
//                    fr.getCurrentPdfViewCtrlFragment().getToolManager().setSignSignatureFieldsWithStamps(true); //TODO pdftron
                }
            }
        });

        return fragment;
    }

    public void cloudSq(View view) {

        useCloudSquare(fr);
    }

    public void custStamp(View view) {
        useCustomStamp(fr);
    }

    public void useCloudSquare(@NonNull PdfViewCtrlTabHostFragment2 fragment) {
        // Create our custom tool
        ToolManager toolManager = fragment.getCurrentPdfViewCtrlFragment().getToolManager();
        ToolManager.Tool customTool = toolManager.createTool(CustomCloudSquare.MODE, toolManager.getTool());
        // Then set it in ToolManager
        toolManager.setTool(customTool);
    }

    public void useCustomStamp(@NonNull PdfViewCtrlTabHostFragment2 fragment) {
//        // Create our custom tool
//        ToolManager toolManager = fragment.getCurrentPdfViewCtrlFragment().getToolManager();
//        ToolManager.Tool customTool = toolManager.createTool(CustomStamp.MODE, toolManager.getTool());
//        // Then set it in ToolManager
//        toolManager.setTool(customTool);

        // demo custom signature
        ToolManager toolManager = fragment.getCurrentPdfViewCtrlFragment().getToolManager();
        ToolManager.Tool customTool = toolManager.createTool(CustomSignature.MODE, toolManager.getTool());
        if (customTool instanceof CustomSignature) {
            ((CustomSignature) customTool).signLastSavedSignatureToField(8);
        }
    }
}
