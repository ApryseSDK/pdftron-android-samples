package com.pdftron.pdftronsignapp.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pdftron.pdftronsignapp.R
import com.pdftron.pdftronsignapp.home.HomeFragment
import com.pdftron.pdftronsignapp.util.FirebaseControl
import com.pdftron.pdftronsignapp.util.RequestCode
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    companion object {
        val TAG = LoginFragment::class.java.name
        fun newInstance() = LoginFragment()
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
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        google_login_btn.setOnClickListener { googleSignIn() }
        register_btn.setOnClickListener { registerUser() }
        login_btn.setOnClickListener { emailLogin() }
    }

    private fun registerUser() {
        if (!isEmailOrPasswordBlank()) {
            return
        }
        if (editTextTextPassword.text.toString().length < 6) {
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.password_hint),
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val email = editTextTextEmailAddress.text.toString()
        val password = editTextTextPassword.text.toString()
        auth
            .createUserWithEmailAndPassword(
                email,
                password
            )
            .addOnCompleteListener { task ->
                activity?.let { activity ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        FirebaseControl().generateUserDocument(user)
                        activity.supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.content_frame,
                                HomeFragment.newInstance(),
                                HomeFragment.TAG
                            )
                            .commit()

                    } else {
                        Log.w(TAG, "registerUser:failure", task.exception)
                        Toast.makeText(activity, requireContext().getString(R.string.register_user_failure), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun emailLogin() {
        if (!isEmailOrPasswordBlank()) {
            return
        }
        val email = editTextTextEmailAddress.text.toString()
        val password = editTextTextPassword.text.toString()
        auth.signInWithEmailAndPassword(
            email,
            password
        )
            .addOnCompleteListener { task ->
                activity?.let { activity ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        FirebaseControl().generateUserDocument(user)
                        activity.supportFragmentManager.beginTransaction()
                            .replace(
                                R.id.content_frame,
                                HomeFragment.newInstance(),
                                HomeFragment.TAG
                            )
                            .commit()
                    } else {
                        Log.w(TAG, "emailLogin:failure", task.exception)
                        Toast.makeText(activity, requireContext().getString(R.string.email_login_failure), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun isEmailOrPasswordBlank(): Boolean {
        if (editTextTextEmailAddress.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.email_required), Toast.LENGTH_SHORT).show()
            return false
        }
        if (editTextTextPassword.text.toString().isEmpty()) {
            Toast.makeText(requireContext(), requireContext().getString(R.string.password_required), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RequestCode.GOOGLE_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        activity?.let {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        FirebaseControl().generateUserDocument(user)
                        it.supportFragmentManager.beginTransaction().replace(
                            R.id.content_frame,
                            HomeFragment.newInstance(),
                            HomeFragment.TAG
                        )
                            .commit()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(it, it.getString(R.string.sign_in_with_credential_failure), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RequestCode.GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(activity, requireContext().getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }
}