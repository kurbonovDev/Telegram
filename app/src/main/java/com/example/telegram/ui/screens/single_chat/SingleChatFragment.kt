package com.example.telegram.ui.screens.single_chat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegram.R
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.NODE_MESSAGES
import com.example.telegram.database.NODE_USER
import com.example.telegram.database.REF_DATABASE_ROOT
import com.example.telegram.database.TYPE_TEXT
import com.example.telegram.database.clearChat
import com.example.telegram.database.deleteChat
import com.example.telegram.database.getCommonModel
import com.example.telegram.database.getMessagKey
import com.example.telegram.database.getUserModel
import com.example.telegram.database.saveToMainList
import com.example.telegram.database.sendMessage
import com.example.telegram.database.uploadFileToStorage
import com.example.telegram.databinding.FragmentSingleChatBinding
import com.example.telegram.models.CommonModel
import com.example.telegram.models.UserModel
import com.example.telegram.ui.message_recycler_view.views.AppViewFactory
import com.example.telegram.ui.screens.BaseFragment
import com.example.telegram.ui.screens.main_list.MainlistFragment
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.utilits.AppChildEventListner
import com.example.telegram.utilits.AppTextWatcher
import com.example.telegram.utilits.AppValueEventListner
import com.example.telegram.utilits.AppVoiceRecorder
import com.example.telegram.utilits.PICK_FILE_REQUEST_CODE
import com.example.telegram.utilits.PICK_VIDEO_REQUEST_CODE
import com.example.telegram.utilits.RECORD_AUDIO
import com.example.telegram.utilits.showToast
import com.example.telegram.utilits.TYPE_CHAT
import com.example.telegram.utilits.TYPE_MESSAGE_FILE
import com.example.telegram.utilits.TYPE_MESSAGE_IMAGE
import com.example.telegram.utilits.TYPE_MESSAGE_VIDEO
import com.example.telegram.utilits.TYPE_MESSAGE_VOICE
import com.example.telegram.utilits.checkPermission
import com.example.telegram.utilits.downloadSetImage
import com.example.telegram.utilits.getFileNameFromUri
import com.example.telegram.utilits.replaceFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerinfoToolbar: AppValueEventListner
    private lateinit var mRecievingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefuser: DatabaseReference
    private lateinit var mBinding: FragmentSingleChatBinding
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListner: AppChildEventListner
    private var mCountMessage = 10
    private var mIsSccroling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mAppVoiceRecorder: AppVoiceRecorder
    private lateinit var mBottomSheetBehavior: BottomSheetBehavior<*>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSingleChatBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecycleView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        setHasOptionsMenu(true)
        mBottomSheetBehavior =
            BottomSheetBehavior.from(requireView().findViewById(R.id.bottom_sheet_choice))
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


        mAppVoiceRecorder = AppVoiceRecorder()
        mSwipeRefreshLayout = mBinding.chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)
        mBinding.chatInputMessage.addTextChangedListener(AppTextWatcher {
            val string = mBinding.chatInputMessage.text.toString()
            if (string.isEmpty() || string == "Запись...") {
                mBinding.chatBtnSendMessage.visibility = View.GONE
                mBinding.chatBtnAttach.visibility = View.VISIBLE
                mBinding.chatBtnVoice.visibility = View.VISIBLE
            } else {
                mBinding.chatBtnSendMessage.visibility = View.VISIBLE
                mBinding.chatBtnAttach.visibility = View.GONE
                mBinding.chatBtnVoice.visibility = View.GONE
            }
        })
        mBinding.chatBtnAttach.setOnClickListener { attach() }

        CoroutineScope(Dispatchers.IO).launch {
            mBinding.chatBtnVoice.setOnTouchListener { v, event ->
                if (checkPermission(RECORD_AUDIO)) {
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        //TODO RECORD
                        mBinding.chatInputMessage.setText("Запись...")
                        mBinding.chatBtnVoice.setColorFilter(
                            ContextCompat.getColor(
                                APP_ACTIVITY,
                                R.color.colorPrimary
                            )
                        )

                        val messageKey = getMessagKey(contact.id)
                        mAppVoiceRecorder.startRecord(messageKey)

                    } else if (event.action == MotionEvent.ACTION_UP) {
                        //TODO stop
                        mBinding.chatInputMessage.setText("")
                        mBinding.chatBtnVoice.colorFilter = null
                        mAppVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileToStorage(
                                Uri.fromFile(file),
                                messageKey,
                                contact.id,
                                TYPE_MESSAGE_VOICE
                            )
                            mSmoothScrollToPosition = true
                        }
                    }
                }
                true
            }
        }
    }

    private fun attach() {
        mBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        val image: ImageView = requireView().findViewById(R.id.btn_attach_image)
        val file: ImageView = requireView().findViewById(R.id.btn_attach_file)
        val video:ImageView=requireView().findViewById(R.id.btn_attach_video)
        image.setOnClickListener { attachImage() }
        file.setOnClickListener { attachFile() }
        video.setOnClickListener { attachVideo() }
    }

    private fun attachVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_VIDEO_REQUEST_CODE)
    }

    private fun attachFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
    }


    private fun attachImage() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start((APP_ACTIVITY), this)
    }

    private fun initRecycleView() {
        mRecyclerView = mBinding.chatRecyclerView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mMessagesListner = AppChildEventListner {
            val message = it.getCommonModel()
            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(AppViewFactory.getView(message)) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(AppViewFactory.getView(message)) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }


        mRefMessages.limitToLast(mCountMessage).addChildEventListener(mMessagesListner)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsSccroling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsSccroling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }
        })
        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsSccroling = false
        mCountMessage += 10
        mRefMessages.removeEventListener(mMessagesListner)
        mRefMessages.limitToLast(mCountMessage).addChildEventListener(mMessagesListner)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.findViewById<ConstraintLayout>(R.id.toolbar_info)
        mToolbarInfo.visibility = View.VISIBLE
        mListenerinfoToolbar = AppValueEventListner {
            mRecievingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefuser = REF_DATABASE_ROOT.child(NODE_USER).child(contact.id)
        mRefuser.addValueEventListener(mListenerinfoToolbar)
        mBinding.chatBtnSendMessage.setOnClickListener {
            mSmoothScrollToPosition = true
            val message = mBinding.chatInputMessage.text.toString()
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessage(message, contact.id, TYPE_TEXT) {
                saveToMainList(contact.id, TYPE_CHAT)
                mBinding.chatInputMessage.setText("")
            }
        }
    }


    private fun initInfoToolbar() {

        if (mRecievingUser.fullname.isEmpty()) {
            mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_fullname).text = contact.fullname
        } else {
            mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_fullname).text =
                mRecievingUser.fullname

        }
        mToolbarInfo.findViewById<ImageView>(R.id.toolbar_chat_image)
            .downloadSetImage(mRecievingUser.photoUrl)
        mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_status).text = mRecievingUser.state
    }

    override fun onStop() {
        super.onStop()
        mToolbarInfo.visibility = View.GONE
        mRefuser.removeEventListener(mListenerinfoToolbar)
        mRefMessages.removeEventListener(mMessagesListner)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            when (requestCode) {
                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val uri = CropImage.getActivityResult(data).uri
                    val messageKey = getMessagKey(contact.id)
                    uploadFileToStorage(uri, messageKey, contact.id, TYPE_MESSAGE_IMAGE)
                    mSmoothScrollToPosition = true
                }

                PICK_FILE_REQUEST_CODE -> {
                    val uri = data.data
                    val messageKey = getMessagKey(contact.id)
                    val fileName = getFileNameFromUri(uri!!)
                    uri.let {
                        uploadFileToStorage(
                            it,
                            messageKey,
                            contact.id,
                            TYPE_MESSAGE_FILE,
                            fileName
                        )
                    }
                    mSmoothScrollToPosition = true
                }
                PICK_VIDEO_REQUEST_CODE->{
                    val uri = data.data
                    val messageKey = getMessagKey(contact.id)
                    val fileName = getFileNameFromUri(uri!!)
                    uri.let {
                        uploadFileToStorage(
                            it,
                            messageKey,
                            contact.id,
                            TYPE_MESSAGE_VIDEO,
                            fileName
                        )
                    }
                    mSmoothScrollToPosition = true

                }
            }

        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        mAppVoiceRecorder.releaseRecorder()
        mAdapter.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.single_chat_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clear_chat->clearChat(contact.id){
                showToast("Чат очищен")
                replaceFragment(MainlistFragment())
            }
            R.id.menu_delete_chat->deleteChat(contact.id){
                showToast("Чат удален")
                replaceFragment(MainlistFragment())
            }
        }
        return true
    }
}