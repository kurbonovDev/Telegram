package com.example.telegram.ui.message_recycler_view.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.ui.screens.single_chat.PhotoFullScreenFragment
import com.example.telegram.ui.screens.single_chat.SingleChatFragment
import com.example.telegram.utilits.asTime
import com.example.telegram.utilits.downloadSetImage
import com.example.telegram.utilits.replaceFragment

class HolderImageMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blocReceivedImageMessage: ConstraintLayout =
        view.findViewById(R.id.bloc_received_image_message)
    private val blocUserImageMessage: ConstraintLayout =
        view.findViewById(R.id.bloc_user_image_message)
    private val chatUserImage: ImageView = view.findViewById(R.id.chat_user_image)
    private val chatReceivedImage: ImageView = view.findViewById(R.id.chat_recieved_image)
    private val chatUserImageMessageTime: TextView =
        view.findViewById(R.id.chat_user_image_message_time)
    private val chatReceivedImageMessageTime: TextView =
        view.findViewById(R.id.chat_received_image_message_time)


    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocUserImageMessage.visibility = View.VISIBLE
            blocReceivedImageMessage.visibility = View.GONE
            chatUserImage.downloadSetImage(view.fileUrl)
            chatUserImageMessageTime.text = view
                .timeStamp.asTime()
            chatUserImage.setOnClickListener {
                replaceFragment(
                    PhotoFullScreenFragment(
                        view.fileUrl,
                        view.fullName
                    )
                )
            }

        } else {
            blocUserImageMessage.visibility = View.GONE
            blocReceivedImageMessage.visibility = View.VISIBLE
            chatReceivedImage.downloadSetImage(view.fileUrl)
            chatReceivedImageMessageTime.text = view
                .timeStamp.asTime()
            chatReceivedImage.setOnClickListener {
                replaceFragment(
                    PhotoFullScreenFragment(
                        view.fileUrl,
                        view.fullName
                    )
                )
            }

        }
    }

    override fun onAttach(view: MessageView) {
    }

    override fun onDettach() {
    }
}