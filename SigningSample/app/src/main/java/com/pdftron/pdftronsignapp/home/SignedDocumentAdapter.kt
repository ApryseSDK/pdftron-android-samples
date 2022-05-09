package com.pdftron.pdftronsignapp.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdftron.pdftronsignapp.R
import com.pdftron.pdftronsignapp.data.DocumentToSign
import kotlinx.android.synthetic.main.document_to_sign_view_holder.view.*
import java.text.SimpleDateFormat

class SignedDocumentAdapter(private val signedDocs: MutableList<DocumentToSign>, val signClicked: (DocumentToSign)->Unit): RecyclerView.Adapter<DocumentToSignViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentToSignViewHolder {
        return DocumentToSignViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DocumentToSignViewHolder, position: Int) {
        val document = signedDocs[position]
        holder.itemView.from_txt.text = document.email
        val sdf = SimpleDateFormat("dd MMM yyyy hh:mm")
        if(document.signedTime != null) {
            val requestedTime = sdf.format(document.signedTime!!.toDate())
            holder.itemView.when_txt.text = requestedTime
        }
        holder.itemView.when_lbl.text = holder.itemView.context.getString(R.string.signed)
        holder.itemView.sign_btn.text = holder.itemView.context.getString(R.string.view)
        holder.itemView.sign_btn.setOnClickListener { signClicked(document) }
    }

    override fun getItemCount() = signedDocs.count()
}