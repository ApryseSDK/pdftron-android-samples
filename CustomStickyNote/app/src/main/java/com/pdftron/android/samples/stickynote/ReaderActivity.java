package com.pdftron.android.samples.stickynote;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.pdftron.pdf.controls.DocumentActivity;

/**
 * ReaderActivity is just used to start a DocumentActivity so we can try out our new Sticky Note Icon.
 */
public class ReaderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        // Open our sample document in the 'res/raw' resource folder
        DocumentActivity.openDocument(this, R.raw.sample);
        finish();
    }
}
