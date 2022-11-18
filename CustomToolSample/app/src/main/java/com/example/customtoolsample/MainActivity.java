package com.example.customtoolsample;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.config.ViewerBuilder2;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AnnotUtils;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.util.Map;

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
//                .addCustomizedTool(CustomStamp.MODE, CustomStamp.class);
                .addCustomizedTool(ToolManager.ToolMode.FORM_TEXT_FIELD_CREATE, CustomStamp.class);

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
            public void onTabChanged(String tag) {

            }

            @Override
            public boolean onOpenDocError() {
                return false;
            }

            @Override
            public void onNavButtonPressed() {

            }

            @Override
            public void onShowFileInFolder(String fileName, String filepath, int itemSource) {

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
            public boolean onToolbarCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                return false;
            }

            @Override
            public boolean onToolbarPrepareOptionsMenu(Menu menu) {
                return false;
            }

            @Override
            public boolean onToolbarOptionsItemSelected(MenuItem item) {
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
            public void onTabPaused(FileInfo fileInfo, boolean isDocModifiedAfterOpening) {

            }

            @Override
            public void onJumpToSdCardFolder() {

            }

            @Override
            public void onTabDocumentLoaded(String tag) {
                // Create an appearance from a button and store into a PDF, note this can be any Android View
                createTextWidgetAppearance();

                // Add annotation modified listener to update the appearance when it gets resized
                fragment.getCurrentPdfViewCtrlFragment().getToolManager().addAnnotationModificationListener(new ToolManager.AnnotationModificationListener() {
                    @Override
                    public void onAnnotationsAdded(Map<Annot, Integer> annots) {

                    }

                    @Override
                    public void onAnnotationsPreModify(Map<Annot, Integer> annots) {

                    }

                    @Override
                    public void onAnnotationsModified(Map<Annot, Integer> annots, Bundle extra) {
                        for (Annot annot : annots.keySet()) {
                            try {
                                // This will update all widgets, add extra logic here if you want to target specific widgets
                                if (annot.getType() == Annot.e_Widget) {
                                    AnnotUtils.refreshCustomFreeTextAppearance(new File(getFilesDir(), "output.pdf"), annot);
                                }
                            } catch (Exception e) {

                            }
                        }
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
        });

        return fragment;
    }

    private void createTextWidgetAppearance() {
        Button view = findViewById(R.id.button4);
        AnnotUtils.createPdfFromView(view, 360, 120, new File(getFilesDir(), "output.pdf"));
    }
}
