//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.pdftron.pdf.controls;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnKeyListener;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ProgressBar;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar.OnMenuItemClickListener;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import co.paulburke.android.itemtouchhelperdemo.helper.SimpleItemTouchHelperCallback;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Page;
import com.pdftron.pdf.controls.AddPageDialogFragment.OnAddNewPagesListener;
import com.pdftron.pdf.controls.AddPageDialogFragment.PageSize;
import com.pdftron.pdf.controls.ThumbnailsViewAdapter.DocumentFormat;
import com.pdftron.pdf.controls.ThumbnailsViewAdapter.EditPagesListener;
import com.pdftron.pdf.controls.ThumbnailsViewFilterMode.Factory;
import com.pdftron.pdf.dialog.pagelabel.PageLabelDialog;
import com.pdftron.pdf.dialog.pagelabel.PageLabelSetting;
import com.pdftron.pdf.dialog.pagelabel.PageLabelSettingViewModel;
import com.pdftron.pdf.dialog.pagelabel.PageLabelUtils;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.tools.UndoRedoManager;
import com.pdftron.pdf.tools.R.attr;
import com.pdftron.pdf.tools.R.color;
import com.pdftron.pdf.tools.R.dimen;
import com.pdftron.pdf.tools.R.id;
import com.pdftron.pdf.tools.R.layout;
import com.pdftron.pdf.tools.R.menu;
import com.pdftron.pdf.tools.R.string;
import com.pdftron.pdf.tools.R.style;
import com.pdftron.pdf.tools.R.styleable;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.AnalyticsParam;
import com.pdftron.pdf.utils.AnnotUtils;
import com.pdftron.pdf.utils.BookmarkManager;
import com.pdftron.pdf.utils.CommonToast;
import com.pdftron.pdf.utils.DialogGoToPage;
import com.pdftron.pdf.utils.Event;
import com.pdftron.pdf.utils.ExceptionHandlerCallback;
import com.pdftron.pdf.utils.PdfViewCtrlSettingsManager;
import com.pdftron.pdf.utils.ToolbarActionMode;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;
import com.pdftron.pdf.utils.DialogGoToPage.DialogGoToPageListener;
import com.pdftron.pdf.utils.ToolbarActionMode.Callback;
import com.pdftron.pdf.widget.recyclerview.ItemClickHelper;
import com.pdftron.pdf.widget.recyclerview.ItemSelectionHelper;
import com.pdftron.pdf.widget.recyclerview.SimpleRecyclerView;
import com.pdftron.pdf.widget.recyclerview.ItemClickHelper.OnItemClickListener;
import com.pdftron.pdf.widget.recyclerview.ItemClickHelper.OnItemLongClickListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CustomThumbnailsViewFragment extends ThumbnailsViewFragment {
    private static final String BUNDLE_READ_ONLY_DOC = "read_only_doc";
    private static final String BUNDLE_EDIT_MODE = "edit_mode";
    private static final String BUNDLE_OUTPUT_FILE_URI = "output_file_uri";
    private static final String BUNDLE_HIDE_FILTER_MODES = "hide_filter_modes";
    public static final int FILTER_MODE_NORMAL = 0;
    public static final int FILTER_MODE_ANNOTATED = 1;
    public static final int FILTER_MODE_BOOKMARKED = 2;
    private ThumbnailsViewFragment.Theme mTheme;
    protected FloatingActionMenu mFabMenu;
    private Uri mOutputFileUri;
    private boolean mIsReadOnly;
    private boolean mIsReadOnlySave;
    private Integer mInitSelectedItem;
    protected PDFViewCtrl mPdfViewCtrl;
    protected Toolbar mToolbar;
    private Toolbar mCabToolbar;
    protected SimpleRecyclerView mRecyclerView;
    private ThumbnailsViewAdapter mAdapter;
    private ProgressBar mProgressBarView;
    protected ItemSelectionHelper mItemSelectionHelper;
    protected ItemClickHelper mItemClickHelper;
    protected ItemTouchHelper mItemTouchHelper;
    protected ToolbarActionMode mActionMode;
    private MenuItem mMenuItemUndo;
    private MenuItem mMenuItemRedo;
    private MenuItem mMenuItemRotate;
    private MenuItem mMenuItemDelete;
    private MenuItem mMenuItemDuplicate;
    private MenuItem mMenuItemExport;
    private MenuItem mMenuItemPageLabel;
    private MenuItem mMenuItemEdit;
    private MenuItem mMenuItemAddBookmark;
    private MenuItem mMenuItemRemoveBookmark;
    protected MenuItem mMenuItemFilter;
    protected MenuItem mMenuItemFilterAll;
    protected MenuItem mMenuItemFilterAnnotated;
    protected MenuItem mMenuItemFilterBookmarked;
    private int mSpanCount;
    private String mTitle = "";
    private boolean mHasEventAction;
    private boolean mAddDocPagesDelay;
    private int mPositionDelay;
    private DocumentFormat mDocumentFormatDelay;
    private Object mDataDelay;
    private ThumbnailsViewFragment.OnThumbnailsViewDialogDismissListener mOnThumbnailsViewDialogDismissListener;
    private ThumbnailsViewFragment.OnThumbnailsEditAttemptWhileReadOnlyListener mOnThumbnailsEditAttemptWhileReadOnlyListener;
    private ThumbnailsViewFragment.OnExportThumbnailsListener mOnExportThumbnailsListener;
    private final CompositeDisposable mDisposables = new CompositeDisposable();
    protected ThumbnailsViewFilterMode mFilterMode;
    private boolean mStartInEdit;
    @Nullable
    private ArrayList<Integer> mHideFilterModes;
    private final Callback mActionModeCallback = new Callback() {
        public boolean onCreateActionMode(ToolbarActionMode mode, Menu menu) {
            mode.inflateMenu(com.pdftron.pdf.tools.R.menu.cab_controls_fragment_thumbnails_view);
            CustomThumbnailsViewFragment.this.mMenuItemUndo = menu.findItem(id.controls_thumbnails_view_action_undo);
            CustomThumbnailsViewFragment.this.mMenuItemRedo = menu.findItem(id.controls_thumbnails_view_action_redo);
            CustomThumbnailsViewFragment.this.mMenuItemRotate = menu.findItem(id.controls_thumbnails_view_action_rotate);
            CustomThumbnailsViewFragment.this.mMenuItemDelete = menu.findItem(id.controls_thumbnails_view_action_delete);
            CustomThumbnailsViewFragment.this.mMenuItemDuplicate = menu.findItem(id.controls_thumbnails_view_action_duplicate);
            CustomThumbnailsViewFragment.this.mMenuItemExport = menu.findItem(id.controls_thumbnails_view_action_export);
            CustomThumbnailsViewFragment.this.mMenuItemPageLabel = menu.findItem(id.controls_thumbnails_view_action_page_label);
            CustomThumbnailsViewFragment.this.mMenuItemRemoveBookmark = menu.findItem(id.controls_thumbnails_view_action_remove_bookmark);
            if (CustomThumbnailsViewFragment.this.isNormalFilterMode()) {
                if (CustomThumbnailsViewFragment.this.mMenuItemRemoveBookmark != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemRemoveBookmark.setVisible(false);
                }

                if (CustomThumbnailsViewFragment.this.mMenuItemExport != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemExport.setVisible(CustomThumbnailsViewFragment.this.mOnExportThumbnailsListener != null);
                }
            } else if (CustomThumbnailsViewFragment.this.isBookmarkFilterMode()) {
                if (CustomThumbnailsViewFragment.this.mMenuItemUndo != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemUndo.setVisible(false);
                }

                if (CustomThumbnailsViewFragment.this.mMenuItemRedo != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemRedo.setVisible(false);
                }

                if (CustomThumbnailsViewFragment.this.mMenuItemRotate != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemRotate.setVisible(false);
                }

                if (CustomThumbnailsViewFragment.this.mMenuItemDelete != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemDelete.setVisible(false);
                }

                if (CustomThumbnailsViewFragment.this.mMenuItemDuplicate != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemDuplicate.setVisible(false);
                }

                if (CustomThumbnailsViewFragment.this.mMenuItemExport != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemExport.setVisible(false);
                }

                if (CustomThumbnailsViewFragment.this.mMenuItemPageLabel != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemPageLabel.setVisible(false);
                }
            }

            return true;
        }

        public boolean onPrepareActionMode(ToolbarActionMode mode, Menu menu) {
            boolean isEnabled = CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemCount() > 0;
            if (CustomThumbnailsViewFragment.this.mMenuItemRotate != null) {
                CustomThumbnailsViewFragment.this.mMenuItemRotate.setEnabled(isEnabled);
                if (CustomThumbnailsViewFragment.this.mMenuItemRotate.getIcon() != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemRotate.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }

            if (CustomThumbnailsViewFragment.this.mMenuItemDelete != null) {
                CustomThumbnailsViewFragment.this.mMenuItemDelete.setEnabled(isEnabled);
                if (CustomThumbnailsViewFragment.this.mMenuItemDelete.getIcon() != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemDelete.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }

            if (CustomThumbnailsViewFragment.this.mMenuItemDuplicate != null) {
                CustomThumbnailsViewFragment.this.mMenuItemDuplicate.setEnabled(isEnabled);
                if (CustomThumbnailsViewFragment.this.mMenuItemDuplicate.getIcon() != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemDuplicate.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }

            if (CustomThumbnailsViewFragment.this.mMenuItemExport != null) {
                CustomThumbnailsViewFragment.this.mMenuItemExport.setEnabled(isEnabled);
                if (CustomThumbnailsViewFragment.this.mMenuItemExport.getIcon() != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemExport.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }

            if (CustomThumbnailsViewFragment.this.mMenuItemPageLabel != null) {
                CustomThumbnailsViewFragment.this.mMenuItemPageLabel.setEnabled(isEnabled);
                if (CustomThumbnailsViewFragment.this.mMenuItemPageLabel.getIcon() != null) {
                    CustomThumbnailsViewFragment.this.mMenuItemPageLabel.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }

            if (CustomThumbnailsViewFragment.this.mMenuItemRemoveBookmark != null) {
                CustomThumbnailsViewFragment.this.mMenuItemRemoveBookmark.setEnabled(isEnabled);
            }

            if (!Utils.isTablet(CustomThumbnailsViewFragment.this.getContext()) && CustomThumbnailsViewFragment.this.getResources().getConfiguration().orientation != 2) {
                mode.setTitle(Utils.getLocaleDigits(Integer.toString(CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemCount())));
            } else {
                mode.setTitle(CustomThumbnailsViewFragment.this.getString(string.controls_thumbnails_view_selected, new Object[]{Utils.getLocaleDigits(Integer.toString(CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemCount()))}));
            }

            CustomThumbnailsViewFragment.this.updateUndoRedoIcons();
            return true;
        }


        public boolean onActionItemClicked(ToolbarActionMode mode, MenuItem item) {
            if (CustomThumbnailsViewFragment.this.mPdfViewCtrl == null) {
                throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
            } else {
                SparseBooleanArray selectedItemsx;
                int position;
                int numPages;
                if (item.getItemId() == id.controls_thumbnails_view_action_rotate) {
                    if (CustomThumbnailsViewFragment.this.mIsReadOnly) {
                        if (CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener != null) {
                            CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                        }

                        return true;
                    }

                    selectedItemsx = CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemPositions();
                    List<Integer> pageListx = new ArrayList();

                    for(position = 0; position < selectedItemsx.size(); ++position) {
                        if (selectedItemsx.valueAt(position)) {
                            numPages = selectedItemsx.keyAt(position);
                            CustomThumbnailsViewFragment.this.mAdapter.rotateDocPage(numPages + 1);
                            pageListx.add(numPages + 1);
                        }
                    }

                    CustomThumbnailsViewFragment.this.manageRotatePages(pageListx);
                    CustomThumbnailsViewFragment.this.mHasEventAction = true;
                    AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewCountParam(2, selectedItemsx.size()));
                } else {
                    int pagex;
                    ArrayList pageListxx;
                    SparseBooleanArray selectedItems;
                    if (item.getItemId() == id.controls_thumbnails_view_action_delete) {
                        if (CustomThumbnailsViewFragment.this.mIsReadOnly) {
                            if (CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener != null) {
                                CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                            }

                            return true;
                        }

                        pageListxx = new ArrayList();
                        selectedItems = CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemPositions();
                        boolean shouldUnlockRead = false;

                        label498: {
                            boolean var28;
                            try {
                                CustomThumbnailsViewFragment.this.mPdfViewCtrl.docLockRead();
                                shouldUnlockRead = true;
                                position = CustomThumbnailsViewFragment.this.mPdfViewCtrl.getDoc().getPageCount();
                                break label498;
                            } catch (Exception var17) {
                                AnalyticsHandlerAdapter.getInstance().sendException(var17);
                                var28 = true;
                            } finally {
                                if (shouldUnlockRead) {
                                    CustomThumbnailsViewFragment.this.mPdfViewCtrl.docUnlockRead();
                                }

                            }

                            return var28;
                        }

                        if (selectedItems.size() >= position) {
                            CommonToast.showText(CustomThumbnailsViewFragment.this.getContext(), string.controls_thumbnails_view_delete_msg_all_pages);
                            CustomThumbnailsViewFragment.this.clearSelectedList();
                            return true;
                        }

                        for(pagex = 0; pagex < selectedItems.size(); ++pagex) {
                            if (selectedItems.valueAt(pagex)) {
                                pageListxx.add(selectedItems.keyAt(pagex) + 1);
                            }
                        }

                        Collections.sort(pageListxx, Collections.reverseOrder());
                        pagex = pageListxx.size();

                        for(int ix = 0; ix < pagex; ++ix) {
                            CustomThumbnailsViewFragment.this.mAdapter.removeDocPage((Integer)pageListxx.get(ix));
                        }

                        CustomThumbnailsViewFragment.this.clearSelectedList();
                        CustomThumbnailsViewFragment.this.manageDeletePages(pageListxx);
                        CustomThumbnailsViewFragment.this.mHasEventAction = true;
                        AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewCountParam(3, selectedItems.size()));
                    } else if (item.getItemId() == id.controls_thumbnails_view_action_duplicate) {
                        if (CustomThumbnailsViewFragment.this.mAdapter != null) {
                            pageListxx = new ArrayList();
                            selectedItems = CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemPositions();

                            for(position = 0; position < selectedItems.size(); ++position) {
                                if (selectedItems.valueAt(position)) {
                                    pageListxx.add(selectedItems.keyAt(position) + 1);
                                }
                            }

                            CustomThumbnailsViewFragment.this.mAdapter.duplicateDocPages(pageListxx);
                            CustomThumbnailsViewFragment.this.mHasEventAction = true;
                            AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewCountParam(1, selectedItems.size()));
                        }
                    } else if (item.getItemId() == id.controls_thumbnails_view_action_export) {
                        if (CustomThumbnailsViewFragment.this.mOnExportThumbnailsListener != null) {
                            selectedItemsx = CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemPositions();
                            CustomThumbnailsViewFragment.this.mOnExportThumbnailsListener.onExportThumbnails(selectedItemsx);
                            CustomThumbnailsViewFragment.this.mHasEventAction = true;
                            AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewCountParam(4, selectedItemsx.size()));
                        }
                    } else {
                        int i;
                        if (item.getItemId() != id.controls_thumbnails_view_action_page_label) {
                            ToolManager toolManager;
                            String redoInfo;
                            List pageList;
                            if (item.getItemId() == id.controls_thumbnails_view_action_undo) {
                                toolManager = (ToolManager)CustomThumbnailsViewFragment.this.mPdfViewCtrl.getToolManager();
                                if (toolManager != null) {
                                    redoInfo = toolManager.getUndoRedoManger().undo(3, true);
                                    CustomThumbnailsViewFragment.this.updateUndoRedoIcons();
                                    if (!Utils.isNullOrEmpty(redoInfo)) {
                                        try {
                                            if (UndoRedoManager.isDeletePagesAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                pageList = UndoRedoManager.getPageList(redoInfo);
                                                if (pageList.size() != 0) {
                                                    CustomThumbnailsViewFragment.this.mAdapter.updateAfterAddition(pageList);
                                                }
                                            } else if (UndoRedoManager.isAddPagesAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                pageList = UndoRedoManager.getPageList(redoInfo);
                                                if (pageList.size() != 0) {
                                                    CustomThumbnailsViewFragment.this.mAdapter.updateAfterDeletion(pageList);
                                                }
                                            } else if (UndoRedoManager.isRotatePagesAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                pageList = UndoRedoManager.getPageList(redoInfo);
                                                if (pageList.size() != 0) {
                                                    CustomThumbnailsViewFragment.this.mAdapter.updateAfterRotation(pageList);
                                                }
                                            } else if (UndoRedoManager.isMovePageAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                CustomThumbnailsViewFragment.this.mAdapter.updateAfterMove(UndoRedoManager.getPageTo(redoInfo), UndoRedoManager.getPageFrom(redoInfo));
                                            } else if (UndoRedoManager.isEditPageLabelsAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                CustomThumbnailsViewFragment.this.mAdapter.updateAfterPageLabelEdit();
                                            }
                                        } catch (Exception var16) {
                                            AnalyticsHandlerAdapter.getInstance().sendException(var16);
                                        }
                                    }
                                }
                            } else if (item.getItemId() == id.controls_thumbnails_view_action_redo) {
                                toolManager = (ToolManager)CustomThumbnailsViewFragment.this.mPdfViewCtrl.getToolManager();
                                if (toolManager != null) {
                                    redoInfo = toolManager.getUndoRedoManger().redo(3, true);
                                    CustomThumbnailsViewFragment.this.updateUndoRedoIcons();
                                    if (!Utils.isNullOrEmpty(redoInfo)) {
                                        try {
                                            if (UndoRedoManager.isDeletePagesAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                pageList = UndoRedoManager.getPageList(redoInfo);
                                                if (pageList.size() != 0) {
                                                    CustomThumbnailsViewFragment.this.mAdapter.updateAfterDeletion(pageList);
                                                }
                                            } else if (UndoRedoManager.isAddPagesAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                pageList = UndoRedoManager.getPageList(redoInfo);
                                                if (pageList.size() != 0) {
                                                    CustomThumbnailsViewFragment.this.mAdapter.updateAfterAddition(pageList);
                                                }
                                            } else if (UndoRedoManager.isRotatePagesAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                pageList = UndoRedoManager.getPageList(redoInfo);
                                                if (pageList.size() != 0) {
                                                    CustomThumbnailsViewFragment.this.mAdapter.updateAfterRotation(pageList);
                                                }
                                            } else if (UndoRedoManager.isMovePageAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                CustomThumbnailsViewFragment.this.mAdapter.updateAfterMove(UndoRedoManager.getPageFrom(redoInfo), UndoRedoManager.getPageTo(redoInfo));
                                            } else if (UndoRedoManager.isEditPageLabelsAction(CustomThumbnailsViewFragment.this.getContext(), redoInfo)) {
                                                CustomThumbnailsViewFragment.this.mAdapter.updateAfterPageLabelEdit();
                                            }
                                        } catch (Exception var15) {
                                            AnalyticsHandlerAdapter.getInstance().sendException(var15);
                                        }
                                    }
                                }
                            } else if (item.getItemId() == id.controls_thumbnails_view_action_remove_bookmark) {
                                selectedItemsx = CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemPositions();

                                for(i = 0; i < selectedItemsx.size(); ++i) {
                                    if (selectedItemsx.valueAt(i)) {
                                        position = selectedItemsx.keyAt(i);
                                        Integer page = CustomThumbnailsViewFragment.this.mAdapter.getItem(position);
                                        if (page != null) {
                                            ViewerUtils.removePageBookmark(CustomThumbnailsViewFragment.this.mPdfViewCtrl.getContext(), CustomThumbnailsViewFragment.this.mIsReadOnlySave, CustomThumbnailsViewFragment.this.mPdfViewCtrl, page);
                                        }
                                    }
                                }

                                if (CustomThumbnailsViewFragment.this.isBookmarkFilterMode()) {
                                    CustomThumbnailsViewFragment.this.populateThumbList(2);
                                }

                                CustomThumbnailsViewFragment.this.finishActionMode();
                            }
                        } else {
                            if (CustomThumbnailsViewFragment.this.mAdapter == null) {
                                return true;
                            }

                            selectedItemsx = CustomThumbnailsViewFragment.this.mItemSelectionHelper.getCheckedItemPositions();
                            i = 2147483647;
                            position = -1;

                            for(numPages = 0; numPages < selectedItemsx.size(); ++numPages) {
                                if (selectedItemsx.valueAt(numPages)) {
                                    pagex = selectedItemsx.keyAt(numPages) + 1;
                                    position = Math.max(pagex, position);
                                    i = Math.min(pagex, i);
                                }
                            }

                            numPages = CustomThumbnailsViewFragment.this.mPdfViewCtrl.getPageCount();
                            if (i < 1 || position < 1 || position < i || i > numPages) {
                                CommonToast.showText(CustomThumbnailsViewFragment.this.getContext(), CustomThumbnailsViewFragment.this.getString(string.page_label_failed), 1);
                                return true;
                            }

                            FragmentActivity activity = CustomThumbnailsViewFragment.this.getActivity();
                            FragmentManager fragManager = CustomThumbnailsViewFragment.this.getFragmentManager();
                            if (fragManager != null && activity != null) {
                                String prefix = PageLabelUtils.getPageLabelPrefix(CustomThumbnailsViewFragment.this.mPdfViewCtrl, i);
                                PageLabelDialog dialog = PageLabelDialog.newInstance(i, position, numPages, prefix);
                                dialog.setStyle(STYLE_NO_TITLE, CustomThumbnailsViewFragment.this.getTheme());
                                dialog.show(fragManager, PageLabelDialog.TAG);
                            }
                        }
                    }
                }

                return true;
            }
        }

        public void onDestroyActionMode(ToolbarActionMode mode) {
            CustomThumbnailsViewFragment.this.mActionMode = null;
            CustomThumbnailsViewFragment.this.clearSelectedList();
        }
    };

    public CustomThumbnailsViewFragment() {
    }

    public static ThumbnailsViewFragment newInstance() {
        return newInstance(false);
    }

    public static ThumbnailsViewFragment newInstance(boolean readOnly) {
        return newInstance(readOnly, false);
    }

    public static ThumbnailsViewFragment newInstance(boolean readOnly, boolean editMode) {
        return newInstance(readOnly, editMode, (int[])null);
    }

    public static ThumbnailsViewFragment newInstance(boolean readOnly, boolean editMode, @Nullable int[] hideFilterModes) {
        ThumbnailsViewFragment fragment = new ThumbnailsViewFragment();
        Bundle args = new Bundle();
        args.putBoolean("read_only_doc", readOnly);
        args.putBoolean("edit_mode", editMode);
        if (hideFilterModes != null) {
            args.putIntArray("hide_filter_modes", hideFilterModes);
        }

        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = this.getContext();
        if (context != null) {
            //this.mTheme = ThumbnailsViewFragment.Theme.fromContext(context);
            if (savedInstanceState != null) {
                this.mOutputFileUri = (Uri)savedInstanceState.getParcelable("output_file_uri");
            }

            int defaultFilterMode = PdfViewCtrlSettingsManager.getThumbListFilterMode(context, 0);
            if (this.getArguments() != null) {
                if (this.getArguments().getBoolean("edit_mode", false)) {
                    this.mStartInEdit = true;
                    defaultFilterMode = 0;
                }

                if (this.getArguments().getIntArray("hide_filter_modes") != null) {
                    int[] hideFilterModesInt = this.getArguments().getIntArray("hide_filter_modes");
                    this.mHideFilterModes = new ArrayList(hideFilterModesInt.length);
                    int[] var5 = hideFilterModesInt;
                    int var6 = hideFilterModesInt.length;

                    for(int var7 = 0; var7 < var6; ++var7) {
                        int mode = var5[var7];
                        this.mHideFilterModes.add(mode);
                    }
                }
            }

            this.mFilterMode = (ThumbnailsViewFilterMode)ViewModelProviders.of(this, new Factory(defaultFilterMode)).get(ThumbnailsViewFilterMode.class);
        }
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(layout.controls_fragment_thumbnails_view, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (this.mPdfViewCtrl != null) {
            if (Utils.isNullOrEmpty(this.mTitle)) {
                this.mTitle = this.getString(string.controls_thumbnails_view_description);
            }

            int viewWidth = this.getDisplayWidth();
            int thumbSize = this.getResources().getDimensionPixelSize(dimen.controls_thumbnails_view_image_width);
            int thumbSpacing = this.getResources().getDimensionPixelSize(dimen.controls_thumbnails_view_grid_spacing);
            this.mSpanCount = (int)Math.floor((double)viewWidth / (double)(thumbSize + thumbSpacing));
            this.mToolbar = (Toolbar)view.findViewById(id.controls_thumbnails_view_toolbar);
            this.mCabToolbar = (Toolbar)view.findViewById(id.controls_thumbnails_view_cab);
            this.mToolbar.setNavigationOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (!CustomThumbnailsViewFragment.this.onBackPressed()) {
                        CustomThumbnailsViewFragment.this.dismiss();
                    }

                }
            });
            if (this.getArguments() != null) {
                Bundle args = this.getArguments();
                this.mIsReadOnly = args.getBoolean("read_only_doc", false);
                this.mIsReadOnlySave = this.mIsReadOnly;
            }

            this.mToolbar.inflateMenu(menu.controls_fragment_thumbnail_browser_toolbar);
            this.mMenuItemEdit = this.mToolbar.getMenu().findItem(id.controls_action_edit);
            if (this.mMenuItemEdit != null) {
                this.mMenuItemEdit.setVisible(!this.mIsReadOnly);
            }

            this.mMenuItemAddBookmark = this.mToolbar.getMenu().findItem(id.controls_thumbnails_view_action_add_bookmark);
            if (this.mMenuItemAddBookmark != null) {
                this.mMenuItemAddBookmark.setVisible(!this.mIsReadOnly);
            }

            this.mMenuItemFilter = this.mToolbar.getMenu().findItem(id.action_filter);
            this.mMenuItemFilterAll = this.mToolbar.getMenu().findItem(id.menu_filter_all);
            this.mMenuItemFilterAnnotated = this.mToolbar.getMenu().findItem(id.menu_filter_annotated);
            this.mMenuItemFilterBookmarked = this.mToolbar.getMenu().findItem(id.menu_filter_bookmarked);
            this.mToolbar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == id.controls_action_edit) {
                        CustomThumbnailsViewFragment.this.startActionMode();
                        return true;
                    } else {
                        if (item.getItemId() == id.action_filter) {
                            if (CustomThumbnailsViewFragment.this.mMenuItemFilterAll != null && CustomThumbnailsViewFragment.this.mMenuItemFilterAnnotated != null && CustomThumbnailsViewFragment.this.mMenuItemFilterBookmarked != null) {
                                Integer mode = CustomThumbnailsViewFragment.this.mFilterMode.getFilterMode();
                                if (mode != null) {
                                    switch(mode) {
                                        case 0:
                                            CustomThumbnailsViewFragment.this.mMenuItemFilterAll.setChecked(true);
                                            break;
                                        case 1:
                                            CustomThumbnailsViewFragment.this.mMenuItemFilterAnnotated.setChecked(true);
                                            break;
                                        case 2:
                                            CustomThumbnailsViewFragment.this.mMenuItemFilterBookmarked.setChecked(true);
                                    }
                                }
                            }
                        } else {
                            if (item.getItemId() == id.menu_filter_all) {
                                CustomThumbnailsViewFragment.this.mFilterMode.publishFilterTypeChange(0);
                                return true;
                            }

                            if (item.getItemId() == id.menu_filter_annotated) {
                                CustomThumbnailsViewFragment.this.mFilterMode.publishFilterTypeChange(1);
                                return true;
                            }

                            if (item.getItemId() == id.menu_filter_bookmarked) {
                                CustomThumbnailsViewFragment.this.mFilterMode.publishFilterTypeChange(2);
                                return true;
                            }

                            if (item.getItemId() == id.controls_thumbnails_view_action_add_bookmark) {
                                Context context = CustomThumbnailsViewFragment.this.getContext();
                                if (context != null) {
                                    DialogGoToPage dlgGotoPage = new DialogGoToPage(context, CustomThumbnailsViewFragment.this.mPdfViewCtrl, new DialogGoToPageListener() {
                                        public void onPageSet(int pageNum) {
                                            ViewerUtils.addPageToBookmark(CustomThumbnailsViewFragment.this.mPdfViewCtrl.getContext(), CustomThumbnailsViewFragment.this.mIsReadOnlySave, CustomThumbnailsViewFragment.this.mPdfViewCtrl, pageNum);
                                            if (CustomThumbnailsViewFragment.this.isBookmarkFilterMode()) {
                                                CustomThumbnailsViewFragment.this.populateThumbList(2);
                                            }

                                        }
                                    });
                                    dlgGotoPage.show(string.action_add_bookmark, string.add, String.valueOf(CustomThumbnailsViewFragment.this.mPdfViewCtrl.getCurrentPage()));
                                }

                                return true;
                            }
                        }

                        return false;
                    }
                }
            });
            this.mToolbar.setTitle(this.mTitle);
            this.mProgressBarView = (ProgressBar)view.findViewById(id.progress_bar_view);
            this.mProgressBarView.setVisibility(View.GONE);
            this.mRecyclerView = (SimpleRecyclerView)view.findViewById(id.controls_thumbnails_view_recycler_view);
            this.mRecyclerView.initView(this.mSpanCount, this.getResources().getDimensionPixelSize(dimen.controls_thumbnails_view_grid_spacing));
            this.mRecyclerView.setItemViewCacheSize(this.mSpanCount * 2);

            try {
                this.mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        if (CustomThumbnailsViewFragment.this.mRecyclerView != null) {
                            try {
                                CustomThumbnailsViewFragment.this.mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } catch (Exception var2) {
                            }

                            if (CustomThumbnailsViewFragment.this.mAdapter != null) {
                                CustomThumbnailsViewFragment.this.mAdapter.updateMainViewWidth(CustomThumbnailsViewFragment.this.getMainViewWidth());
                                CustomThumbnailsViewFragment.this.updateSpanCount(CustomThumbnailsViewFragment.this.mSpanCount);
                            }
                        }
                    }
                });
            } catch (Exception var12) {
                AnalyticsHandlerAdapter.getInstance().sendException(var12);
            }

            this.mItemClickHelper = new ItemClickHelper();
            this.mItemClickHelper.attachToRecyclerView(this.mRecyclerView);
            this.mItemSelectionHelper = new ItemSelectionHelper();
            this.mItemSelectionHelper.attachToRecyclerView(this.mRecyclerView);
            this.mItemSelectionHelper.setChoiceMode(2);
            this.mAdapter = new ThumbnailsViewAdapter(this.getActivity(), this, this.getFragmentManager(), this.mPdfViewCtrl, (List)null, this.mSpanCount, this.mItemSelectionHelper, this.mTheme);
            this.mAdapter.registerAdapterDataObserver(this.mItemSelectionHelper.getDataObserver());
            this.mAdapter.updateMainViewWidth(this.getMainViewWidth());
            this.mRecyclerView.setAdapter(this.mAdapter);
            this.mFilterMode.observeFilterTypeChanges(this.getViewLifecycleOwner(), new Observer<Integer>() {
                public void onChanged(Integer mode) {
                    if (mode != null) {
                        CustomThumbnailsViewFragment.this.populateThumbList(mode);
                        CustomThumbnailsViewFragment.this.updateSharedPrefs(mode);
                        switch(mode) {
                            case 0:
                                CustomThumbnailsViewFragment.this.mToolbar.setTitle(CustomThumbnailsViewFragment.this.mTitle);
                                CustomThumbnailsViewFragment.this.mIsReadOnly = CustomThumbnailsViewFragment.this.mIsReadOnlySave;
                                break;
                            case 1:
                                CustomThumbnailsViewFragment.this.mToolbar.setTitle(String.format("%s (%s)", CustomThumbnailsViewFragment.this.mTitle, CustomThumbnailsViewFragment.this.getResources().getString(string.action_filter_thumbnails_annotated)));
                                CustomThumbnailsViewFragment.this.mIsReadOnly = true;
                                break;
                            case 2:
                                CustomThumbnailsViewFragment.this.mToolbar.setTitle(String.format("%s (%s)", CustomThumbnailsViewFragment.this.mTitle, CustomThumbnailsViewFragment.this.getResources().getString(string.action_filter_thumbnails_bookmarked)));
                                CustomThumbnailsViewFragment.this.mIsReadOnly = CustomThumbnailsViewFragment.this.mIsReadOnlySave;
                        }

                        CustomThumbnailsViewFragment.this.updateReadOnlyUI();
                    }

                }
            });
            this.mItemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(this.mAdapter, this.mSpanCount, false, false));
            this.mItemTouchHelper.attachToRecyclerView(this.mRecyclerView);
            this.mItemClickHelper.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(RecyclerView recyclerView, View v, final int position, final long id) {
                    if (CustomThumbnailsViewFragment.this.mActionMode == null) {
                        int page = CustomThumbnailsViewFragment.this.mAdapter.getItem(position);
                        CustomThumbnailsViewFragment.this.mAdapter.setCurrentPage(page);
                        CustomThumbnailsViewFragment.this.mHasEventAction = true;
                        AnalyticsHandlerAdapter.getInstance().sendEvent(30, AnalyticsParam.viewerNavigateByParam(4));
                        CustomThumbnailsViewFragment.this.dismiss();
                    } else {
                        CustomThumbnailsViewFragment.this.mItemSelectionHelper.setItemChecked(position, !CustomThumbnailsViewFragment.this.mItemSelectionHelper.isItemChecked(position));
                        CustomThumbnailsViewFragment.this.mActionMode.invalidate();
                    }

                }
            });
            this.mItemClickHelper.setOnItemLongClickListener(new OnItemLongClickListener() {
                public boolean onItemLongClick(RecyclerView recyclerView, View v, final int position, final long id) {
                    if (CustomThumbnailsViewFragment.this.mIsReadOnly) {
                        if (CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener != null) {
                            CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                        }

                        return true;
                    } else {
                        if (CustomThumbnailsViewFragment.this.mActionMode == null) {
                            CustomThumbnailsViewFragment.this.mItemSelectionHelper.setItemChecked(position, true);
                            CustomThumbnailsViewFragment.this.startActionMode();
                        } else if (CustomThumbnailsViewFragment.this.isNormalFilterMode()) {
                            CustomThumbnailsViewFragment.this.mRecyclerView.post(new Runnable() {
                                public void run() {
                                    ViewHolder holder = CustomThumbnailsViewFragment.this.mRecyclerView.findViewHolderForAdapterPosition(position);
                                    if (holder != null && CustomThumbnailsViewFragment.this.mItemTouchHelper != null) {
                                        CustomThumbnailsViewFragment.this.mItemTouchHelper.startDrag(holder);
                                    }

                                }
                            });
                        }

                        return true;
                    }
                }
            });
            Dialog dialog = this.getDialog();
            if (dialog != null) {
                dialog.setOnKeyListener(new OnKeyListener() {
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getKeyCode() == 4 && event.getAction() == 1) {
                            if (CustomThumbnailsViewFragment.this.onBackPressed()) {
                                return true;
                            }

                            dialog.dismiss();
                        }

                        return false;
                    }
                });
            }

            this.mFabMenu = (FloatingActionMenu)view.findViewById(id.fab_menu);
            this.mFabMenu.setClosedOnTouchOutside(true);
            if (this.mIsReadOnly) {
                this.mFabMenu.setVisibility(View.GONE);
            }

            FloatingActionButton pagePdfButton = (FloatingActionButton)this.mFabMenu.findViewById(id.page_pdf);
            pagePdfButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CustomThumbnailsViewFragment.this.mFabMenu.close(true);
                    if (CustomThumbnailsViewFragment.this.mIsReadOnly) {
                        if (CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener != null) {
                            CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                        }

                    } else {
                        boolean shouldUnlockRead = false;

                        try {
                            CustomThumbnailsViewFragment.this.mPdfViewCtrl.docLockRead();
                            shouldUnlockRead = true;
                            Page lastPage = CustomThumbnailsViewFragment.this.mPdfViewCtrl.getDoc().getPage(CustomThumbnailsViewFragment.this.mPdfViewCtrl.getDoc().getPageCount());
                            AddPageDialogFragment addPageDialogFragment = AddPageDialogFragment.newInstance(lastPage.getPageWidth(), lastPage.getPageHeight()).setInitialPageSize(PageSize.Custom);
                            addPageDialogFragment.setOnAddNewPagesListener(new OnAddNewPagesListener() {
                                public void onAddNewPages(Page[] pages) {
                                    if (pages != null && pages.length != 0) {
                                        CustomThumbnailsViewFragment.this.mAdapter.addDocPages(CustomThumbnailsViewFragment.this.getLastSelectedPage(), DocumentFormat.PDF_PAGE, pages);
                                        CustomThumbnailsViewFragment.this.mHasEventAction = true;
                                        AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewCountParam(5, pages.length));
                                    }
                                }
                            });
                            FragmentActivity activity = CustomThumbnailsViewFragment.this.getActivity();
                            if (activity != null) {
                                addPageDialogFragment.show(activity.getSupportFragmentManager(), "add_page_dialog");
                            }
                        } catch (Exception var9) {
                            AnalyticsHandlerAdapter.getInstance().sendException(var9);
                        } finally {
                            if (shouldUnlockRead) {
                                CustomThumbnailsViewFragment.this.mPdfViewCtrl.docUnlockRead();
                            }

                        }

                    }
                }
            });
            FloatingActionButton pdfDocButton = (FloatingActionButton)this.mFabMenu.findViewById(id.pdf_doc);
            pdfDocButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CustomThumbnailsViewFragment.this.mFabMenu.close(true);
                    if (CustomThumbnailsViewFragment.this.mIsReadOnly) {
                        if (CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener != null) {
                            CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                        }

                    } else {
                        CustomThumbnailsViewFragment.this.launchAndroidFilePicker();
                    }
                }
            });
            FloatingActionButton imagePdfButton = (FloatingActionButton)this.mFabMenu.findViewById(id.image_pdf);
            imagePdfButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CustomThumbnailsViewFragment.this.mFabMenu.close(true);
                    if (CustomThumbnailsViewFragment.this.mIsReadOnly) {
                        if (CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener != null) {
                            CustomThumbnailsViewFragment.this.mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                        }

                    } else {
                        CustomThumbnailsViewFragment.this.mOutputFileUri = ViewerUtils.openImageIntent(CustomThumbnailsViewFragment.this);
                    }
                }
            });
            FragmentActivity activity = this.getActivity();
            if (activity != null) {
                PageLabelSettingViewModel mPageLabelViewModel = (PageLabelSettingViewModel)ViewModelProviders.of(activity).get(PageLabelSettingViewModel.class);
                mPageLabelViewModel.observeOnComplete(this.getViewLifecycleOwner(), new Observer<Event<PageLabelSetting>>() {
                    public void onChanged(@Nullable Event<PageLabelSetting> pageLabelSettingEvent) {
                        if (pageLabelSettingEvent != null && !pageLabelSettingEvent.hasBeenHandled()) {
                            boolean isSuccessful = PageLabelUtils.setPageLabel(CustomThumbnailsViewFragment.this.mPdfViewCtrl, (PageLabelSetting)pageLabelSettingEvent.getContentIfNotHandled());
                            if (isSuccessful) {
                                CustomThumbnailsViewFragment.this.mHasEventAction = true;
                                CustomThumbnailsViewFragment.this.mAdapter.updateAfterPageLabelEdit();
                                CustomThumbnailsViewFragment.this.managePageLabelChanged();
                                CommonToast.showText(CustomThumbnailsViewFragment.this.getContext(), CustomThumbnailsViewFragment.this.getString(string.page_label_success), 1);
                            } else {
                                CommonToast.showText(CustomThumbnailsViewFragment.this.getContext(), CustomThumbnailsViewFragment.this.getString(string.page_label_failed), 1);
                            }
                        }

                    }
                });
            }

            this.loadAttributes();
        }
    }

    private void loadAttributes() {
        Context context = this.getContext();
        if (null != context) {
            TypedArray a = context.obtainStyledAttributes((AttributeSet)null, styleable.ThumbnailBrowser, attr.thumbnail_browser, style.ThumbnailBrowserStyle);

            try {
                boolean showFilterMenuItem = a.getBoolean(styleable.ThumbnailBrowser_showFilterMenuItem, true);
                boolean showAnnotatedMenuItem = a.getBoolean(styleable.ThumbnailBrowser_showFilterAnnotated, true);
                boolean showBookmarkedMenuItem = a.getBoolean(styleable.ThumbnailBrowser_showFilterBookmarked, true);
                boolean showAddBookmarkMenuItem = a.getBoolean(styleable.ThumbnailBrowser_showAddBookmarkMenuItem, true);
                if (this.mHideFilterModes != null) {
                    if (this.mHideFilterModes.contains(1)) {
                        showAnnotatedMenuItem = false;
                    }

                    if (this.mHideFilterModes.contains(2)) {
                        showBookmarkedMenuItem = false;
                    }
                }

                if (!showAnnotatedMenuItem && !showBookmarkedMenuItem) {
                    showFilterMenuItem = false;
                }

                if (this.mMenuItemFilter != null) {
                    this.mMenuItemFilter.setVisible(showFilterMenuItem);
                }

                if (this.mMenuItemFilterAnnotated != null) {
                    this.mMenuItemFilterAnnotated.setVisible(showAnnotatedMenuItem);
                }

                if (this.mMenuItemFilterBookmarked != null) {
                    this.mMenuItemFilterBookmarked.setVisible(showBookmarkedMenuItem);
                }

                if (this.mMenuItemAddBookmark != null) {
                    this.mMenuItemAddBookmark.setVisible(showAddBookmarkMenuItem);
                }
            } finally {
                a.recycle();
            }

        }
    }

    protected void startActionMode() {
        this.mActionMode = new ToolbarActionMode(this.getActivity(), this.mCabToolbar);
        this.mActionMode.setMainToolbar(this.mToolbar);
        this.mActionMode.startActionMode(this.mActionModeCallback);
    }

    protected void populateThumbList(final int mode) {
        if (this.mPdfViewCtrl != null && this.mPdfViewCtrl.getDoc() != null) {
            this.mDisposables.clear();
            this.mDisposables.add(getPages(this.mPdfViewCtrl, mode).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnSubscribe(new Consumer<Disposable>() {
                public void accept(Disposable disposable) throws Exception {
                    CustomThumbnailsViewFragment.this.mAdapter.clear();
                    CustomThumbnailsViewFragment.this.mAdapter.notifyDataSetChanged();
                    CustomThumbnailsViewFragment.this.mProgressBarView.setVisibility(View.VISIBLE);
                    CustomThumbnailsViewFragment.this.mRecyclerView.setVisibility(View.GONE);
                }
            }).subscribe(new Consumer<List<Integer>>() {
                public void accept(List<Integer> integers) throws Exception {
                    CustomThumbnailsViewFragment.this.mAdapter.addAll(integers);
                }
            }, new Consumer<Throwable>() {
                public void accept(Throwable throwable) throws Exception {
                    CustomThumbnailsViewFragment.this.mProgressBarView.setVisibility(View.GONE);
                    CommonToast.showText(CustomThumbnailsViewFragment.this.getActivity(), string.error_generic_message, 0);
                    AnalyticsHandlerAdapter.getInstance().sendException(new RuntimeException(throwable));
                }
            }, new Action() {
                public void run() throws Exception {
                    CustomThumbnailsViewFragment.this.updateUIVisibilityOnLoadComplete();
                    if (CustomThumbnailsViewFragment.this.mRecyclerView != null && CustomThumbnailsViewFragment.this.mAdapter != null && CustomThumbnailsViewFragment.this.mPdfViewCtrl != null) {
                        int pos = CustomThumbnailsViewFragment.this.mAdapter.getPositionForPage(CustomThumbnailsViewFragment.this.mPdfViewCtrl.getCurrentPage());
                        if (pos >= 0 && pos < CustomThumbnailsViewFragment.this.mAdapter.getItemCount()) {
                            CustomThumbnailsViewFragment.this.mRecyclerView.scrollToPosition(pos);
                        }
                    }

                    if (CustomThumbnailsViewFragment.this.mStartInEdit) {
                        CustomThumbnailsViewFragment.this.mStartInEdit = false;
                        if (mode == 0) {
                            CustomThumbnailsViewFragment.this.startActionMode();
                            if (CustomThumbnailsViewFragment.this.mInitSelectedItem != null) {
                                CustomThumbnailsViewFragment.this.mItemSelectionHelper.setItemChecked(CustomThumbnailsViewFragment.this.mInitSelectedItem, true);
                                CustomThumbnailsViewFragment.this.mActionMode.invalidate();
                                CustomThumbnailsViewFragment.this.mInitSelectedItem = null;
                            }
                        }
                    }

                }
            }));
        }
    }

    protected void updateUIVisibilityOnLoadComplete() {
        this.mProgressBarView.setVisibility(View.GONE);
        this.mRecyclerView.setVisibility(View.VISIBLE);
    }

    public static Observable<List<Integer>> getPages(final PDFViewCtrl pdfViewCtrl, final int mode) {
        return Observable.create(new ObservableOnSubscribe<List<Integer>>() {
            public void subscribe(@NonNull ObservableEmitter<List<Integer>> emitter) throws Exception {
                if (pdfViewCtrl == null) {
                    emitter.onComplete();
                } else {
                    boolean shouldUnlockRead = false;

                    try {
                        pdfViewCtrl.docLockRead();
                        shouldUnlockRead = true;
                        ArrayList<Integer> excludeList = new ArrayList();
                        excludeList.add(1);
                        excludeList.add(19);
                        ArrayList<Integer> bookmarkedPages = new ArrayList();
                        if (mode == 2) {
                            try {
                                bookmarkedPages = BookmarkManager.getPdfBookmarkedPageNumbers(pdfViewCtrl.getDoc());
                            } catch (Exception var13) {
                            }
                        }

                        int pageCount = pdfViewCtrl.getDoc().getPageCount();

                        for(int pageNum = 1; pageNum <= pageCount; ++pageNum) {
                            boolean canAdd = true;
                            if (mode == 1) {
                                int annotCount = AnnotUtils.getAnnotationCountOnPage(pdfViewCtrl, pageNum, excludeList);
                                canAdd = annotCount > 0;
                            }

                            if (mode == 2) {
                                canAdd = bookmarkedPages.contains(pageNum);
                            }

                            if (canAdd) {
                                ArrayList<Integer> pages = new ArrayList();
                                pages.add(pageNum);
                                emitter.onNext(pages);
                            }
                        }
                    } catch (Exception var14) {
                        emitter.onError(var14);
                    } finally {
                        if (shouldUnlockRead) {
                            pdfViewCtrl.docUnlockRead();
                        }

                        emitter.onComplete();
                    }

                }
            }
        });
    }

    private void updateSharedPrefs(int filterMode) {
        Context context = this.getContext();
        if (context != null) {
            PdfViewCtrlSettingsManager.updateThumbListFilterMode(context, filterMode);
        }

    }

    protected void updateReadOnlyUI() {
        this.finishActionMode();
        if (this.mMenuItemEdit != null) {
            this.mMenuItemEdit.setVisible(!this.mIsReadOnly);
        }

        if (this.mMenuItemAddBookmark != null) {
            this.mMenuItemAddBookmark.setVisible(!this.mIsReadOnly);
        }

        if (this.mFabMenu != null) {
            this.mFabMenu.setVisibility(!this.mIsReadOnly && !this.isBookmarkFilterMode() ? View.VISIBLE : View.GONE);
        }

    }

    public void onResume() {
        super.onResume();
        if (this.mPdfViewCtrl != null && this.mPdfViewCtrl.getToolManager() != null && ((ToolManager)this.mPdfViewCtrl.getToolManager()).canResumePdfDocWithoutReloading()) {
            this.addDocPages();
        }

    }

    public void onStart() {
        super.onStart();
        AnalyticsHandlerAdapter.getInstance().sendTimedEvent(27);
    }

    public void onStop() {
        super.onStop();
        AnalyticsHandlerAdapter.getInstance().endTimedEvent(27);
    }

    public void addDocPages() {
        if (this.mAddDocPagesDelay && this.mDataDelay != null) {
            this.mAddDocPagesDelay = false;
            this.mAdapter.addDocPages(this.mPositionDelay, this.mDocumentFormatDelay, this.mDataDelay);
        }

    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.mOutputFileUri != null) {
            outState.putParcelable("output_file_uri", this.mOutputFileUri);
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Activity activity = this.getActivity();
        if (activity != null) {
            if (resultCode == -1) {
                if (requestCode == 10004 || requestCode == 10003) {
                    this.mPositionDelay = this.getLastSelectedPage();
                    if (requestCode == 10004) {
                        this.mDocumentFormatDelay = DocumentFormat.PDF_DOC;
                        if (data == null || data.getData() == null) {
                            return;
                        }

                        this.mDataDelay = data.getData();
                    } else {
                        this.mDocumentFormatDelay = DocumentFormat.IMAGE;

                        try {
                            Map imageIntent = ViewerUtils.readImageIntent(data, activity, this.mOutputFileUri);
                            if (!ViewerUtils.checkImageIntent(imageIntent)) {
                                Utils.handlePdfFromImageFailed(activity, imageIntent);
                                return;
                            }

                            this.mDataDelay = ViewerUtils.getImageUri(imageIntent);
                            AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewParam(ViewerUtils.isImageFromCamera(imageIntent) ? 8 : 7));
                        } catch (FileNotFoundException var6) {
                        }
                    }

                    if (this.mDataDelay != null) {
                        this.mAddDocPagesDelay = true;
                        this.mHasEventAction = true;
                    }
                }

            }
        }
    }

    public ThumbnailsViewFragment setPdfViewCtrl(@NonNull PDFViewCtrl pdfViewCtrl) {
        this.mPdfViewCtrl = pdfViewCtrl;
        return this;
    }

    public void setOnThumbnailsViewDialogDismissListener(ThumbnailsViewFragment.OnThumbnailsViewDialogDismissListener listener) {
        this.mOnThumbnailsViewDialogDismissListener = listener;
    }

    public void setOnThumbnailsEditAttemptWhileReadOnlyListener(ThumbnailsViewFragment.OnThumbnailsEditAttemptWhileReadOnlyListener listener) {
        this.mOnThumbnailsEditAttemptWhileReadOnlyListener = listener;
    }

    public void setOnExportThumbnailsListener(ThumbnailsViewFragment.OnExportThumbnailsListener listener) {
        this.mOnExportThumbnailsListener = listener;
    }

    public void setItemChecked(int position) {
        this.mInitSelectedItem = position;
    }

    private void launchAndroidFilePicker() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("*/*");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
        intent.putExtra("android.provider.extra.SHOW_ADVANCED", true);
        this.startActivityForResult(intent, 10004);
    }

    private int getLastSelectedPage() {
        int lastSelectedPage = -1;
        if (this.mItemSelectionHelper != null && this.mAdapter != null && this.mItemSelectionHelper.getCheckedItemCount() > 0) {
            lastSelectedPage = -2147483648;
            SparseBooleanArray selectedItems = this.mItemSelectionHelper.getCheckedItemPositions();

            for(int i = 0; i < selectedItems.size(); ++i) {
                if (selectedItems.valueAt(i)) {
                    int position = selectedItems.keyAt(i);
                    Integer itemMap = this.mAdapter.getItem(position);
                    if (itemMap != null) {
                        int pageNum = itemMap;
                        if (pageNum > lastSelectedPage) {
                            lastSelectedPage = pageNum;
                        }
                    }
                }
            }
        }

        return lastSelectedPage;
    }

    public void setTitle(String title) {
        this.mTitle = title;
        if (this.mToolbar != null) {
            this.mToolbar.setTitle(title);
        }

    }

    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mAdapter != null) {
            int viewWidth = this.getDisplayWidth();
            int thumbSize = this.getResources().getDimensionPixelSize(dimen.controls_thumbnails_view_image_width);
            int thumbSpacing = this.getResources().getDimensionPixelSize(dimen.controls_thumbnails_view_grid_spacing);
            this.mSpanCount = (int)Math.floor((double)viewWidth / (double)(thumbSize + thumbSpacing));
            this.mAdapter.updateMainViewWidth(viewWidth);
            this.updateSpanCount(this.mSpanCount);
        }

        if (this.mActionMode != null) {
            this.mActionMode.invalidate();
        }

    }

    private int getMainViewWidth() {
        return this.mRecyclerView != null && ViewCompat.isLaidOut(this.mRecyclerView) ? this.mRecyclerView.getMeasuredWidth() : this.getDisplayWidth();
    }

    private int getDisplayWidth() {
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public void updateSpanCount(int count) {
        this.mSpanCount = count;
        this.mRecyclerView.updateSpanCount(count);
    }

    public ThumbnailsViewAdapter getAdapter() {
        return this.mAdapter;
    }

    private boolean finishActionMode() {
        boolean success = false;
        if (this.mActionMode != null) {
            success = true;
            this.mActionMode.finish();
            this.mActionMode = null;
        }

        this.clearSelectedList();
        return success;
    }

    private void clearSelectedList() {
        if (this.mItemSelectionHelper != null) {
            this.mItemSelectionHelper.clearChoices();
        }

        if (this.mActionMode != null) {
            this.mActionMode.invalidate();
        }

    }

    private boolean onBackPressed() {
        if (!this.isAdded()) {
            return false;
        } else {
            boolean handled = false;
            if (this.mActionMode != null) {
                handled = this.finishActionMode();
            }

            return handled;
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        this.mDisposables.clear();
    }

    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (this.mPdfViewCtrl != null && this.mPdfViewCtrl.getDoc() != null) {
            AnalyticsHandlerAdapter.getInstance().sendEvent(28, AnalyticsParam.noActionParam(this.mHasEventAction));
            if (this.mAdapter.getDocPagesModified()) {
                ViewerUtils.safeUpdatePageLayout(this.mPdfViewCtrl, new ExceptionHandlerCallback() {
                    public void onException(Exception e) {
                        AnalyticsHandlerAdapter.getInstance().sendException(e);
                    }
                });
            }

            try {
                this.mPdfViewCtrl.setCurrentPage(this.mAdapter.getCurrentPage());
            } catch (Exception var4) {
                AnalyticsHandlerAdapter.getInstance().sendException(var4);
            }

            this.mAdapter.clearResources();
            this.mAdapter.finish();

            try {
                this.mPdfViewCtrl.cancelAllThumbRequests();
            } catch (Exception var3) {
                AnalyticsHandlerAdapter.getInstance().sendException(var3);
            }

            if (this.mOnThumbnailsViewDialogDismissListener != null) {
                this.mOnThumbnailsViewDialogDismissListener.onThumbnailsViewDialogDismiss(this.mAdapter.getCurrentPage(), this.mAdapter.getDocPagesModified());
            }

        }
    }

    private void manageAddPages(List<Integer> pageList) {
        if (this.mPdfViewCtrl == null) {
            throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
        } else {
            ToolManager toolManager = (ToolManager)this.mPdfViewCtrl.getToolManager();
            if (toolManager != null) {
                toolManager.raisePagesAdded(pageList);
            }

            this.updateUndoRedoIcons();
        }
    }

    private void manageDeletePages(List<Integer> pageList) {
        if (this.mPdfViewCtrl == null) {
            throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
        } else {
            ToolManager toolManager = (ToolManager)this.mPdfViewCtrl.getToolManager();
            if (toolManager != null) {
                toolManager.raisePagesDeleted(pageList);
            }

            this.updateUndoRedoIcons();
        }
    }

    private void manageRotatePages(List<Integer> pageList) {
        if (this.mPdfViewCtrl == null) {
            throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
        } else {
            ToolManager toolManager = (ToolManager)this.mPdfViewCtrl.getToolManager();
            if (toolManager != null) {
                toolManager.raisePagesRotated(pageList);
            }

            this.updateUndoRedoIcons();
        }
    }

    private void manageMovePage(int fromPageNum, int toPageNum) {
        if (this.mPdfViewCtrl == null) {
            throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
        } else {
            ToolManager toolManager = (ToolManager)this.mPdfViewCtrl.getToolManager();
            if (toolManager != null) {
                toolManager.raisePageMoved(fromPageNum, toPageNum);
            }

            this.updateUndoRedoIcons();
        }
    }

    private void managePageLabelChanged() {
        if (this.mPdfViewCtrl == null) {
            throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
        } else {
            ToolManager toolManager = (ToolManager)this.mPdfViewCtrl.getToolManager();
            if (toolManager != null) {
                toolManager.raisePageLabelChangedEvent();
            }

            this.updateUndoRedoIcons();
        }
    }

    public void onPagesAdded(List<Integer> pageList) {
        this.manageAddPages(pageList);
        if (this.mDocumentFormatDelay != null) {
            if (this.mDocumentFormatDelay == DocumentFormat.PDF_DOC) {
                AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewCountParam(6, pageList.size()));
            }

            this.mDocumentFormatDelay = null;
        }

    }

    public void onPageMoved(int fromPageNum, int toPageNum) {
        this.manageMovePage(fromPageNum, toPageNum);
        AnalyticsHandlerAdapter.getInstance().sendEvent(29, AnalyticsParam.thumbnailsViewParam(9));
    }

    public void updateUndoRedoIcons() {
        if (this.mPdfViewCtrl == null) {
            throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
        } else {
            if (this.mMenuItemUndo != null && this.mMenuItemRedo != null) {
                boolean undoEnabled = false;
                boolean redoEnabled = false;
                ToolManager toolManager = (ToolManager)this.mPdfViewCtrl.getToolManager();
                if (toolManager != null) {
                    UndoRedoManager undoRedoManager = toolManager.getUndoRedoManger();
                    undoEnabled = undoRedoManager.isNextUndoEditPageAction();
                    redoEnabled = undoRedoManager.isNextRedoEditPageAction();
                }

                this.mMenuItemUndo.setEnabled(undoEnabled);
                if (this.mMenuItemUndo.getIcon() != null) {
                    this.mMenuItemUndo.getIcon().setAlpha(undoEnabled ? 255 : 150);
                }

                this.mMenuItemRedo.setEnabled(redoEnabled);
                if (this.mMenuItemRedo.getIcon() != null) {
                    this.mMenuItemRedo.getIcon().setAlpha(redoEnabled ? 255 : 150);
                }
            }

        }
    }

    private boolean isNormalFilterMode() {
        Integer mode = this.mFilterMode.getFilterMode();
        return mode == null || mode == 0;
    }

    private boolean isBookmarkFilterMode() {
        Integer mode = this.mFilterMode.getFilterMode();
        return mode != null && mode == 2;
    }

    @RestrictTo({Scope.LIBRARY_GROUP})
    public static class Theme {
        @ColorInt
        public final int pageNumberTextColor;
        @ColorInt
        public final int pageNumberBackgroundColor;
        @ColorInt
        public final int activePageNumberTextColor;
        @ColorInt
        public final int activePageNumberBackgroundColor;

        public Theme(int pageNumberTextColor, int pageNumberBackgroundColor, int activePageNumberTextColor, int activePageNumberBackgroundColor) {
            this.pageNumberTextColor = pageNumberTextColor;
            this.pageNumberBackgroundColor = pageNumberBackgroundColor;
            this.activePageNumberTextColor = activePageNumberTextColor;
            this.activePageNumberBackgroundColor = activePageNumberBackgroundColor;
        }


    }

    public interface OnExportThumbnailsListener {
        void onExportThumbnails(SparseBooleanArray pageNums);
    }

    public interface OnThumbnailsEditAttemptWhileReadOnlyListener {
        void onThumbnailsEditAttemptWhileReadOnly();
    }

    public interface OnThumbnailsViewDialogDismissListener {
        void onThumbnailsViewDialogDismiss(int pageNum, boolean docPagesModified);
    }

    public static enum FilterModes {
        ANNOTATED(1),
        BOOKMARKED(2);

        final int mode;

        private FilterModes(int mode) {
            this.mode = mode;
        }

        public int getValue() {
            return this.mode;
        }
    }
}
