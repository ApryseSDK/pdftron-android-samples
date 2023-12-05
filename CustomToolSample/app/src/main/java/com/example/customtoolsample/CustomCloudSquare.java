package com.example.customtoolsample;

import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.model.ShapeBorderStyle;
import com.pdftron.pdf.tools.RectCreate;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.AnnotUtils;

public class CustomCloudSquare extends RectCreate {

    // Since this tool creates polygon annotation, use Annot.e_Polygon as parameter.
    public static ToolManager.ToolModeBase MODE =
            ToolManager.ToolMode.addNewMode(Annot.e_Polygon);

    public CustomCloudSquare(@NonNull PDFViewCtrl ctrl) {
        super(ctrl);
    }

    @Override
    public ToolManager.ToolModeBase getToolMode() {
        return MODE;
    }

    @Override
    protected Annot createMarkup(@NonNull PDFDoc doc, Rect bbox) throws PDFNetException {
        Annot annot = super.createMarkup(doc, bbox);
        mBorderStyle = ShapeBorderStyle.CLOUDY;
        return annot;
    }
}
