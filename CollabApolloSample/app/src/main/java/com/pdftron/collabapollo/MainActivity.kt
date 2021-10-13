package com.pdftron.collabapollo

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pdftron.collab.client.CollabClient
import com.pdftron.collab.ui.viewer.CollabViewerBuilder2
import com.pdftron.collab.ui.viewer.CollabViewerTabHostFragment2
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.config.ViewerConfig

class MainActivity : AppCompatActivity() {

    private val DEFAULT_FILE_NAME = "demo-annotated.pdf"
    private val DEFAULT_FILE_URL =
        "https://pdftron.s3.amazonaws.com/downloads/pl/${DEFAULT_FILE_NAME}"

    private lateinit var mPdfViewCtrlTabHostFragment: CollabViewerTabHostFragment2

    private var mCollabClient = CollabClient.Builder()
        .url("http://192.168.0.15:3000")
        .subscriptionUrl("ws://192.168.0.15:3000/subscribe")
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPdfViewCtrlTabHostFragment =
            createPdfViewerFragment(this, Uri.parse(DEFAULT_FILE_URL), getViewerConfig())
        mPdfViewCtrlTabHostFragment.addCollabHostListener(object :
            CollabViewerTabHostFragment2.CollabTabHostListener {
            override fun onNavButtonPressed() {
                finish()
            }

            override fun onTabDocumentLoaded(p0: String?) {
                handleTabDocumentLoaded(p0!!)
            }
        })

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment, null)
        ft.commit()
    }

    fun handleTabDocumentLoaded(tag: String) {
        if (mPdfViewCtrlTabHostFragment is CollabViewerTabHostFragment2) {
            val collabHost = mPdfViewCtrlTabHostFragment as CollabViewerTabHostFragment2
            val collabManager = collabHost.collabManager
            val pdfViewCtrl = collabHost.currentPdfViewCtrlFragment.pdfViewCtrl

            mCollabClient.start(collabManager)
            mCollabClient.loginAnonymous("testUser")
            mCollabClient.loadDocument("12345", DEFAULT_FILE_NAME, true, pdfViewCtrl)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mCollabClient.destroy()
    }

    private fun createPdfViewerFragment(
        context: Context,
        fileUri: Uri,
        config: ViewerConfig
    ): CollabViewerTabHostFragment2 {
        return CollabViewerBuilder2
            .withUri(fileUri)
            .usingConfig(getViewerConfig())
            .usingNavIcon(R.drawable.ic_arrow_back_white_24dp)
            .usingTheme(R.style.PDFTronAppTheme)
            .build(this, CollabViewerTabHostFragment2::class.java)
    }

    private fun getViewerConfig(): ViewerConfig {
        val toolManagerBuilder = ToolManagerBuilder.from()
            .setAnnotationLayerEnabled(false)
            .setAutoSelect(true)
        return ViewerConfig.Builder()
            .multiTabEnabled(false)
            .showCloseTabOption(false)
            .saveCopyExportPath(this.filesDir.absolutePath)
            .openUrlCachePath(this.filesDir.absolutePath)
            .toolManagerBuilder(toolManagerBuilder)
            .build()
    }
}