package com.pdftron.demohtml2pdf

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.DocumentActivity
import com.pdftron.pdf.utils.HTML2PDF
import com.pdftron.pdf.utils.Utils


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val htmlFile =
            Utils.copyResourceToLocal(this, R.raw.test, "test", ".html")

        val webView = WebView(this)
        webView.loadUrl("file://" + htmlFile.absolutePath)

        val html2PDF = HTML2PDF(webView)
        html2PDF.setOutputFolder(this.cacheDir)
        html2PDF.setHTML2PDFListener(object : HTML2PDF.HTML2PDFListener {
            override fun onConversionFinished(pdfOutput: String, isLocal: Boolean) {
                // Handle callback when conversion finished

                openDocument(pdfOutput)
                finish()
            }

            override fun onConversionFailed(error: String?) {
                // Handle callback if conversion failed
                Log.e("html2pdf", error)
            }
        })
        html2PDF.doHtml2Pdf()
    }

    fun openDocument(filepath: String) {
        val config = ViewerConfig.Builder()
            .build()

        val intent = DocumentActivity.IntentBuilder.fromActivityClass(
            this,
            DocumentActivity::class.java
        )
            .withUri(Uri.parse(filepath))
            .usingConfig(config)
            .usingNewUi(true)
            .build()
        startActivity(intent)
    }
}
