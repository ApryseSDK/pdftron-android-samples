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

        val htmlDocument = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<!-- Text between angle brackets is an HTML tag and is not displayed.\n" +
                "Most tags, such as the HTML and /HTML tags that surround the contents of\n" +
                "a page, come in pairs; some tags, like HR, for a horizontal rule, stand \n" +
                "alone. Comments, such as the text you're reading, are not displayed when\n" +
                "the Web page is shown. The information between the HEAD and /HEAD tags is \n" +
                "not displayed. The information between the BODY and /BODY tags is displayed.-->\n" +
                "<head>\n" +
                "<title>Enter a title, displayed at the top of the window.</title>\n" +
                "</head>\n" +
                "<!-- The information between the BODY and /BODY tags is displayed.-->\n" +
                "<body>\n" +
                "<h1>Enter the main heading, usually the same as the title.</h1>\n" +
                "<p>Be <b>bold</b> in stating your key points. Put them in a list: </p>\n" +
                "<ul>\n" +
                "<li>The first item in your list</li>\n" +
                "<li>The second item; <i>italicize</i> key words</li>\n" +
                "</ul>\n" +
                "<p>Improve your image by including an image. </p>\n" +
                "<p><img src=\"http://www.mygifs.com/CoverImage.gif\" alt=\"A Great HTML Resource\"></p>\n" +
                "<p>Add a link to your favorite <a href=\"https://www.dummies.com/\">Web site</a>.\n" +
                "Break up your page with a horizontal rule or two. </p>\n" +
                "<hr>\n" +
                "<p>Finally, link to <a href=\"page2.html\">another page</a> in your own Web site.</p>\n" +
                "<!-- And add a copyright notice.-->\n" +
                "<p>&#169; Wiley Publishing, 2011</p>\n" +
                "</body>\n" +
                "</html>"

        val html2PDF = HTML2PDF(this)
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
