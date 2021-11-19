package com.pdftron.demohtml2pdf

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.DocumentActivity
import com.pdftron.pdf.utils.HTML2PDF

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val htmlDocument = "<!DOCTYPE html>\n<div style=\"height:100px; width:100px; \">" +
                "<span>עברית=בדיקה<br/>BLABLA=Long English Text 1-2-3-4-5-6-7-8-9-10-11-12-13-14-15-16-17-18-19-20</span>\n" +
                "</div>"

//        val htmlDocument = "<!DOCTYPE html>\n" +
//                "<html lang=\"de\">\n" +
//                "\n" +
//                "<head>\n" +
//                " <meta charset=\"UTF-8\" />\n" +
//                " <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
//                " <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\" />\n" +
//                " <title>Test Page</title>\n" +
//                " <style>\n" +
//                " body {\n" +
//                " font-family: Helvetica, Arial, sans-serif;\n" +
//                " padding: 10px;\n" +
//                " -webkit-hyphens: auto;\n" +
//                " -moz-hyphens: auto;\n" +
//                " hyphens: auto;\n" +
//                " }\n" +
//                "\n" +
//                " h2 {\n" +
//                " width: 100%;\n" +
//                " border-bottom: 1px solid #000000;\n" +
//                " }\n" +
//                "\n" +
//                " </style>\n" +
//                "</head>\n" +
//                "\n" +
//                "<body>\n" +
//                "<h2>Hello World</h2>\n" +
//                "<p> Test Text </p>\n" +
//                "</body>\n" +
//                "</html>"

        val html2PDF = HTML2PDF(this.applicationContext)
        html2PDF.setOutputFolder(this.cacheDir)
        html2PDF.setOutputFileName("test_html_string.pdf")
        html2PDF.setHTML2PDFListener(object : HTML2PDF.HTML2PDFListener {
            override fun onConversionFinished(pdfOutput: String, isLocal: Boolean) {
                // Handle callback when conversion finished
                Log.d("sgong", "out: $pdfOutput")
                openDocument(pdfOutput)
                finish()
            }

            override fun onConversionFailed(error: String?) {
                // Handle callback if conversion failed
                Log.e("html2pdf", error)
            }
        })
        html2PDF.fromHTMLDocument(null, htmlDocument)
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
