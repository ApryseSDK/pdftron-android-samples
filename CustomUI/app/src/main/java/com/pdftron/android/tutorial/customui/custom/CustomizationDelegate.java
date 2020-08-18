package com.pdftron.android.tutorial.customui.custom;

import android.content.Context;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;

import com.pdftron.pdf.controls.PdfViewCtrlTabFragment2;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.model.FileInfo;

/**
 * Abstract delegate customization class used to customize a PdfViewCtrlTabFragment. Will apply
 * customization on each tab using when onTabDocumentLoaded is called.
 */
abstract class CustomizationDelegate implements PdfViewCtrlTabHostFragment2.TabHostListener {

    @NonNull
    protected final Context mContext;
    @NonNull
    private final PdfViewCtrlTabHostFragment2 mTabHostFragment;

    CustomizationDelegate(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment2 tabHostFragment) {
        mContext = context;
        mTabHostFragment = tabHostFragment;
        mTabHostFragment.addHostListener(this);
    }

    protected void applyCustomization(@NonNull PdfViewCtrlTabFragment2 tabFragment) {
        // Implement to customize using tab fragment ...
    }

    protected void applyCustomization(@NonNull PdfViewCtrlTabHostFragment2 tabHostFragment) {
        // Implement to customize using host fragment ...
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
        return true;
    }

    @Override
    public void onTabPaused(FileInfo fileInfo, boolean b) {

    }

    @Override
    public void onJumpToSdCardFolder() {

    }

    @Override
    public void onTabDocumentLoaded(String s) {
        applyCustomization(mTabHostFragment.getCurrentPdfViewCtrlFragment());
        applyCustomization(mTabHostFragment);
    }

}
