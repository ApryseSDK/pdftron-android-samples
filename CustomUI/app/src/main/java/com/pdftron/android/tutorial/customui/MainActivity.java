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

import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomLinkClick;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.config.ViewerBuilder;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.AnnotEdit;
import com.pdftron.pdf.tools.AnnotEditTextMarkup;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

    private PdfViewCtrlTabHostFragment mPdfViewCtrlTabHostFragment;

    private HashMap<Annot, Integer> mSelectedAnnots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a PdfViewCtrlTabHostFragment with a document Uri
        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
        Uri uri = Uri.fromFile(f);
        ViewerConfig viewerConfig = new ViewerConfig.Builder()
                .toolbarTitle("٩(◕‿◕｡)۶")
                .showBottomNavBar(false)
                .fullscreenModeEnabled(false)
                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder.withUri(uri)
                .usingCustomToolbar(new int[]{R.menu.my_custom_options_toolbar})
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
                .build(this);
        mPdfViewCtrlTabHostFragment.addHostListener(this);

        // Apply customizations to tab host fragment
//        new CustomQuickMenu(MainActivity.this, mPdfViewCtrlTabHostFragment);
//        new CustomLinkClick(MainActivity.this, mPdfViewCtrlTabHostFragment);
//        new CustomAnnotationToolbar(MainActivity.this, mPdfViewCtrlTabHostFragment);

        // custom style editing

        View redBtn = findViewById(R.id.btn_red);
        View blueBtn = findViewById(R.id.btn_blue);
        View yellowBtn = findViewById(R.id.btn_yellow);

        redBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editColor(getResources().getColor(R.color.red));
            }
        });
        blueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editColor(getResources().getColor(R.color.blue));
            }
        });
        yellowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editColor(getResources().getColor(R.color.yellow));
            }
        });

        // Add the fragment to our activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
        ft.commit();
    }

    private void editColor(int color) {
        if (mPdfViewCtrlTabHostFragment == null) {
            return;
        }
        try {
            ToolManager toolManager = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getToolManager();
            Tool currentTool = (Tool) toolManager.getTool();
            if (mSelectedAnnots != null) {
                for (Map.Entry<Annot, Integer> entry : mSelectedAnnots.entrySet()) {
                    Annot annot = entry.getKey();
                    Integer page = entry.getValue();
                    if (currentTool instanceof AnnotEdit) {
                        AnnotEdit tool = (AnnotEdit) currentTool;
                         if (annot.getType() == Annot.e_Ink) {
                            tool.onChangeAnnotStrokeColor(color);
                        } else if (annot.getType() == Annot.e_FreeText) {
                            tool.onChangeAnnotTextColor(color);
                        }
                    } else if (currentTool instanceof AnnotEditTextMarkup) {
                        AnnotEdit annotEdit = (AnnotEdit) toolManager.createTool(ToolManager.ToolMode.ANNOT_EDIT, toolManager.getTool());
                        if (annot.getType() == Annot.e_Highlight) {
                            annotEdit.onChangeAnnotStrokeColor(color);
                        }
                        toolManager.selectAnnot(annot, page);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        if (mPdfViewCtrlTabHostFragment != null) {
            mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getToolManager().addAnnotationsSelectionListener(new ToolManager.AnnotationsSelectionListener() {
                @Override
                public void onAnnotationsSelectionChanged(HashMap<Annot, Integer> hashMap) {
                    mSelectedAnnots = hashMap;
                }
            });
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
