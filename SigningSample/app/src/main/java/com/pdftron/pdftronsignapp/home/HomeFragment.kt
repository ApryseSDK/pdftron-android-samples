package com.pdftron.pdftronsignapp.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import com.pdftron.pdftronsignapp.MainActivity
import com.pdftron.pdftronsignapp.R
import com.pdftron.pdftronsignapp.addusertodoc.AddUserToDocFragment
import com.pdftron.pdftronsignapp.data.DocumentToSign
import com.pdftron.pdftronsignapp.login.LoginFragment
import com.pdftron.pdftronsignapp.util.FirebaseControl
import com.pdftron.pdftronsignapp.util.RequestCode
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File

class HomeFragment : Fragment() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var mDocumentToSignAdapter: DocumentToSignAdapter? = null
    private var mSignedDocumentsAdapter: SignedDocumentAdapter? = null
    private val mFireBaseControl = FirebaseControl()
    private val documentReferences = mutableListOf<DocumentToSign>()
    private val signedDocuments = mutableListOf<DocumentToSign>()

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

        if(auth.currentUser.displayName.isNullOrEmpty())
            user_name.text = auth.currentUser.email
        else
            user_name.text = auth.currentUser.displayName
        setUpRecyclerview()
        getDocumentsToSign()
        getSignedDocuments()
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

    private fun setUpRecyclerview() {
        mDocumentToSignAdapter = DocumentToSignAdapter(documentReferences) { documentToSign ->
            mFireBaseControl.getDocumentUriFromFb(documentToSign.docRef) { uri ->
                showDocument(
                    documentToSign.docId,
                    uri
                )
            }
        }
        home_recycler.layoutManager =
            LinearLayoutManager(home_recycler.context, LinearLayoutManager.VERTICAL, false)
        home_recycler.adapter = mDocumentToSignAdapter

        mSignedDocumentsAdapter = SignedDocumentAdapter(signedDocuments) { documentToSign ->
            mFireBaseControl.getDocumentUriFromFb(documentToSign.docRef) { uri ->
                showDocument(
                    documentToSign.docId,
                    uri
                )
            }
        }
        home_recycler_signed_docs.layoutManager =
            LinearLayoutManager(home_recycler.context, LinearLayoutManager.VERTICAL, false)
        home_recycler_signed_docs.adapter = mSignedDocumentsAdapter

        swipeContainer.setOnRefreshListener {
            getDocumentsToSign()
            swipeContainer.isRefreshing = false
        }
        swipeContainerSigned.setOnRefreshListener {
            getSignedDocuments()
            swipeContainerSigned.isRefreshing = false
        }
    }

    private fun getDocumentsToSign() {
        mFireBaseControl.searchForDocumentToSign {
            val originalItemsCount = documentReferences.count()
            documentReferences.clear()
            if (originalItemsCount > 0) {
                mDocumentToSignAdapter?.notifyItemRangeRemoved(0, originalItemsCount)
            }
            documentReferences.addAll(it)
            if (documentReferences.count() > 0) {
                mDocumentToSignAdapter?.notifyItemRangeInserted(0, it.count())
            }
        }
    }

    private fun getSignedDocuments() {
        mFireBaseControl.searchForDocumentsSigned {
            val originalItemsCount = signedDocuments.count()
            signedDocuments.clear()
            if (originalItemsCount > 0) {
                mSignedDocumentsAdapter?.notifyItemRangeRemoved(0, originalItemsCount)
            }
            signedDocuments.addAll(it)
            if (signedDocuments.count() > 0)
                mSignedDocumentsAdapter?.notifyItemRangeInserted(0, it.count())
        }
    }

    private fun showDocument(docId: String, file: File) {
        (activity as MainActivity).showDocument(docId, file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode.FILE_REQUEST_CODE) {
            if (data != null && data.data != null) {
                (activity as MainActivity).buildViewerFragment(
                    data.data as Uri
                )
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.content_frame, AddUserToDocFragment.newInstance())
                    ?.addToBackStack("AddUserToDocFragment")?.commit()
            }
        }
    }
}