package com.pdftron.android.pdfviewctrlviewer;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.Bookmark;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ToolManagerBuilder;
import com.pdftron.pdf.controls.AnnotationDialogFragment;
import com.pdftron.pdf.controls.BookmarksTabLayout;
import com.pdftron.pdf.controls.OutlineDialogFragment;
import com.pdftron.pdf.controls.ThumbnailSlider;
import com.pdftron.pdf.controls.UserBookmarkDialogFragment;
import com.pdftron.pdf.dialog.BookmarksDialogFragment;
import com.pdftron.pdf.dialog.annotlist.AnnotationListSortOrder;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AppUtils;
import com.pdftron.pdf.utils.DialogFragmentTab;
import com.pdftron.pdf.utils.PdfViewCtrlSettingsManager;
import com.pdftron.pdf.utils.ThemeProvider;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.widget.preset.component.PresetBarComponent;
import com.pdftron.pdf.widget.preset.component.PresetBarViewModel;
import com.pdftron.pdf.widget.preset.component.view.PresetBarView;
import com.pdftron.pdf.widget.preset.signature.SignatureViewModel;
import com.pdftron.pdf.widget.toolbar.ToolManagerViewModel;
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType;
import com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarComponent;
import com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarViewModel;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;
import com.pdftron.pdf.widget.toolbar.component.view.AnnotationToolbarView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        BookmarksDialogFragment.BookmarksDialogListener,
        BookmarksTabLayout.BookmarksTabsListener {
    private static final String TAG = MainActivity.class.getName();

    private PDFViewCtrl mPdfViewCtrl;
    private PDFDoc mPdfDoc;
    private ToolManager mToolManager;
    private AnnotationToolbarComponent mAnnotationToolbarComponent;
    private PresetBarComponent mPresetBarComponent;
    private FrameLayout mToolbarContainer;
    private FrameLayout mPresetContainer;
    private ThumbnailSlider mThumbnailSlider;

    private BookmarksDialogFragment mBookmarksDialog;
    private int mBookmarkDialogCurrentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPdfViewCtrl = findViewById(R.id.pdfviewctrl);
        mToolbarContainer = findViewById(R.id.annotation_toolbar_container);
        mPresetContainer = findViewById(R.id.preset_container);
        mThumbnailSlider = findViewById(R.id.thumbnail_slider);
        setupToolManager();
        setupAnnotationToolbar();
        try {
            AppUtils.setupPDFViewCtrl(mPdfViewCtrl);
            viewFromResource(R.raw.sample, "sample_file");
        } catch (PDFNetException e) {
            Log.e(TAG, "Error setting up PDFViewCtrl");
        }
    }

    /**
     * Helper method to set up and initialize the ToolManager.
     */
    public void setupToolManager() {
        mToolManager = ToolManagerBuilder.from()
                .build(this, mPdfViewCtrl);
    }

    /**
     * Helper method to set up and initialize the AnnotationToolbar.
     */
    public void setupAnnotationToolbar() {
        ToolManagerViewModel toolManagerViewModel = ViewModelProviders.of(this).get(ToolManagerViewModel.class);
        toolManagerViewModel.setToolManager(mToolManager);
        SignatureViewModel signatureViewModel = ViewModelProviders.of(this).get(SignatureViewModel.class);
        PresetBarViewModel presetViewModel = ViewModelProviders.of(this).get(PresetBarViewModel.class);
        AnnotationToolbarViewModel annotationToolbarViewModel = ViewModelProviders.of(this).get(AnnotationToolbarViewModel.class);

        // Create our UI components for the annotation toolbar annd preset bar
        mAnnotationToolbarComponent = new AnnotationToolbarComponent(
                this,
                annotationToolbarViewModel,
                presetViewModel,
                toolManagerViewModel,
                new AnnotationToolbarView(mToolbarContainer)
        );

        mPresetBarComponent = new PresetBarComponent(
                this,
                getSupportFragmentManager(),
                presetViewModel,
                toolManagerViewModel,
                signatureViewModel,
                new PresetBarView(mPresetContainer)
        );

        // Create our custom toolbar and pass it to the annotation toolbar UI component
        mAnnotationToolbarComponent.inflateWithBuilder(
                AnnotationToolbarBuilder.withTag("Custom Toolbar")
                        .addToolButton(ToolbarButtonType.SQUARE, DefaultToolbars.ButtonId.SQUARE.value())
                        .addToolButton(ToolbarButtonType.INK, DefaultToolbars.ButtonId.INK.value())
                        .addToolButton(ToolbarButtonType.FREE_HIGHLIGHT, DefaultToolbars.ButtonId.FREE_HIGHLIGHT.value())
                        .addToolButton(ToolbarButtonType.ERASER, DefaultToolbars.ButtonId.ERASER.value())
                        .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
                        .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value())
        );

        mPdfViewCtrl.addPageChangeListener(new PDFViewCtrl.PageChangeListener() {
            @Override
            public void onPageChange(int oldPage, int curPage, PDFViewCtrl.PageChangeState state) {
                mThumbnailSlider.setProgress(curPage);
            }
        });
        mThumbnailSlider.setOnMenuItemClickedListener(new ThumbnailSlider.OnMenuItemClickedListener() {
            @Override
            public void onMenuItemClicked(int menuItemPosition) {
                if (menuItemPosition == ThumbnailSlider.POSITION_RIGHT) {
                    onNavigationListSelected();
                }
            }
        });
    }

    /**
     * Helper method to view a PDF document from resource
     *
     * @param resourceId of the sample PDF file
     * @param fileName   of the temporary PDF file copy
     * @throws PDFNetException if invalid document path is supplied to PDFDoc
     */
    public void viewFromResource(int resourceId, String fileName) throws PDFNetException {
        File file = Utils.copyResourceToLocal(this, resourceId, fileName, ".pdf");
        mPdfDoc = new PDFDoc(file.getAbsolutePath());
        mPdfViewCtrl.setDoc(mPdfDoc);
        // Alternatively, you can open the document using Uri:
        // Uri fileUri = Uri.fromFile(file);
        // mPdfDoc = mPdfViewCtrl.openPDFUri(fileUri, null);
    }

    /**
     * We need to clean up and handle PDFViewCtrl based on Android lifecycle callback.
     */

    @Override
    protected void onPause() {
        super.onPause();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.pause();
            mPdfViewCtrl.purgeMemory();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.destroy();
            mPdfViewCtrl = null;
        }

        if (mPdfDoc != null) {
            try {
                mPdfDoc.close();
            } catch (Exception e) {
                // handle exception
            } finally {
                mPdfDoc = null;
            }
        }
    }

    private void onNavigationListSelected() {
        mBookmarksDialog = BookmarksDialogFragment.newInstance(BookmarksDialogFragment.DialogMode.DIALOG);

        mBookmarksDialog.setPdfViewCtrl(mPdfViewCtrl)
                .setDialogFragmentTabs(getBookmarksDialogTabs(), mBookmarkDialogCurrentTab);
        mBookmarksDialog.setBookmarksDialogListener(this);
        mBookmarksDialog.setBookmarksTabsListener(this);
        mBookmarksDialog.setStyle(DialogFragment.STYLE_NO_TITLE, (new ThemeProvider()).getTheme());
        mBookmarksDialog.show(getSupportFragmentManager(), "bookmarks_dialog");
    }

    private ArrayList<DialogFragmentTab> getBookmarksDialogTabs() {
        DialogFragmentTab userBookmarkTab = createUserBookmarkDialogTab();
        DialogFragmentTab outlineTab = createOutlineDialogTab();
        DialogFragmentTab annotationTab = createAnnotationDialogTab();
        ArrayList<DialogFragmentTab> dialogFragmentTabs = new ArrayList<>(3);
        dialogFragmentTabs.add(userBookmarkTab);
        dialogFragmentTabs.add(outlineTab);
        dialogFragmentTabs.add(annotationTab);
        return dialogFragmentTabs;
    }

    private DialogFragmentTab createUserBookmarkDialogTab() {
        Bundle bundle = new Bundle();
        boolean readonly = false;
        boolean allowEditing = true;
        boolean allowBookmarkCreation = true;
        boolean autoSort = true;
        bundle.putBoolean(UserBookmarkDialogFragment.BUNDLE_IS_READ_ONLY, readonly);
        bundle.putBoolean(UserBookmarkDialogFragment.BUNDLE_ALLOW_EDITING, allowEditing);
        bundle.putBoolean(UserBookmarkDialogFragment.BUNDLE_BOOKMARK_CREATION_ENABLED, allowBookmarkCreation);
        bundle.putBoolean(UserBookmarkDialogFragment.BUNDLE_AUTO_SORT_BOOKMARKS, autoSort);
        return new DialogFragmentTab(UserBookmarkDialogFragment.class,
                BookmarksTabLayout.TAG_TAB_BOOKMARK,
                Utils.getDrawable(this, com.pdftron.pdf.tools.R.drawable.ic_bookmarks_white_24dp),
                null,
                getString(com.pdftron.pdf.tools.R.string.bookmark_dialog_fragment_bookmark_tab_title),
                bundle,
                com.pdftron.pdf.tools.R.menu.fragment_user_bookmark);
    }

    /**
     * Creates the outline dialog fragment tab
     *
     * @return The outline dialog fragment tab
     */
    private DialogFragmentTab createOutlineDialogTab() {
        Bundle bundle = new Bundle();
        boolean allowEditing = true;
        bundle.putBoolean(OutlineDialogFragment.BUNDLE_EDITING_ENABLED, allowEditing);
        return new DialogFragmentTab(OutlineDialogFragment.class,
                BookmarksTabLayout.TAG_TAB_OUTLINE,
                Utils.getDrawable(this, com.pdftron.pdf.tools.R.drawable.ic_outline_white_24dp),
                null,
                getString(com.pdftron.pdf.tools.R.string.bookmark_dialog_fragment_outline_tab_title),
                bundle,
                com.pdftron.pdf.tools.R.menu.fragment_outline);
    }

    /**
     * Creates the annotation dialog fragment tab
     *
     * @return The annotation dialog fragment tab
     */
    private DialogFragmentTab createAnnotationDialogTab() {
        Bundle bundle = new Bundle();
        boolean readonly = true;
        boolean isRTL = false;
        boolean filterEnabled = true;
        bundle.putBoolean(AnnotationDialogFragment.BUNDLE_IS_READ_ONLY, readonly);
        bundle.putBoolean(AnnotationDialogFragment.BUNDLE_IS_RTL, isRTL);
        bundle.putInt(AnnotationDialogFragment.BUNDLE_KEY_SORT_MODE,
                PdfViewCtrlSettingsManager.getAnnotListSortOrder(this,
                        AnnotationListSortOrder.DATE_ASCENDING) // default sort order
        );
        bundle.putBoolean(AnnotationDialogFragment.BUNDLE_ENABLE_ANNOTATION_FILTER, filterEnabled);
        return new DialogFragmentTab(AnnotationDialogFragment.class,
                BookmarksTabLayout.TAG_TAB_ANNOTATION,
                Utils.getDrawable(this, com.pdftron.pdf.tools.R.drawable.ic_annotations_white_24dp),
                null,
                getString(com.pdftron.pdf.tools.R.string.bookmark_dialog_fragment_annotation_tab_title),
                bundle,
                com.pdftron.pdf.tools.R.menu.fragment_annotlist_sort);
    }

    @Override
    public void onBookmarksDialogWillDismiss(int tabIndex) {
        if (mBookmarksDialog != null) {
            mBookmarksDialog.dismiss();
        }
    }

    @Override
    public void onBookmarksDialogDismissed(int tabIndex) {
        mBookmarkDialogCurrentTab = tabIndex;
    }

    @Override
    public void onUserBookmarkClick(int pageNum) {
        if (mBookmarksDialog != null) {
            mBookmarksDialog.dismiss();
        }
        mPdfViewCtrl.setCurrentPage(pageNum);
    }

    @Override
    public void onOutlineClicked(Bookmark parent, Bookmark bookmark) {
        if (mBookmarksDialog != null) {
            mBookmarksDialog.dismiss();
        }
    }

    @Override
    public void onAnnotationClicked(Annot annotation, int pageNum) {
        if (mBookmarksDialog != null) {
            mBookmarksDialog.dismiss();
        }
    }

    @Override
    public void onExportAnnotations(PDFDoc outputDoc) {

    }

    @Override
    public void onEditBookmarkFocusChanged(boolean isActive) {

    }
}
