package com.pdftron.standardpdfviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pdftron.pdf.utils.Utils;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // uncomment to use PDFViewCtrl only
//        PDFViewCtrl pdfViewCtrl = findViewById(R.id.pdfviewctrl);
//        try {
//            pdfViewCtrl.openPDFUri(Uri.parse("https://pdftron.s3.amazonaws.com/downloads/pdfref.pdf"), null);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        File f = Utils.copyResourceToLocal(this, R.raw.sample, "sample", ".pptx");
        Uri uri = Uri.fromFile(f);
        boolean newUi = true;

        ViewerConfig.Builder builder = new ViewerConfig.Builder()
//                .useStandardLibrary(true)
                .openUrlCachePath(this.getCacheDir().getAbsolutePath())
                .saveCopyExportPath(this.getCacheDir().getAbsolutePath());
        Intent intent = DocumentActivity.IntentBuilder.fromActivityClass(this, DocumentActivity.class)
                .withUri(uri)
                .usingConfig(builder.build())
                .usingNewUi(newUi)
                .build();
        startActivity(intent);
        finish();
    }
}
