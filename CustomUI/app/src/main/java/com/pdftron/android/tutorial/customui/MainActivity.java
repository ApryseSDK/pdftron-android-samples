package com.pdftron.android.tutorial.customui;

import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.pdftron.android.tutorial.customui.custom.CustomAnnotationToolbar;
import com.pdftron.android.tutorial.customui.custom.CustomQuickMenu;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.PDFViewCtrlConfig;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.config.ViewerBuilder;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;
import com.pdftron.pdf.model.FileInfo;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements PdfViewCtrlTabHostFragment.TabHostListener {

    final int MIN_KEYBOARD_HEIGHT_PX = 150;
    private PdfViewCtrlTabHostFragment mPdfViewCtrlTabHostFragment;
    private boolean mNeedToResetMargin;
    ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        private final Rect windowVisibleDisplayFrame = new Rect();
        private int lastVisibleDecorViewHeight;

        @Override
        public void onGlobalLayout() {
            final View decorView = getWindow().getDecorView();
            decorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
            final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

            if (lastVisibleDecorViewHeight != 0) {
                if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                    int currentKeyboardHeight = decorView.getHeight() - windowVisibleDisplayFrame.bottom;
                    // keyboard shown

                    if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
                        final PDFViewCtrl pdfViewCtrl = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPDFViewCtrl();

                        // first check if the field is in view
                        try {
                            ToolManager tm = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getToolManager();
                            if (tm != null && tm.getTool() instanceof FormFill) {
                                FormFill formFill = (FormFill) tm.getTool();
                                Annot ann = formFill.getAnnot();
                                int page = formFill.getPage();
                                if (ann != null) {
                                    com.pdftron.pdf.Rect bbox = pdfViewCtrl.getScreenRectForAnnot(ann, page);
                                    int totalHeight = pdfViewCtrl.getHeight();
                                    int diff = totalHeight - currentKeyboardHeight;
                                    if (bbox.getY1() < diff) {
                                        mNeedToResetMargin = false;
                                        return;
                                    }
                                }
                            } else {
                                // not in form filling
                                return;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        pdfViewCtrl.setPageViewMode(PDFViewCtrl.PageViewMode.ZOOM);

                        View root = findViewById(R.id.fragment_container);
                        if (root != null) {
                            if (root.getLayoutParams() != null && root.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
                                mlp.bottomMargin = currentKeyboardHeight;
                                root.requestLayout();

                                mNeedToResetMargin = true;
                            }

                            root.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        ToolManager tm = mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getToolManager();
                                        if (tm != null && tm.getTool() instanceof FormFill) {
                                            Annot ann = ((FormFill) tm.getTool()).getAnnot();
                                            int page = ((FormFill) tm.getTool()).getPage();
                                            if (ann != null) {
                                                com.pdftron.pdf.Rect bbox = pdfViewCtrl.getScreenRectForAnnot(ann, page);
                                                pdfViewCtrl.scrollTo(pdfViewCtrl.getScrollX(), (int) (pdfViewCtrl.getScrollY() + bbox.getY1()));
                                            }
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }, 100);
                        }
                    }
                } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                    // keyboard hidden

                    if (mNeedToResetMargin) {
                        mNeedToResetMargin = false;
                        if (mPdfViewCtrlTabHostFragment != null && mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment() != null) {
                            mPdfViewCtrlTabHostFragment.getCurrentPdfViewCtrlFragment().getPDFViewCtrl().setPageViewMode(PDFViewCtrl.PageViewMode.ZOOM);
                        }
                        View root = findViewById(R.id.fragment_container);
                        if (root != null) {
                            if (root.getLayoutParams() != null && root.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
                                mlp.bottomMargin = 0;
                                root.requestLayout();
                            }
                        }
                    }
                }
            }
            lastVisibleDecorViewHeight = visibleDecorViewHeight;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate a PdfViewCtrlTabHostFragment with a document Uri
        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pdf");
        Uri uri = Uri.fromFile(f);
        PDFViewCtrlConfig pdfViewCtrlConfig = PDFViewCtrlConfig.getDefaultConfig(this);
        android.graphics.Point displaySize = new android.graphics.Point(0, 0);
        Utils.getDisplaySize(this, displaySize);
        pdfViewCtrlConfig.setThumbnailMaxSideLength(Math.max(displaySize.x, displaySize.y));
        ToolManagerBuilder toolManagerBuilder = ToolManagerBuilder.from()
                .addCustomizedTool(ToolManager.ToolMode.FORM_FILL, FormFill.class);
        ViewerConfig viewerConfig = new ViewerConfig.Builder()
                .toolbarTitle("٩(◕‿◕｡)۶")
                .pdfViewCtrlConfig(pdfViewCtrlConfig)
                .toolManagerBuilder(toolManagerBuilder)
                .build();
        mPdfViewCtrlTabHostFragment = ViewerBuilder.withUri(uri)
                .usingCustomToolbar(new int[]{R.menu.my_custom_options_toolbar})
                .usingNavIcon(R.drawable.ic_star_white_24dp)
                .usingConfig(viewerConfig)
                .build(this);
        mPdfViewCtrlTabHostFragment.addHostListener(this);

        // Add the fragment to our activity
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment);
        ft.commit();
    }

    @Override
    protected void onDestroy() {
        if (mPdfViewCtrlTabHostFragment != null) {
            mPdfViewCtrlTabHostFragment.removeHostListener(this);
        }

        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);

        super.onDestroy();
    }

    @Override
    public void onTabDocumentLoaded(String s) {
        new CustomQuickMenu(MainActivity.this, mPdfViewCtrlTabHostFragment).applyCustomization();
//        new CustomLinkClick(MainActivity.this, mPdfViewCtrlTabHostFragment).applyCustomization();
        new CustomAnnotationToolbar(MainActivity.this, mPdfViewCtrlTabHostFragment).applyCustomization();

        final View decorView = getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
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
