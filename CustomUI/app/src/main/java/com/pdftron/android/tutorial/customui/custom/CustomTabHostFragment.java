package com.pdftron.android.tutorial.customui.custom;

import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.widget.toolbar.builder.ToolbarItem;
import com.pdftron.pdf.widget.toolbar.component.AnnotationToolbarComponent;
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars;

public class CustomTabHostFragment extends PdfViewCtrlTabHostFragment2 {
    @Override
    protected void initViews() {
        super.initViews();
        mAnnotationToolbarComponent.addButtonClickListener(new AnnotationToolbarComponent.AnnotationButtonClickListener() {
            @Override
            public boolean onInterceptItemClick(@Nullable ToolbarItem toolbarItem, MenuItem item) {
                if (item.getItemId() == DefaultToolbars.ButtonId.CIRCLE.value()) {
                    Toast.makeText(getContext(), "Paywall Enabled", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }

            @Override
            public void onPreItemClick(@Nullable ToolbarItem toolbarItem, MenuItem item) {

            }

            @Override
            public void onPostItemClick(@Nullable ToolbarItem toolbarItem, @NonNull MenuItem item) {

            }
        });
    }
}
