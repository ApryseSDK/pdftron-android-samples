package com.example.customtoolsample;

import android.content.ClipData;
import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.QuickMenuItem;
import com.pdftron.pdf.tools.TextSelect;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.CommonToast;
import com.pdftron.pdf.utils.ViewerUtils;

public class CustomToolSelect extends TextSelect {
    public CustomToolSelect(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }
    public static ToolManager.ToolModeBase MODE = ToolManager.ToolMode.TEXT_SELECT;
    @Override
    public boolean onQuickMenuClicked(QuickMenuItem menuItem) {
        if (menuItem.getItemId() == com.pdftron.pdf.tools.R.id.qm_copy) {
            if (mPdfViewCtrl.hasSelection()) {
                String text = ViewerUtils.getSelectedString(mPdfViewCtrl);
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) mPdfViewCtrl.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    ClipData clip = ClipData.newPlainText("text", text);
                    clipboard.setPrimaryClip(clip);
                }

                // Add any custom toast message here
                CommonToast.showText(mPdfViewCtrl.getContext(), "Any Text Here", Toast.LENGTH_SHORT);
            }
            exitCurrentMode();
            return true;
        }
        return super.onQuickMenuClicked(menuItem);
    }
}
