package com.pdftron.pdftronsignapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.pdftron.pdf.controls.PdfViewCtrlTabHostFragment2
import com.pdftron.pdf.model.FileInfo
import com.pdftron.pdftronsignapp.login.LoginFragment
import com.pdftron.pdftronsignapp.util.CustomButtonId

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.content_frame, LoginFragment.newInstance(), LoginFragment.TAG).commitAllowingStateLoss()
    }
}