package com.example.customtoolsample;

import android.content.SharedPreferences;
import android.view.MotionEvent;
import androidx.annotation.NonNull;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.FreehandCreate;
import com.pdftron.pdf.tools.Tool;
import com.pdftron.pdf.tools.ToolManager;

public class CustomInk extends FreehandCreate {
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.INK_CREATE;

    private double lastZoom = -1;

    public CustomInk(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (Math.abs(lastZoom - mPdfViewCtrl.getZoom()) > 0.001) {
            if (lastZoom == -1) {
                lastZoom = 1.0f;
            }
            // Since zoom changed, we will commit this annotation because the next annotation
            // created at the zoom level will have different thickness
            commitAnnotation();

            // Now we set the new thickness relative to current zoom
            double newZoom = mPdfViewCtrl.getZoom();
            mThickness = mThickness * (float) lastZoom / (float) newZoom;
            lastZoom = newZoom;

            // We will also need to store this to shared preferences as this is referenced internally
            SharedPreferences settings = Tool.getToolPreferences(mPdfViewCtrl.getContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.putFloat(getThicknessKey(getCreateAnnotType()), mThickness);

            editor.apply();
        }
        return super.onDown(e);
    }

    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return MODE;
    }
}
