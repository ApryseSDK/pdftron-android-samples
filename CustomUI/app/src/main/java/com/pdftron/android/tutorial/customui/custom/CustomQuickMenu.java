package com.pdftron.android.tutorial.customui.custom;

import android.content.Context;
import androidx.annotation.NonNull;

import com.pdftron.android.tutorial.customui.R;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.controls.PdfViewCtrlTabFragment2;
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2;
import com.pdftron.pdf.tools.QuickMenu;
import com.pdftron.pdf.tools.QuickMenuItem;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.CommonToast;

import java.util.ArrayList;

/**
 * Delegate class that adds custom quick menu buttons when annotations are selected. In particular,
 * this sample adds a custom star to almost all of the annotation quick menus, adds a "Link" quick
 * menu button when square annotations are selected, and adds an "UnLink" quick menu button when circle
 * annotations are selected.
 */
public class CustomQuickMenu extends CustomizationDelegate {

    public CustomQuickMenu(@NonNull Context context, @NonNull PdfViewCtrlTabHostFragment2 tabHostFragment) {
        super(context, tabHostFragment);
    }

    @Override
    public void applyCustomization(@NonNull PdfViewCtrlTabFragment2 tabFragment) {
        customizeQuickMenu(mContext, tabFragment);
    }

    private static void customizeQuickMenu(@NonNull final Context context,
            @NonNull final PdfViewCtrlTabFragment2 tabFragment) {
        // let's add some new items in the quick menu
        tabFragment.addQuickMenuListener(new ToolManager.QuickMenuListener() {
            @Override
            public boolean onQuickMenuClicked(QuickMenuItem menuItem) {
                int which = menuItem.getItemId();
                if (which == R.id.qm_translate) {
                    CommonToast.showText(context, "Translate pressed");
                    return true;
                }
                return false;
            }

            @Override
            public boolean onShowQuickMenu(QuickMenu quickMenu, Annot annot) {

                return false;
            }

            @Override
            public void onQuickMenuShown() {
                // Called when the quick menu is shown
            }

            @Override
            public void onQuickMenuDismissed() {
                // Called when the quick menu is dismissed
            }
        });
    }
}
