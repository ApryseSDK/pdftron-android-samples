package com.pdftron.standardpdfviewer;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.pdftron.common.PDFNetException;
import com.pdftron.filters.SecondaryFileFilter;
import com.pdftron.pdf.Convert;
import com.pdftron.pdf.DocumentConversion;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pdftron.pdf.utils.Utils;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PDFViewCtrl pdfViewCtrl = findViewById(R.id.pdfviewctrl);
        File lecture6 = Utils.copyResourceToLocal(this, R.raw.lecture, "lecture", ".ppt");
        Uri fileUri = Uri.fromFile(lecture6);

        try {
            String tag = fileUri.getPath();
            File file = new File(tag);
            if (!file.exists()) {
                throw new RuntimeException();
            }

            // Crashes after the following is called if 'armeabi-v7a'
            DocumentConversion documentConversion = Convert.universalConversion(tag, null);
            pdfViewCtrl.openUniversalDocument(documentConversion);
        } catch (Exception e) {

        }

    }
}
