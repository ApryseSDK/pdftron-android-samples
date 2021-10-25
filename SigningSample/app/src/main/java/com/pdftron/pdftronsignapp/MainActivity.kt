package com.pdftron.pdftronsignapp

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdftron.pdf.Annot
import com.pdftron.pdf.PDFDoc
import com.pdftron.pdf.annots.Widget
import com.pdftron.pdf.config.ToolManagerBuilder
import com.pdftron.pdf.config.ViewerBuilder2
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2
import com.pdftron.pdf.tools.ToolManager.AnnotationModificationListener
import com.pdftron.pdf.utils.AnnotUtils
import com.pdftron.pdf.utils.Utils
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars
import com.pdftron.pdftronsignapp.customtool.DateFieldCreate
import com.pdftron.pdftronsignapp.data.User
import com.pdftron.pdftronsignapp.home.HomeFragment
import com.pdftron.pdftronsignapp.listeners.MyBasicAnnotationListener
import com.pdftron.pdftronsignapp.listeners.MyTabHostListener
import com.pdftron.pdftronsignapp.login.LoginFragment
import com.pdftron.pdftronsignapp.util.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import com.pdftron.pdf.utils.AnnotUtils.KEY_WidgetAuthor


class MainActivity : AppCompatActivity() {
    private lateinit var mPdfViewCtrlTabHostFragment: PdfViewCtrlTabHostFragment2
    private lateinit var mBasicAnnotationListener: MyBasicAnnotationListener
    private val mFirebaseControl = FirebaseControl()
    private lateinit var usersList: List<User>
    private lateinit var mUser: User;
    private var docId: String = ""
    private lateinit var progressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, HomeFragment.newInstance(), HomeFragment.TAG)
                .commit()
            return
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, LoginFragment.newInstance(), LoginFragment.TAG)
            .commit()

        checkPermissions()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        bottom_bar.visibility = View.GONE
    }

    fun buildViewerFragment(fileUri: Uri) {
        val tempDoc =
            Utils.duplicateInDownload(this, this.contentResolver, fileUri, "tempDoc").blockingGet()
        val config = createViewerConfig()
        config.addToolbarBuilder(buildAnnotationToolbar())
        // Create the viewer fragment
        mBasicAnnotationListener = MyBasicAnnotationListener()
        mPdfViewCtrlTabHostFragment =
            ViewerBuilder2.withUri(Uri.fromFile(tempDoc)).usingConfig(config.build()).build(this)
        mPdfViewCtrlTabHostFragment.addHostListener(
            MyTabHostListener(
                { showBottomBar(false) },
                mPdfViewCtrlTabHostFragment,
                mBasicAnnotationListener
            )
        )
    }

    fun showDocument(docId: String, file: File) {
        showProgressDialog()
        this.docId = docId
        val fileForThisUser = removeAnnotationsForOtherUsers(file)
        val config = createViewerConfig()
        config.showAppBar(false)
        mBasicAnnotationListener = MyBasicAnnotationListener()
        mPdfViewCtrlTabHostFragment =
            ViewerBuilder2.withFile(fileForThisUser).usingConfig(config.build()).build(this)
        mPdfViewCtrlTabHostFragment.addHostListener(
            MyTabHostListener(
                { showBottomBar(true) },
                mPdfViewCtrlTabHostFragment,
                mBasicAnnotationListener
            )
        )
        showPdfViewCtrlTabHostFragment()
    }

    fun addViewerFragment(users: List<User>) {
        showProgressDialog()
        // Add the fragment to the layout fragment container
        usersList = users
        showPdfViewCtrlTabHostFragment()
    }

    private fun showProgressDialog() {
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun createViewerConfig(): ViewerConfig.Builder {
        return ViewerConfig.Builder()
            .fullscreenModeEnabled(false)
            .multiTabEnabled(false)
            .documentEditingEnabled(true)
            .longPressQuickMenuEnabled(true)
            .showSearchView(true)
            .showBottomToolbar(false)
            .showTopToolbar(false)
            .toolManagerBuilder(toolManagerBuilder())
    }

    private fun toolManagerBuilder(): ToolManagerBuilder {
        return ToolManagerBuilder.from()
            .addCustomizedTool(DateFieldCreate.MODE, DateFieldCreate::class.java)
    }

    private fun buildAnnotationToolbar(): AnnotationToolbarBuilder {
        return AnnotationToolbarBuilder
            .withTag("Sign Sample")
            .addToolButton(ToolbarButtonType.SIGNATURE_FIELD, CustomButtonId.SIGNATURE_FIELD)
            .addToolButton(ToolbarButtonType.TEXT_FIELD, CustomButtonId.TEXT_FIELD)
            .addCustomSelectableButton(
                R.string.date,
                R.drawable.ic_date_range_24px,
                CustomButtonId.DATE
            )
            .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
            .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value())
    }

    private fun showBottomBar(isSigning: Boolean) {
        progressDialog.hide()
        bottom_bar.visibility = View.VISIBLE

        if (isSigning) {
            main_recycler?.visibility = View.INVISIBLE
            signingBottomBarSetup()
            mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.toolManager.enableAnnotEditing(
                arrayOf(Annot.e_Widget)
            )
            return
        }
        main_recycler?.visibility = View.VISIBLE
        send_btn.text = send_btn.context.getString(R.string.send)
        send_btn.setOnClickListener { sendDocument() }

        mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.toolManager.addAnnotationModificationListener(
            mModificationListener
        )
        mUser = usersList.first();
        mBasicAnnotationListener.setCurrentUser(usersList.first())
        main_recycler.adapter =
            BottomBarAdapter(usersList) {
                mBasicAnnotationListener.setCurrentUser(it)
                mUser = it;
            }
        main_recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.toolManager.disableAnnotEditing(
            arrayOf(Annot.e_Widget)
        )

        // flatten existing annotations and form fields
        mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.pdfViewCtrl.docLock(true) {
            mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.pdfDoc.flattenAnnotationsAdvanced(
                arrayOf(
                    PDFDoc.FlattenMode.ANNOTS,
                    PDFDoc.FlattenMode.FORMS
                )
            )
            // update the viewer
            mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.pdfViewCtrl.update(true)
        }
    }

    private fun signingBottomBarSetup() {
        send_btn.text = send_btn.context.getString(R.string.finish)
        send_btn.setOnClickListener {
            mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.save(false, true, true)
            val pdfDoc = mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.pdfDoc
            if (areAllSignAndDateFieldsComplete(pdfDoc)) {
                mFirebaseControl.updateDocumentToSign(
                    mPdfViewCtrlTabHostFragment,
                    this.docId
                ) {
                    supportFragmentManager.popBackStack()
                    bottom_bar.visibility = View.GONE
                }
            } else {
                Toast.makeText(
                    this,
                    this.getString(R.string.all_fields_required),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showPdfViewCtrlTabHostFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_frame, mPdfViewCtrlTabHostFragment)
            .addToBackStack("PdfViewCtrlTabHostFragment")
            .commit()
    }

    private fun sendDocument() {
        mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.save(false, true, true)
        val path = mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.filePath
        val file = File(path)
        mFirebaseControl.addDocumentToSign(
            file,
            usersList.map { it.email }) {
            supportFragmentManager.popBackStack()
            bottom_bar.visibility = View.GONE
            deleteTempFile(file)
        }
    }

    private fun deleteTempFile(file: File) {
        try {
            file.delete()
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                RequestCode.PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private val mModificationListener: AnnotationModificationListener =
        object : AnnotationModificationListener {
            override fun onAnnotationsAdded(annots: Map<Annot, Int>) {

                for ((key, value) in annots) {
                    if (key.type == Annot.e_Widget) {
                        val widget = Widget(key)
                        widget.sdfObj?.putText(KEY_WidgetAuthor, mUser.displayName)
                    }
                    var author = AnnotUtils.getAuthor(key)
                    Log.i("author", author ?: "")
                }

            }

            override fun onAnnotationsPreModify(annots: Map<Annot, Int>) {

            }

            override fun onAnnotationsModified(annots: Map<Annot, Int>, extra: Bundle) {

            }

            override fun onAnnotationsPreRemove(annots: Map<Annot, Int>) {

            }

            override fun onAnnotationsRemoved(annots: Map<Annot, Int>) {

            }

            override fun onAnnotationsRemovedOnPage(pageNum: Int) {

            }

            override fun annotationsCouldNotBeAdded(errorMessage: String) {

            }
        }
}