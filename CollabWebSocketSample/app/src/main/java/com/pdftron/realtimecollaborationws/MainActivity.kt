package com.pdftron.realtimecollaborationws

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pdftron.collab.ui.viewer.CollabViewerBuilder
import com.pdftron.collab.ui.viewer.CollabViewerTabHostFragment
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.config.ViewerConfig
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val DEFAULT_FILE_URL =
        "https://pdftron.s3.amazonaws.com/downloads/pl/webviewer-demo.pdf"

    private lateinit var mPdfViewCtrlTabHostFragment: CollabViewerTabHostFragment

    private lateinit var mWSConnection: WSConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mWSConnection = WSConnection()

        mPdfViewCtrlTabHostFragment =
            createPdfViewerFragment(this, Uri.parse(DEFAULT_FILE_URL), getViewerConfig())

        mPdfViewCtrlTabHostFragment.addCollabHostListener(object :
            CollabViewerTabHostFragment.CollabTabHostListener {
            override fun onNavButtonPressed() {
                finish()
            }

            override fun onTabDocumentLoaded(p0: String?) {
                handleTabDocumentLoaded(p0!!)
            }

        })
    }

    fun handleTabDocumentLoaded(tag: String) {


        mWSConnection.start()
            .subscribeOn(Schedulers.newThread())
            .subscribe{
                if (it is ServerEvent.importXfdfCommand) {
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()

        mWSConnection.close()
    }

    private fun createPdfViewerFragment(
        context: Context,
        fileUri: Uri,
        config: ViewerConfig
    ): CollabViewerTabHostFragment {
        return CollabViewerBuilder.withUri(fileUri)
            .usingConfig(config)
            .build(context)
    }

    private fun getViewerConfig(): ViewerConfig {
        val toolManagerBuilder = ToolManagerBuilder.from()
            .setAnnotationLayerEnabled(false)
            .setAutoSelect(true)
        return ViewerConfig.Builder()
            .multiTabEnabled(false)
            .fullscreenModeEnabled(false)
            .showCloseTabOption(false)
            .saveCopyExportPath(this.filesDir.absolutePath)
            .openUrlCachePath(this.filesDir.absolutePath)
            .toolManagerBuilder(toolManagerBuilder)
            .build()
    }
}
