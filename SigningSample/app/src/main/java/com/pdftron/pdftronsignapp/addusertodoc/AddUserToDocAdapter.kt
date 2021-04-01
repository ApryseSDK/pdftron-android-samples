package com.pdftron.pdftronsignapp.addusertodoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdftron.pdftronsignapp.R
import com.pdftron.pdftronsignapp.data.User
import kotlinx.android.synthetic.main.add_user_to_doc_view_holder.view.*

class AddUserToDocAdapter(private val userList: MutableList<User>): RecyclerView.Adapter<AddUserToDocViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddUserToDocViewHolder {
        return AddUserToDocViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AddUserToDocViewHolder, position: Int) {
        val user = userList[position]
        holder.itemView.name_txt.text = user.displayName
        holder.itemView.email_txt.text = user.email
    }

    override fun getItemCount() = userList.count()
}

class AddUserToDocViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    companion object {
        fun from(parent: ViewGroup): AddUserToDocViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.add_user_to_doc_view_holder, parent, false)
            return AddUserToDocViewHolder(view)
        }
    }
}