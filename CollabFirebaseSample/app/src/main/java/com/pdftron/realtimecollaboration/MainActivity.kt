package com.pdftron.realtimecollaboration

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pdftron.collab.ui.viewer.CollabViewerBuilder
import com.pdftron.collab.ui.viewer.CollabViewerTabHostFragment
import com.pdftron.collab.viewmodel.DocumentViewModel
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.dialog.simpleinput.TextInputDialog
import com.pdftron.pdf.dialog.simpleinput.TextInputResult
import com.pdftron.pdf.dialog.simpleinput.TextInputViewModel
import com.pdftron.pdf.utils.Event
import com.pdftron.pdf.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private val USER_NAME_REQUEST_CODE = 1000

    private val DEFAULT_FILE_URL =
        "https://pdftron.s3.amazonaws.com/downloads/pl/demo-annotated.pdf"

    private lateinit var server: Server

    private var mPdfViewCtrlTabHostFragment: CollabViewerTabHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        server = Server(this.application)

        val textInputViewModel = ViewModelProviders.of(this).get(TextInputViewModel::class.java)
        textInputViewModel.observeOnComplete(this, Observer<Event<TextInputResult>> {
            if (it != null && !it.hasBeenHandled()) {
                val result = it.contentIfNotHandled!!
                if (result.requestCode == USER_NAME_REQUEST_CODE) {
                    server.updateAuthor(result.result)
                }
            } else {
                server.updateAuthor("Guest")
            }
        })

        val documentViewModel = ViewModelProviders.of(this).get(DocumentViewModel::class.java)
        documentViewModel.setCustomConnection(server)

        setUpSampleView()
    }

    override fun onDestroy() {
        super.onDestroy()

        server.signOut()
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

    private fun createTabletViewerFragment(
        activity: FragmentActivity,
        fileUri: Uri,
        config: ViewerConfig
    ): CollabViewerTabHostFragment {
        return CollabViewerBuilder.withUri(fileUri)
            .usingConfig(config)
            .usingTabClass(CustomTabFragment::class.java)
            .build(activity)
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

    private fun setUpSampleView() {
        mPdfViewCtrlTabHostFragment =
            if (Utils.isTablet(this) && Utils.isLandscape(this))
                createTabletViewerFragment(this, Uri.parse(DEFAULT_FILE_URL), getViewerConfig())
            else
                createPdfViewerFragment(this, Uri.parse(DEFAULT_FILE_URL), getViewerConfig())

        mPdfViewCtrlTabHostFragment!!.addCollabHostListener(object :
            CollabViewerTabHostFragment.CollabTabHostListener {
            override fun onNavButtonPressed() {
                finish()
            }

            override fun onTabDocumentLoaded(p0: String?) {
                server.signIn().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        if (it is ServerEvent.SignUp) {
                            if (it.response.isSuccessful) {
                                Log.d(TAG, "signInAnonymously:success")
                                showInput()
                            } else {
                                Log.w(TAG, "signInAnonymously:failure", it.response.exception)
                            }
                        } else if (it is ServerEvent.SignIn) {
                            Log.d(TAG, "signInAnonymously:success as: " + it.name)
                            showName(it.name)
                        }
                    }
            }
        })

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, mPdfViewCtrlTabHostFragment!!, null)
        ft.commit()
    }

    private fun showName(name: String) {
        Utils.safeShowAlertDialog(
            this,
            String.format(getString(R.string.existing_user_name_body), name),
            getString(R.string.existing_user_name_title)
        )
    }

    private fun showInput() {
        val dialog = TextInputDialog.newInstance(
            USER_NAME_REQUEST_CODE,
            R.string.user_name_title,
            R.string.user_name_body,
            R.string.ok,
            R.string.cancel
        )
        dialog.isCancelable = false
        dialog.setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, R.style.CustomAppTheme)
        dialog.show(supportFragmentManager, TextInputDialog.TAG)
    }
}
