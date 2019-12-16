package com.example.customtoolsample;

import android.net.Uri;
import androidx.annotation.NonNull;

import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.tools.Stamper;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class CustomStamp extends Stamper {

    // Since this tool creates polygon annotation, use Annot.e_Polygon as parameter.
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.addNewMode(Annot.e_Stamp);

    public CustomStamp(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return MODE;
    }

    @Override
    protected void addStamp() {
        File resource = Utils.copyResourceToLocal(mPdfViewCtrl.getContext(), R.raw.pdftronlogo, "PDFTronLogo", "png");
        createImageStamp(Uri.fromFile(resource), 0, null);
    }
}
