package com.pdftron.pdftronsignapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pdftron.pdftronsignapp.data.User
import kotlinx.android.synthetic.main.bottom_bar_view_holder.view.*

class BottomBarAdapter( private val users: List<User>, val userSelected:(user:User)->Unit): RecyclerView.Adapter<BottomBarViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomBarViewHolder {
        return BottomBarViewHolder.from(parent)
    }

    private var selectedUserPosition = 0

    override fun onBindViewHolder(holder: BottomBarViewHolder, position: Int) {
        val user = users[position]
        holder.itemView.user_txt.text = user.displayName
        if(position == selectedUserPosition){
            holder.itemView.main_layout.setBackgroundColor(Color.BLUE)
            holder.itemView.user_txt.setTextColor(Color.WHITE)
        }
        else{
            holder.itemView.main_layout.setBackgroundColor(Color.WHITE)
            holder.itemView.user_txt.setTextColor(Color.GRAY)
        }
        holder.itemView.setOnClickListener {
            val oldSelectedUser = selectedUserPosition
            selectedUserPosition = position
            notifyItemChanged(oldSelectedUser)
            notifyItemChanged(selectedUserPosition)
            userSelected(user)
        }
    }

    override fun getItemCount() = users.count()
}

class BottomBarViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    companion object {
        fun from(parent: ViewGroup): BottomBarViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.bottom_bar_view_holder, parent, false)
            return BottomBarViewHolder(view)
        }
    }
}