package com.pdftron.pdftronsignapp

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdftron.pdf.Action
import com.pdftron.pdf.Annot
import com.pdftron.pdf.Field
import com.pdftron.pdf.PDFDoc.e_both
import com.pdftron.pdf.annots.Widget
import com.pdftron.pdf.config.ViewerBuilder2
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2
import com.pdftron.pdf.utils.Utils
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType
import com.pdftron.pdf.widget.toolbar.builder.ToolbarItem
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars
import com.pdftron.pdftronsignapp.data.User
import com.pdftron.pdftronsignapp.home.HomeFragment
import com.pdftron.pdftronsignapp.listeners.MyAnnotationModificationListener
import com.pdftron.pdftronsignapp.listeners.MyBasicAnnotationListener
import com.pdftron.pdftronsignapp.listeners.MyTabHostListener
import com.pdftron.pdftronsignapp.login.LoginFragment
import com.pdftron.pdftronsignapp.util.*
import com.pdftron.sdf.Obj
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var mPdfViewCtrlTabHostFragment: PdfViewCtrlTabHostFragment2
    private lateinit var mBasicAnnotationListener: MyBasicAnnotationListener
    private lateinit var mAnnotationModificationListener: MyAnnotationModificationListener
    private val mFirebaseControl = FirebaseControl()
    private lateinit var usersList: List<User>
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
        mAnnotationModificationListener = MyAnnotationModificationListener { annotationsAdded(it) }
        mPdfViewCtrlTabHostFragment =
            ViewerBuilder2.withUri(Uri.fromFile(tempDoc)).usingConfig(config.build()).build(this)
        mPdfViewCtrlTabHostFragment.addHostListener(
            MyTabHostListener(
                { showBottomBar(false) },
                mPdfViewCtrlTabHostFragment,
                mBasicAnnotationListener,
                mAnnotationModificationListener
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
        mAnnotationModificationListener = MyAnnotationModificationListener { annotationsAdded(it) }
        mPdfViewCtrlTabHostFragment =
            ViewerBuilder2.withFile(fileForThisUser).usingConfig(config.build()).build(this)
        mPdfViewCtrlTabHostFragment.addHostListener(
            MyTabHostListener(
                { showBottomBar(true) },
                mPdfViewCtrlTabHostFragment,
                mBasicAnnotationListener,
                mAnnotationModificationListener
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

    private fun annotationsAdded(annots: MutableMap<Annot, Int>?) {
        annots!!.forEach {
            val annot = it.key
            if (annot.isValid) {
                if (annot.type == Annot.e_Widget) {
                    val widget = Widget(annot)
                    val field = widget.field
                    if (field.type == Field.e_text) {
                        val pdfDoc = mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.pdfDoc
                        val dateAction: Action = Action.createJavaScript(
                            pdfDoc,
                            "AFDate_FormatEx(\"d/m/yy\");"
                        )
                        val aaObj: Obj = widget.sdfObj.putDict("AA")
                        aaObj.put("K", dateAction.sdfObj)
                        aaObj.put("F", dateAction.sdfObj)
                    }
                }
            }
        }
    }

    private fun createViewerConfig(): ViewerConfig.Builder {
        return ViewerConfig.Builder()
            .fullscreenModeEnabled(false)
            .multiTabEnabled(false)
            .documentEditingEnabled(true)
            .longPressQuickMenuEnabled(true)
            .showSearchView(true)
            .showBottomToolbar(false)
            .showBottomNavBar(false)
            .showTopToolbar(false)
    }

    private fun buildAnnotationToolbar(): AnnotationToolbarBuilder {
        val builder = AnnotationToolbarBuilder
            .withTag("Sign Sample")
            .addToolButton(ToolbarButtonType.SIGNATURE_FIELD, CustomButtonId.SIGNATURE_FIELD)
            .addToolButton(ToolbarButtonType.FREE_TEXT, CustomButtonId.FREE_TEXT)
            .addToolButton(ToolbarButtonType.TEXT_FIELD, CustomButtonId.DATE)
            .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
            .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value())

        //change icon on text field to a calendar so users use it as a date field. Date selection functionality is added in the annotationsAdded function
        val toolbarItems: List<ToolbarItem> = builder.toolbarItems
        val item = toolbarItems.first { it.toolbarButtonType == ToolbarButtonType.TEXT_FIELD }
        item.setIcon(R.drawable.ic_date_range_24px)

        return builder
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

        mBasicAnnotationListener.setCurrentUser(usersList.first())
        main_recycler.adapter =
            BottomBarAdapter(usersList) { mBasicAnnotationListener.setCurrentUser(it) }
        main_recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.toolManager.disableAnnotEditing(
            arrayOf(Annot.e_Widget)
        )
    }

    private fun signingBottomBarSetup() {
        send_btn.text = send_btn.context.getString(R.string.finish)
        send_btn.setOnClickListener {
            mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.save(false, true, true)
            val filePath = mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.filePath
            val pdfDoc = mPdfViewCtrlTabHostFragment.currentPdfViewCtrlFragment.pdfDoc
            if (areAllSignAndDateFieldsComplete(pdfDoc)) {
                val fdfDoc = pdfDoc.fdfExtract(e_both)
                val xdfString = fdfDoc.saveAsXFDF()
                mFirebaseControl.updateDocumentToSign(
                    filePath,
                    pdfDoc,
                    this.docId,
                    xdfString
                ) {
                    supportFragmentManager.popBackStack()
                    bottom_bar.visibility = View.GONE
                }
            } else {
                Toast.makeText(
                    this,
                    this.getString(R.string.dates_and_signatures_required),
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
}