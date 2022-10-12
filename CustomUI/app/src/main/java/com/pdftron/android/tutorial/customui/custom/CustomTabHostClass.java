package com.pdftron.android.tutorial.customui.custom;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

public class CustomTabHostClass extends PdfViewCtrlTabHostFragment2 {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAnnotationToolbarViewModel.observeBuilderState(this, annotationToolbarBuilder -> setIconColor(DefaultToolbars.ButtonId.INK.value(), Color.BLUE));
    }
}

