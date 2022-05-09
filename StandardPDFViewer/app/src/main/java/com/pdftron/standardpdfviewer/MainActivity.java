package com.pdftron.standardpdfviewer;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.pdftron.pdf.PDFViewCtrl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDFViewCtrl pdfViewCtrl = findViewById(R.id.pdfviewctrl);
        try {
            pdfViewCtrl.openPDFUri(Uri.parse("https://pdftron.s3.amazonaws.com/downloads/pdfref.pdf"), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
