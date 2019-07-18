package com.pdftron.android.tutorial.customui.custom;

import android.content.Context;
import android.support.annotation.NonNull;

import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment;

public abstract class CustomizationDelegate {

    @NonNull
    final Context mContext;
    @NonNull
    final PdfViewCtrlTabHostFragment mTabHostFragment;

    CustomizationDelegate(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment tabHostFragment) {
        mContext = context;
        mTabHostFragment = tabHostFragment;
    }

    abstract public void applyCustomization();
}
