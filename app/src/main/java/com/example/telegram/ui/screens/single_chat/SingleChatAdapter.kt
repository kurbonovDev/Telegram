package com.example.telegram.ui.screens.single_chat

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.ui.message_recycler_view.view_holder.AppHolderFactory
import com.example.telegram.ui.message_recycler_view.view_holder.MessageHolder
import com.example.telegram.ui.message_recycler_view.views.MessageView

class SingleChatAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mlistMessagesCache = mutableListOf<MessageView>()
    private var mListHolders= mutableListOf<MessageHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppHolderFactory.getHolder(parent, viewType)

    }

    override fun getItemCount(): Int = mlistMessagesCache.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MessageHolder).drawMessage(mlistMessagesCache[position])
    }
    override fun getItemViewType(position: Int): Int {
        return mlistMessagesCache[position].getTypeView()
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onAttach(mlistMessagesCache[holder.adapterPosition])
        mListHolders.add((holder as MessageHolder))
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        (holder as MessageHolder).onDettach()
        mListHolders.remove((holder as MessageHolder))
        super.onViewDetachedFromWindow(holder)
    }


    fun addItemToBottom(item: MessageView, onSuccess: () -> Unit) {
        if (!mlistMessagesCache.contains(item)) {
            mlistMessagesCache.add(item)
            notifyItemInserted(mlistMessagesCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(item: MessageView, onSuccess: () -> Unit) {
        if (!mlistMessagesCache.contains(item)) {
            mlistMessagesCache.add(item)
            mlistMessagesCache.sortBy { it.timeStamp }
            notifyItemInserted(0)
        }
        onSuccess()
    }

    fun onDestroy() {
        mListHolders.forEach {
            it.onDettach()
        }
    }


}


