package com.example.customtoolsample;

import android.content.Intent;
import android.graphics.PointF;
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
import com.pdftron.pdf.controls.AnnotStyleDialogFragment;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.interfaces.OnCreateSignatureListener;
import com.pdftron.pdf.interfaces.OnDialogDismissListener;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.Signature;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.RequestCode;
import com.pdftron.pdf.utils.StampManager;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private PdfViewCtrlTabHostFragment2 fr = null;

    private Uri mImageSignature = null;
    private File mSignatureFileToSign = null;

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
                    fr.getCurrentPdfViewCtrlFragment().getToolManager().setSignSignatureFieldsWithStamps(true);
                }
            }
        });

        return fragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RequestCode.DIGITAL_SIGNATURE_IMAGE) { // If signature from image file picker
            Uri uri = ViewerUtils.getImageUriFromIntent(data, this, mImageSignature);
            if (uri != null) {
                String signatureFilePath = StampManager.getInstance().createSignatureFromImage(this, uri);
                if (signatureFilePath != null) {
                    mSignatureFileToSign = new File(signatureFilePath);
                }
            }
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        if (mSignatureFileToSign != null) {
            useCustomStamp(fr, mSignatureFileToSign);
            mSignatureFileToSign = null;
        }
    }

    public void cloudSq(View view) {

        useCloudSquare(fr);
    }

    public void custStamp(View view) {
//        useCustomStamp(fr);
        openSignatureDialog();
    }

    public void useCloudSquare(@NonNull PdfViewCtrlTabHostFragment2 fragment) {
        // Create our custom tool
        ToolManager toolManager = fragment.getCurrentPdfViewCtrlFragment().getToolManager();
        ToolManager.Tool customTool = toolManager.createTool(CustomCloudSquare.MODE, toolManager.getTool());
        // Then set it in ToolManager
        toolManager.setTool(customTool);
    }

    public void openSignatureDialog() {
        fr.getCurrentPdfViewCtrlFragment().getToolManager().setShowSavedSignatures(false);
        Signature signature = new Signature(fr.getCurrentPdfViewCtrlFragment().getPDFViewCtrl());
        signature.showSignaturePickerDialog(new OnCreateSignatureListener() {
            @Override
            public void onSignatureCreated(@Nullable String s, boolean b) {
                useCustomStamp(fr, new File(s));
            }

            @Override
            public void onSignatureFromImage(@Nullable PointF targetPoint, int targetPage, @Nullable Long widget) {
                mImageSignature = ViewerUtils.openImageIntent(MainActivity.this, RequestCode.DIGITAL_SIGNATURE_IMAGE);
            }

            @Override
            public void onAnnotStyleDialogFragmentDismissed(AnnotStyleDialogFragment annotStyleDialogFragment) {

            }
        }, new OnDialogDismissListener() {
            @Override
            public void onDialogDismiss() {

            }
        });
    }

    public void useCustomStamp(@NonNull PdfViewCtrlTabHostFragment2 fragment, File signatureFile) {
        // demo custom signature
        ToolManager toolManager = fragment.getCurrentPdfViewCtrlFragment().getToolManager();
        ToolManager.Tool customTool = toolManager.createTool(CustomSignature.MODE, toolManager.getTool());
        if (customTool instanceof CustomSignature) {
            // sign on field
//            ((CustomSignature) customTool).signLastSavedSignatureToField(8, signatureFile);
            // sign at location
            ((CustomSignature) customTool).signAtLocation(signatureFile, 8, 0, 0);
        }
    }
}
