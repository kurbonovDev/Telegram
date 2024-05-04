package com.example.telegram.ui.message_recycler_view.view_holder

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.getFileFromStorage
import com.example.telegram.ui.message_recycler_view.views.MessageView
import com.example.telegram.utilits.showToast
import com.example.telegram.utilits.WRITE_FILES
import com.example.telegram.utilits.asTime
import com.example.telegram.utilits.checkPermission
import java.io.File

class HolderFileMessage(view: View) : RecyclerView.ViewHolder(view), MessageHolder {



    private val blocReceivedfileMessage: ConstraintLayout =
        view.findViewById(R.id.bloc_received_file_message)
    private   val blocUserfileMessage: ConstraintLayout = view.findViewById(R.id.bloc_user_file_message)

    private val chatUserfileMessageTime: TextView =
        view.findViewById(R.id.chat_user_file_message_time)
    private val chatReceivedfileMessageTime: TextView =
        view.findViewById(R.id.chat_received_file_message_time)

    private val chatUserFilename:TextView = view.findViewById(R.id.chat_user_filename)
    private val chatUserBtnDownload: ImageView = view.findViewById(R.id.chat_user_btn_download)
    private val chatUserProgressBar: ProgressBar = view.findViewById(R.id.chat_user_progress_bar)

    private val chatReceivedFilename:TextView = view.findViewById(R.id.chat_received_filename)
    private val chatReceivedBtnDownload:ImageView = view.findViewById(R.id.chat_received_btn_download)
    private val chatReceivedProgressBar:ProgressBar = view.findViewById(R.id.chat_received_progress_bar)



    override fun drawMessage(view: MessageView) {
        if (view.from == CURRENT_UID) {
            blocUserfileMessage.visibility = View.VISIBLE
            blocReceivedfileMessage.visibility = View.GONE
            chatUserfileMessageTime.text = view
                .timeStamp.asTime()
            chatUserFilename.text=view.text


        } else {
            blocUserfileMessage.visibility = View.GONE
            blocReceivedfileMessage.visibility = View.VISIBLE
            chatReceivedfileMessageTime.text = view
                .timeStamp.asTime()
            chatReceivedFilename.text=view.text
        }
    }

    override fun onAttach(view: MessageView) {
        if (view.from == CURRENT_UID){
            chatUserBtnDownload.setOnClickListener { clickToBtnFile(view) }
        }
        else chatReceivedBtnDownload.setOnClickListener {clickToBtnFile(view) }
    }

    private fun clickToBtnFile(view: MessageView) {
        if (view.from == CURRENT_UID){
            chatUserBtnDownload.visibility = View.INVISIBLE
            chatUserProgressBar.visibility = View.VISIBLE
        } else {
            chatReceivedBtnDownload.visibility = View.INVISIBLE
            chatReceivedProgressBar.visibility = View.VISIBLE
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            view.text
        )

        try {
            if (checkPermission(WRITE_FILES)){
                file.createNewFile()
                getFileFromStorage(file,view.fileUrl){
                    if (view.from == CURRENT_UID){
                        chatUserBtnDownload.visibility = View.VISIBLE
                        chatUserProgressBar.visibility = View.INVISIBLE
                    } else {
                        chatReceivedBtnDownload.visibility = View.VISIBLE
                        chatReceivedProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }catch (e:Exception){
            showToast(e.message.toString())
        }
    }


    override fun onDettach() {
        chatUserBtnDownload.setOnClickListener(null)
        chatReceivedBtnDownload.setOnClickListener(null)
    }
}