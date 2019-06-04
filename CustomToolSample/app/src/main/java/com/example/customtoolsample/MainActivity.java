package com.example.customtoolsample;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.config.ViewerBuilder;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    PdfViewCtrlTabHostFragment fr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File resource = Utils.copyResourceToLocal(this, R.raw.gettingstarted, "GettingStarted", "pdf");
        fr = addViewerFragment(R.id.myLayout, this, Uri.fromFile(resource), "");

    }

    // Add a viewer fragment to the layout container in the specified
// activity, and returns the added fragment
    public PdfViewCtrlTabHostFragment addViewerFragment(@IdRes int fragmentContainer,
                                                        @NonNull FragmentActivity activity, @NonNull Uri fileUri, @Nullable String password) {


        ToolManagerBuilder toolManagerBuilder = ToolManagerBuilder
                .from()
                .setRealTimeAnnotEdit(false)
                .addCustomizedTool(CustomCloudSquare.MODE, CustomCloudSquare.class)
                .addCustomizedTool(CustomStamp.MODE, CustomStamp.class);

        ViewerConfig config = new ViewerConfig.Builder()
                .toolManagerBuilder(toolManagerBuilder)
                .build();

        // Create the viewer fragment
        PdfViewCtrlTabHostFragment fragment =
                ViewerBuilder.withUri(fileUri, password)
                        .usingConfig(config)
                        .build(activity);

        // Add the fragment to the layout fragment container
        activity.getSupportFragmentManager().beginTransaction()
                .replace(fragmentContainer, fragment)
                .commit();

        return fragment;
    }

    public void cloudSq(View view) {

        useCloudSquare(fr);
    }

    public void custStamp(View view) {
        useCustomStamp(fr);
    }


    public void useCloudSquare(@NonNull PdfViewCtrlTabHostFragment fragment) {
        // Create our custom tool
        ToolManager toolManager = fragment.getCurrentPdfViewCtrlFragment().getToolManager();
        ToolManager.Tool customTool = toolManager.createTool(CustomCloudSquare.MODE, toolManager.getTool());
        // Then set it in ToolManager
        toolManager.setTool(customTool);
    }

    //@data {"m":true}//
    public void useCustomStamp(@NonNull PdfViewCtrlTabHostFragment fragment) {
        // Create our custom tool
        ToolManager toolManager = fragment.getCurrentPdfViewCtrlFragment().getToolManager();
        ToolManager.Tool customTool = toolManager.createTool(CustomStamp.MODE, toolManager.getTool());
        // Then set it in ToolManager
        toolManager.setTool(customTool);
    }

}
