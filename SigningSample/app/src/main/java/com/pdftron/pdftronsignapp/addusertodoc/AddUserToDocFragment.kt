package com.pdftron.pdftronsignapp.addusertodoc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.pdftron.pdftronsignapp.MainActivity
import com.pdftron.pdftronsignapp.R
import com.pdftron.pdftronsignapp.data.User
import kotlinx.android.synthetic.main.fragment_add_user_to_doc.*

class AddUserToDocFragment : Fragment() {

    private val userList = mutableListOf<User>()
    private lateinit var adapter: AddUserToDocAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_user_to_doc, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AddUserToDocAdapter(userList)
        recycler_view.adapter = adapter
        recycler_view.layoutManager = LinearLayoutManager(view.context)

        add_user.setOnClickListener {
            if (name_et.text.toString().isEmpty()) {
                Toast.makeText(this.context, add_user.context.getString(R.string.name_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (email_et.text.toString().isEmpty()) {
                Toast.makeText(this.context, add_user.context.getString(R.string.email_required), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            addUser(name_et.text.toString(), email_et.text.toString())
        }
        next_btn.setOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
            (activity as MainActivity).addViewerFragment(userList)
        }
    }

    companion object {
        fun newInstance() = AddUserToDocFragment()
    }

    private fun addUser(displayName: String, email: String) {
        val user = User(
            displayName = displayName,
            email = email,
            photoURL = ""
        )

        userList.add(0, user)
        adapter.notifyItemInserted(0)

        name_et.text.clear()
        email_et.text.clear()
    }
}