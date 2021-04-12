package com.pdftron.pdftronsignapp.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdftron.pdftronsignapp.R
import com.pdftron.pdftronsignapp.data.DocumentToSign
import kotlinx.android.synthetic.main.document_to_sign_view_holder.view.*
import java.text.SimpleDateFormat

class DocumentToSignAdapter(private val docsToSignList: MutableList<DocumentToSign>, val signClicked: (DocumentToSign)->Unit): RecyclerView.Adapter<DocumentToSignViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentToSignViewHolder {
        return DocumentToSignViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DocumentToSignViewHolder, position: Int) {
        val document = docsToSignList[position]
        holder.itemView.from_txt.text = document.email
        val sdf = SimpleDateFormat("dd MMM yyyy hh:mm")
        val requestedTime = sdf.format(document.requestedTime.toDate())
        holder.itemView.when_txt.text = requestedTime
        holder.itemView.sign_btn.setOnClickListener { signClicked(document) }
    }

    override fun getItemCount() = docsToSignList.count()
}

class DocumentToSignViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    companion object{
        fun from(parent: ViewGroup): DocumentToSignViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.document_to_sign_view_holder, parent, false)
            return DocumentToSignViewHolder(view)
        }
    }
}