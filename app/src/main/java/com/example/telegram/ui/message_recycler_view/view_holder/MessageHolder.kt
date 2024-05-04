package com.example.telegram.ui.message_recycler_view.view_holder

import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.ui.screens.single_chat.SingleChatFragment

interface MessageHolder  {
    fun drawMessage(view: MessageView)
    fun onAttach(view: MessageView)
    fun onDettach()
}