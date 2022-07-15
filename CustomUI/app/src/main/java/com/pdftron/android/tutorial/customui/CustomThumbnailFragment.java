package com.pdftron.android.tutorial.customui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdftron.pdf.controls.ThumbnailsViewFragment;

public class CustomThumbnailFragment extends ThumbnailsViewFragment {

    public static CustomThumbnailFragment newInstance(boolean readOnly, boolean editMode,
            @Nullable int[] hideFilterModes, @Nullable String[] hideEditOptions) {
        CustomThumbnailFragment fragment = new CustomThumbnailFragment();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar.setBackgroundColor(Color.GREEN);
    }
}
