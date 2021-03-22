package com.pdftron.pdftronsignapp.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.pdftron.pdf.config.ViewerBuilder2
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2
import com.pdftron.pdf.widget.bottombar.builder.BottomBarBuilder
import com.pdftron.pdf.widget.toolbar.builder.AnnotationToolbarBuilder
import com.pdftron.pdf.widget.toolbar.builder.ToolbarButtonType
import com.pdftron.pdf.widget.toolbar.component.DefaultToolbars
import com.pdftron.pdftronsignapp.util.FirebaseControl
import com.pdftron.pdftronsignapp.R
import com.pdftron.pdftronsignapp.login.LoginFragment
import com.pdftron.pdftronsignapp.util.CustomButtonId
import com.pdftron.pdftronsignapp.listeners.MyTabHostListener
import com.pdftron.pdftronsignapp.util.RequestCode
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var mPdfViewCtrlTabHostFragment : PdfViewCtrlTabHostFragment2

    companion object {
        val TAG = HomeFragment::class.java.name
        fun newInstance() = HomeFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            googleSignInClient = GoogleSignIn.getClient(it, gso)

            auth = Firebase.auth
        }

        FirebaseControl().getUserDocument { updateUi(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logout_btn.setOnClickListener { logout() }
        create_new_doc_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
            }
            startActivityForResult(intent, RequestCode.FILE_REQUEST_CODE)
        }
    }

    private fun logout() {
        auth.signOut()
        googleSignInClient.signOut()
        activity?.supportFragmentManager?.beginTransaction()?.replace(
            R.id.content_frame,
            LoginFragment.newInstance(),
            LoginFragment.TAG
        )?.commitAllowingStateLoss()
    }

    private fun updateUi(document: DocumentSnapshot?) {
        if (document == null)
            return
        //todo: update ui?
    }

    private fun addViewerFragment(
        @IdRes fragmentContainer: Int,
        activity: AppCompatActivity,
        fileUri: Uri
    ) {

        val annotationToolbarBuilder = AnnotationToolbarBuilder
            .withTag("Sign Sample")
            .addCustomButton(R.string.custom, R.drawable.ic_annotation_caret_black_24dp, CustomButtonId.PROFILE)
            .addToolButton(ToolbarButtonType.SIGNATURE_FIELD, CustomButtonId.SIGNATURE_FIELD)
            .addToolButton(ToolbarButtonType.FREE_TEXT, CustomButtonId.FREE_TEXT)
            .addToolButton(ToolbarButtonType.DATE, CustomButtonId.DATE)
            .addToolStickyButton(ToolbarButtonType.UNDO, DefaultToolbars.ButtonId.UNDO.value())
            .addToolStickyButton(ToolbarButtonType.REDO, DefaultToolbars.ButtonId.REDO.value())

        val bottomBarBuilder = BottomBarBuilder
            .withTag("Bottom Bar")
            .addCustomButton(R.string.save_as_wait, R.drawable.serif_a_letter_black, CustomButtonId.SAVE)

        val config = ViewerConfig.Builder()
            .fullscreenModeEnabled(true)
            .multiTabEnabled(false)
            .documentEditingEnabled(true)
            .longPressQuickMenuEnabled(true)
            .showSearchView(true)
            .addToolbarBuilder(annotationToolbarBuilder)
            .showBottomToolbar(true)
            .bottomBarBuilder(bottomBarBuilder)
            .build()

        // Create the viewer fragment
        mPdfViewCtrlTabHostFragment = ViewerBuilder2.withUri(fileUri).usingConfig(config).build(activity)
        mPdfViewCtrlTabHostFragment.addHostListener(MyTabHostListener(mPdfViewCtrlTabHostFragment))

        // Add the fragment to the layout fragment container
        activity.supportFragmentManager.beginTransaction()
            .replace(fragmentContainer, mPdfViewCtrlTabHostFragment)
            .addToBackStack("PdfViewCtrlTabHostFragment2")
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.FILE_REQUEST_CODE) {
            if (data != null && data.data != null)
                addViewerFragment(
                    R.id.content_frame,
                    activity as AppCompatActivity,
                    data.data as Uri
                )
        }
    }
}