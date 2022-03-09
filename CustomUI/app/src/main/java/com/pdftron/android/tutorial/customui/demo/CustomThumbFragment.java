package com.pdftron.android.tutorial.customui.demo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.pdftron.android.tutorial.customui.R;
import com.pdftron.pdf.controls.ThumbnailsViewFragment;
import com.pdftron.pdf.dialog.pagelabel.PageLabelDialog;
import com.pdftron.pdf.dialog.pagelabel.PageLabelUtils;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.tools.UndoRedoManager;
import com.pdftron.pdf.utils.AnalyticsHandlerAdapter;
import com.pdftron.pdf.utils.AnalyticsParam;
import com.pdftron.pdf.utils.CommonToast;
import com.pdftron.pdf.utils.ToolbarActionMode;
import com.pdftron.pdf.utils.Utils;
import com.pdftron.pdf.utils.ViewerUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomThumbFragment extends ThumbnailsViewFragment {

    public static CustomThumbFragment newInstance(boolean readOnly, boolean editMode,
            @Nullable int[] hideFilterModes, @Nullable String[] hideEditOptions) {
        CustomThumbFragment fragment = new CustomThumbFragment();
        Bundle args = new Bundle();
        args.putBoolean(BUNDLE_READ_ONLY_DOC, readOnly);
        args.putBoolean(BUNDLE_EDIT_MODE, editMode);
        if (hideFilterModes != null) {
            args.putIntArray(BUNDLE_HIDE_FILTER_MODES, hideFilterModes);
        }
        if (hideEditOptions != null) {
            args.putStringArray(BUNDLE_HIDE_EDIT_OPTIONS, hideEditOptions);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void startActionMode() {
        mActionMode = new ToolbarActionMode(getActivity(), mCabToolbar);
        mActionMode.setMainToolbar(mToolbar);
        mActionMode.startActionMode(mActionModeCallback);
    }

    private MenuItem mMenuItemRotateLeft;
    private MenuItem mMenuItemRotateRight;

    private final ToolbarActionMode.Callback mActionModeCallback = new ToolbarActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ToolbarActionMode mode, Menu menu) {
            mode.inflateMenu(R.menu.custom_cab_thumb);

            mMenuItemDelete = menu.findItem(R.id.controls_thumbnails_view_action_delete);
            mMenuItemRotateLeft = menu.findItem(R.id.controls_thumbnails_view_action_rotate_left);
            mMenuItemRotateRight = menu.findItem(R.id.controls_thumbnails_view_action_rotate_right);

            if (isBookmarkFilterMode()) {
                if (mMenuItemRotateLeft != null) {
                    mMenuItemRotateLeft.setVisible(false);
                }
                if (mMenuItemRotateRight != null) {
                    mMenuItemRotateRight.setVisible(false);
                }
                if (mMenuItemDelete != null) {
                    mMenuItemDelete.setVisible(false);
                }
            }

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ToolbarActionMode mode, Menu menu) {
            boolean isEnabled = mItemSelectionHelper.getCheckedItemCount() > 0;

            if (mMenuItemRotateLeft != null) {
                mMenuItemRotateLeft.setEnabled(isEnabled);
                if (mMenuItemRotateLeft.getIcon() != null) {
                    mMenuItemRotateLeft.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }

            if (mMenuItemRotateRight != null) {
                mMenuItemRotateRight.setEnabled(isEnabled);
                if (mMenuItemRotateRight.getIcon() != null) {
                    mMenuItemRotateRight.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }

            if (mMenuItemRotate != null) {
                mMenuItemRotate.setEnabled(isEnabled);
                if (mMenuItemRotate.getIcon() != null) {
                    mMenuItemRotate.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }
            if (mMenuItemDelete != null) {
                mMenuItemDelete.setEnabled(isEnabled);
                if (mMenuItemDelete.getIcon() != null) {
                    mMenuItemDelete.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }
            if (mMenuItemDuplicate != null) {
                mMenuItemDuplicate.setEnabled(isEnabled);
                if (mMenuItemDuplicate.getIcon() != null) {
                    mMenuItemDuplicate.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }
            if (mMenuItemExport != null) {
                mMenuItemExport.setEnabled(isEnabled);
                if (mMenuItemExport.getIcon() != null) {
                    mMenuItemExport.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }
            if (mMenuItemPageLabel != null) {
                mMenuItemPageLabel.setEnabled(isEnabled);
                if (mMenuItemPageLabel.getIcon() != null) {
                    mMenuItemPageLabel.getIcon().setAlpha(isEnabled ? 255 : 150);
                }
            }
            if (mMenuItemRemoveBookmark != null) {
                mMenuItemRemoveBookmark.setEnabled(isEnabled);
            }

            if (Utils.isTablet(getContext()) || getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mode.setTitle(getString(R.string.controls_thumbnails_view_selected,
                        Utils.getLocaleDigits(Integer.toString(mItemSelectionHelper.getCheckedItemCount()))));
            } else {
                mode.setTitle(Utils.getLocaleDigits(Integer.toString(mItemSelectionHelper.getCheckedItemCount())));
            }
            updateUndoRedoIcons();
            return true;
        }

        @Override
        public boolean onActionItemClicked(ToolbarActionMode mode, MenuItem item) {
            if (mPdfViewCtrl == null) {
                throw new NullPointerException("setPdfViewCtrl() must be called with a valid PDFViewCtrl");
            }

            if (item.getItemId() == R.id.controls_thumbnails_view_action_rotate_left) {
                if (mIsReadOnly) {
                    if (mOnThumbnailsEditAttemptWhileReadOnlyListener != null)
                        mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                    return true;
                }
                // rotate all selected pages
                SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();
                List<Integer> pageList = new ArrayList<>();
                for (int i = 0; i < selectedItems.size(); i++) {
                    if (selectedItems.valueAt(i)) {
                        int position = selectedItems.keyAt(i);
                        mAdapter.rotateDocPage(position + 1, false);
                        pageList.add(position + 1);
                    }
                }
                manageRotatePages(pageList);
                mHasEventAction = true;
                AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_THUMBNAILS_VIEW,
                        AnalyticsParam.thumbnailsViewCountParam(AnalyticsHandlerAdapter.THUMBNAILS_VIEW_ROTATE, selectedItems.size()));
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_rotate_right) {
                if (mIsReadOnly) {
                    if (mOnThumbnailsEditAttemptWhileReadOnlyListener != null)
                        mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                    return true;
                }
                // rotate all selected pages
                SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();
                List<Integer> pageList = new ArrayList<>();
                for (int i = 0; i < selectedItems.size(); i++) {
                    if (selectedItems.valueAt(i)) {
                        int position = selectedItems.keyAt(i);
                        mAdapter.rotateDocPage(position + 1);
                        pageList.add(position + 1);
                    }
                }
                manageRotatePages(pageList);
                mHasEventAction = true;
                AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_THUMBNAILS_VIEW,
                        AnalyticsParam.thumbnailsViewCountParam(AnalyticsHandlerAdapter.THUMBNAILS_VIEW_ROTATE, selectedItems.size()));
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_rotate) {
                if (mIsReadOnly) {
                    if (mOnThumbnailsEditAttemptWhileReadOnlyListener != null)
                        mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                    return true;
                }
                // rotate all selected pages
                SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();
                List<Integer> pageList = new ArrayList<>();
                for (int i = 0; i < selectedItems.size(); i++) {
                    if (selectedItems.valueAt(i)) {
                        int position = selectedItems.keyAt(i);
                        mAdapter.rotateDocPage(position + 1);
                        pageList.add(position + 1);
                    }
                }
                manageRotatePages(pageList);
                mHasEventAction = true;
                AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_THUMBNAILS_VIEW,
                        AnalyticsParam.thumbnailsViewCountParam(AnalyticsHandlerAdapter.THUMBNAILS_VIEW_ROTATE, selectedItems.size()));
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_delete) {
                if (mIsReadOnly) {
                    if (mOnThumbnailsEditAttemptWhileReadOnlyListener != null)
                        mOnThumbnailsEditAttemptWhileReadOnlyListener.onThumbnailsEditAttemptWhileReadOnly();
                    return true;
                }
                // Need to convert checked-positions to a sortable list
                List<Integer> pageList = new ArrayList<>();
                SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();

                int pageCount;
                boolean shouldUnlockRead = false;
                try {
                    mPdfViewCtrl.docLockRead();
                    shouldUnlockRead = true;
                    pageCount = mPdfViewCtrl.getDoc().getPageCount();
                } catch (Exception ex) {
                    AnalyticsHandlerAdapter.getInstance().sendException(ex);
                    return true;
                } finally {
                    if (shouldUnlockRead) {
                        mPdfViewCtrl.docUnlockRead();
                    }
                }

                if (selectedItems.size() >= pageCount) {
                    CommonToast.showText(getContext(), R.string.controls_thumbnails_view_delete_msg_all_pages);
                    clearSelectedList();
                    return true;
                }

                for (int i = 0; i < selectedItems.size(); i++) {
                    if (selectedItems.valueAt(i)) {
                        pageList.add(selectedItems.keyAt(i) + 1);
                    }
                }
                // delete should start from back
                Collections.sort(pageList, Collections.reverseOrder());
                int count = pageList.size();
                for (int i = 0; i < count; ++i) {
                    mAdapter.removeDocPage(pageList.get(i));
                }
                clearSelectedList();
                manageDeletePages(pageList);
                mHasEventAction = true;
                AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_THUMBNAILS_VIEW,
                        AnalyticsParam.thumbnailsViewCountParam(AnalyticsHandlerAdapter.THUMBNAILS_VIEW_DELETE, selectedItems.size()));
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_duplicate) {
                if (mAdapter != null) {
                    List<Integer> pageList = new ArrayList<>();
                    SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();
                    for (int i = 0; i < selectedItems.size(); i++) {
                        if (selectedItems.valueAt(i)) {
                            pageList.add(selectedItems.keyAt(i) + 1);
                        }
                    }
                    mAdapter.duplicateDocPages(pageList);
                    mHasEventAction = true;
                    AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_THUMBNAILS_VIEW,
                            AnalyticsParam.thumbnailsViewCountParam(AnalyticsHandlerAdapter.THUMBNAILS_VIEW_DUPLICATE, selectedItems.size()));
                }
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_export) {
                if (mOnExportThumbnailsListener != null) {
                    SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();
                    mOnExportThumbnailsListener.onExportThumbnails(selectedItems);
                    mHasEventAction = true;
                    AnalyticsHandlerAdapter.getInstance().sendEvent(AnalyticsHandlerAdapter.EVENT_THUMBNAILS_VIEW,
                            AnalyticsParam.thumbnailsViewCountParam(AnalyticsHandlerAdapter.THUMBNAILS_VIEW_EXPORT, selectedItems.size()));
                }
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_page_label) {
                if (mAdapter == null) {
                    return true;
                }

                SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();
                int fromPage = Integer.MAX_VALUE;
                int toPage = -1;
                for (int i = 0; i < selectedItems.size(); i++) {
                    if (selectedItems.valueAt(i)) {
                        int page = selectedItems.keyAt(i) + 1;
                        toPage = Math.max(page, toPage);
                        fromPage = Math.min(page, fromPage);
                    }
                }

                final int numPages = mPdfViewCtrl.getPageCount();
                // If this is true, then return. We are somehow at an invalid state.
                if (fromPage < 1 || toPage < 1 || toPage < fromPage || fromPage > numPages) {
                    CommonToast.showText(getContext(), getString(R.string.page_label_failed), Toast.LENGTH_LONG);
                    return true;
                }

                // If only one page is selected, then just create a page label
                // component for the select page, otherwise create a component
                // for the page range
                FragmentActivity activity = getActivity();
                FragmentManager fragManager = getFragmentManager();
                if (fragManager != null && activity != null) {
                    String prefix = PageLabelUtils.getPageLabelPrefix(mPdfViewCtrl, fromPage);
                    PageLabelDialog dialog = PageLabelDialog.newInstance(fromPage, toPage, numPages, prefix);
                    dialog.setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
                    dialog.show(fragManager, PageLabelDialog.TAG);
                }
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_undo) {
                ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
                if (toolManager != null) {
                    String undoInfo = toolManager.getUndoRedoManger().undo(AnalyticsHandlerAdapter.LOCATION_THUMBNAILS_VIEW, true);
                    updateUndoRedoIcons();
                    if (!Utils.isNullOrEmpty(undoInfo)) {
                        try {
                            if (UndoRedoManager.isDeletePagesAction(getContext(), undoInfo)) {
                                List<Integer> pageList = UndoRedoManager.getPageList(undoInfo);
                                if (pageList.size() != 0) {
                                    mAdapter.updateAfterAddition(pageList);
                                }
                            } else if (UndoRedoManager.isAddPagesAction(getContext(), undoInfo)) {
                                List<Integer> pageList = UndoRedoManager.getPageList(undoInfo);
                                if (pageList.size() != 0) {
                                    mAdapter.updateAfterDeletion(pageList);
                                }
                            } else if (UndoRedoManager.isRotatePagesAction(getContext(), undoInfo)) {
                                List<Integer> pageList = UndoRedoManager.getPageList(undoInfo);
                                if (pageList.size() != 0) {
                                    mAdapter.updateAfterRotation(pageList);
                                }
                            } else if (UndoRedoManager.isMovePageAction(getContext(), undoInfo)) {
                                mAdapter.updateAfterMove(UndoRedoManager.getPageTo(undoInfo),
                                        UndoRedoManager.getPageFrom(undoInfo));
                            } else if (UndoRedoManager.isEditPageLabelsAction(getContext(), undoInfo)) {
                                mAdapter.updateAfterPageLabelEdit();
                            }
                        } catch (Exception e) {
                            AnalyticsHandlerAdapter.getInstance().sendException(e);
                        }
                    }
                }
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_redo) {
                ToolManager toolManager = (ToolManager) mPdfViewCtrl.getToolManager();
                if (toolManager != null) {
                    String redoInfo = toolManager.getUndoRedoManger().redo(AnalyticsHandlerAdapter.LOCATION_THUMBNAILS_VIEW, true);
                    updateUndoRedoIcons();
                    if (!Utils.isNullOrEmpty(redoInfo)) {
                        try {
                            if (UndoRedoManager.isDeletePagesAction(getContext(), redoInfo)) {
                                List<Integer> pageList = UndoRedoManager.getPageList(redoInfo);
                                if (pageList.size() != 0) {
                                    mAdapter.updateAfterDeletion(pageList);
                                }
                            } else if (UndoRedoManager.isAddPagesAction(getContext(), redoInfo)) {
                                List<Integer> pageList = UndoRedoManager.getPageList(redoInfo);
                                if (pageList.size() != 0) {
                                    mAdapter.updateAfterAddition(pageList);
                                }
                            } else if (UndoRedoManager.isRotatePagesAction(getContext(), redoInfo)) {
                                List<Integer> pageList = UndoRedoManager.getPageList(redoInfo);
                                if (pageList.size() != 0) {
                                    mAdapter.updateAfterRotation(pageList);
                                }
                            } else if (UndoRedoManager.isMovePageAction(getContext(), redoInfo)) {
                                mAdapter.updateAfterMove(UndoRedoManager.getPageFrom(redoInfo),
                                        UndoRedoManager.getPageTo(redoInfo));
                            } else if (UndoRedoManager.isEditPageLabelsAction(getContext(), redoInfo)) {
                                mAdapter.updateAfterPageLabelEdit();
                            }
                        } catch (Exception e) {
                            AnalyticsHandlerAdapter.getInstance().sendException(e);
                        }
                    }
                }
            } else if (item.getItemId() == R.id.controls_thumbnails_view_action_remove_bookmark) {
                SparseBooleanArray selectedItems = mItemSelectionHelper.getCheckedItemPositions();
                for (int i = 0; i < selectedItems.size(); i++) {
                    if (selectedItems.valueAt(i)) {
                        int position = selectedItems.keyAt(i);
                        Integer page = mAdapter.getItem(position);
                        if (page != null) {
                            ViewerUtils.removePageBookmark(mPdfViewCtrl.getContext(), mIsReadOnlySave, mPdfViewCtrl, page);
                        }
                    }
                }
                if (isBookmarkFilterMode()) {
                    populateThumbList(FILTER_MODE_BOOKMARKED);
                }
                finishActionMode();
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ToolbarActionMode mode) {
            mActionMode = null;
            clearSelectedList();
        }
    };
}
