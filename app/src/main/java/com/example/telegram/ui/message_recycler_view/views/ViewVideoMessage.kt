package com.example.telegram.ui.message_recycler_view.views

data class ViewVideoMessage(
    override val id: String,
    override val from: String,
    override val timeStamp: String,
    override val fileUrl: String,
    override val text: String="",
    override val fullName: String=""
) : MessageView {
    override fun getTypeView(): Int {
        return MessageView.MESSAGE_VIDEO
    }
    override fun equals(other: Any?): Boolean {
        return (other as MessageView).id == id
    }
}