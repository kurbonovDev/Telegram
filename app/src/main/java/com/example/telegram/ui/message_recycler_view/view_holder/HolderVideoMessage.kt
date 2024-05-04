package com.example.telegram.ui.message_recycler_view.view_holder

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.utilits.asTime

class HolderVideoMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {
    private val blockUserVideoMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_video_message)
    private  val chatUserVideoMessage: VideoView = view.findViewById(R.id.videoUser)
    private val chatUserMessageVideoTime: TextView = view.findViewById(R.id.chat_user_video_message_time)
    private val blockReceivedVideoMessage: ConstraintLayout = view.findViewById(R.id.bloc_received_video_message)
    private val chatReceivedVideoMessage: VideoView = view.findViewById(R.id.videoRecieved)
    private  val chatReceivedVideoMessageTime: TextView = view.findViewById(R.id.chat_received_video_message_time)
    private  val chatReceivedBtnVideoMessageBtn: ImageView = view.findViewById(R.id.chat_recieved_btn_play)
    private  val chatUserVideoMessageBtn: ImageView = view.findViewById(R.id.chat_user_btn_play)



    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blockUserVideoMessage.visibility = View.VISIBLE
            blockReceivedVideoMessage.visibility = View.GONE
            chatUserMessageVideoTime.text = view
                .timeStamp.asTime()



        } else {
            blockUserVideoMessage.visibility = View.GONE
            blockReceivedVideoMessage.visibility = View.VISIBLE
            chatReceivedVideoMessageTime.text = view
                .timeStamp.asTime()
        }
    }

    override fun onAttach(view: MessageView) {
        chatUserVideoMessageBtn.setOnClickListener{
            playVideoUser(view)
            chatUserVideoMessageBtn.visibility=View.GONE
        }

        chatReceivedBtnVideoMessageBtn.setOnClickListener {
            playVideoRecieved(view)
            chatReceivedBtnVideoMessageBtn.visibility=View.GONE
        }
    }

    private fun playVideoRecieved(view: MessageView) {
        chatReceivedVideoMessage.setVideoURI(Uri.parse(view.fileUrl))
        chatReceivedVideoMessage.requestFocus()
        chatReceivedVideoMessage.setOnPreparedListener { mediaPlayer ->

            chatReceivedVideoMessage.start()
        }
        chatReceivedVideoMessage.setOnCompletionListener {
            chatReceivedBtnVideoMessageBtn.visibility=View.VISIBLE
        }
    }

    private fun playVideoUser(view: MessageView) {
        chatUserVideoMessage.setVideoURI(Uri.parse(view.fileUrl))
        chatUserVideoMessage.requestFocus()
        chatUserVideoMessage.setOnPreparedListener { mediaPlayer ->

            chatUserVideoMessage.start()
        }
        chatUserVideoMessage.setOnCompletionListener { chatUserVideoMessageBtn.visibility=View.VISIBLE }


    }

    override fun onDettach() {
    }


}