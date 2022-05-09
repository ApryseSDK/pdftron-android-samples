package com.pdftron.nativecppexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pdftron.pdf.PDFNet
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize PDFTron, this will start FontConfig
        PDFNet.initialize(this, R.raw.pdfnet, "")

        // Font list
        sample_text.text = fontListFromJNI()
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun fontListFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("PDFNetC")
        }
    }
}
