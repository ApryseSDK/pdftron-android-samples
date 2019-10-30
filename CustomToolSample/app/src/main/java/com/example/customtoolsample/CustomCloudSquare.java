package com.example.customtoolsample;

import android.graphics.Color;
import androidx.annotation.NonNull;

import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.Annot;
import com.pdftron.pdf.ColorPt;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.Point;
import com.pdftron.pdf.Rect;
import com.pdftron.pdf.annots.Markup;
import com.pdftron.pdf.annots.PolyLine;
import com.pdftron.pdf.annots.Polygon;
import com.pdftron.pdf.tools.RectCreate;
import com.pdftron.pdf.tools.ToolManager;
import com.pdftron.pdf.utils.Utils;

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
    protected Annot createMarkup(PDFDoc doc, Rect bbox) throws PDFNetException {
        Polygon poly = new Polygon(Polygon.create(doc, Annot.e_Polygon, bbox));
        ColorPt color = Utils.color2ColorPt(Color.RED);
        poly.setColor(color, 3);
        poly.setVertex(0, new Point(bbox.getX1(), bbox.getY1()));
        poly.setVertex(1, new Point(bbox.getX1(), bbox.getY2()));
        poly.setVertex(2, new Point(bbox.getX2(), bbox.getY2()));
        poly.setVertex(3, new Point(bbox.getX2(), bbox.getY1()));
        poly.setIntentName(PolyLine.e_PolygonCloud);
        poly.setBorderEffect(Markup.e_Cloudy);
        poly.setBorderEffectIntensity(2.0);
        poly.setRect(bbox);

        return poly;
    }
}
