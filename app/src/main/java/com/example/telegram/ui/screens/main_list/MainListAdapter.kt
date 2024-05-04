package com.example.telegram.ui.screens.main_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.screens.single_chat.SingleChatFragment
import com.example.telegram.utilits.downloadSetImage
import com.example.telegram.utilits.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView

class MainListAdapter : RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    private val listItem = mutableListOf<CommonModel>()

    class MainListHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.findViewById(R.id.main_list_item_fullname)
        val itemLastMessage: TextView = view.findViewById(R.id.main_list_last_message)
        val itemPhoto: CircleImageView = view.findViewById(R.id.main_list_item_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.main_list_item, parent, false)
        val holder=MainListHolder(view)
        holder.itemView.setOnClickListener {
            replaceFragment(SingleChatFragment(listItem[holder.adapterPosition]))
        }
        return holder
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemName.text=listItem[position].fullname
        holder.itemLastMessage.text=listItem[position].lastMessage
        holder.itemPhoto.downloadSetImage(listItem[position].photoUrl)
    }

    fun updateListItems(item:CommonModel){
        listItem.add(item)
        notifyItemInserted(listItem.size)

    }

}