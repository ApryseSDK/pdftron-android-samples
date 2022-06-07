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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

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
                if (which == R.id.qm_star) {
                    CommonToast.showText(context, "Star pressed");
                    return true;
                } else if (which == R.id.qm_custom_link) {
                    CommonToast.showText(context, "Link pressed");
                } else if (which == R.id.qm_custom_unlink) {
                    CommonToast.showText(context, "Unlink pressed");
                }
                return false;
            }

            @Override
            public boolean onShowQuickMenu(QuickMenu quickMenu, Annot annot) {
                // Programmatically change quick menu
                try {
                    if (annot != null && quickMenu != null) {
                        if (annot.getType() == Annot.e_Text) {
                            // Add a custom quick menu button when square annotations are selected
                            // to the first row called "Link" with a ic_link_black_24dp icon
                            QuickMenuItem flatten =
                                    new QuickMenuItem(context,
                                            R.id.qm_flatten,
                                            QuickMenuItem.OVERFLOW_ROW_MENU
                                    );
                            ArrayList<QuickMenuItem> items = new ArrayList<>(1);
                            items.add(flatten);
                            quickMenu.removeMenuEntries(items);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

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
