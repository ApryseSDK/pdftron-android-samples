package com.pdftron.realtimecollaborationws

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pdftron.collab.ui.viewer.CollabViewerBuilder2
import com.pdftron.collab.ui.viewer.CollabViewerTabHostFragment2
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.config.ViewerConfig
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val DEFAULT_FILE_URL =
        "https://pdftron.s3.amazonaws.com/downloads/pl/webviewer-demo.pdf"

    private val nameList = arrayOf("Bulbasaur", "Ivysaur", "Venusaur",
        "Charmander", "Charmeleon", "Charizard",
        "Squirtle", "Wartortle", "Blastoise")

    private lateinit var mPdfViewCtrlTabHostFragment: CollabViewerTabHostFragment2

    private val mWSConnection = WSConnection()

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

        val collabManager = mPdfViewCtrlTabHostFragment.collabManager
        val userName = nameList[Math.floor(Math.random() * nameList.size).toInt()]
        collabManager!!.setCurrentUser(userName, userName)
        collabManager.setCurrentDocument(tag)

        collabManager.setCollabManagerListener { action, annotations, documentId, userName ->
            // local change, send info to server here
            mWSConnection.sendMessage(annotations)
        }

        mWSConnection.start()
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it is ServerEvent.importXfdfCommand) {
                    collabManager.importAnnotationCommand(it.xfdfCommand)
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
    ): CollabViewerTabHostFragment2 {
        return CollabViewerBuilder2.withUri(fileUri)
            .usingConfig(config)
            .build(context)
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
