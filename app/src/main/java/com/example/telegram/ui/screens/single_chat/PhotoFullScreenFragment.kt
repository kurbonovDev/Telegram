package com.example.telegram.ui.screens.single_chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegram.R
import com.example.telegram.databinding.FragmentPhotoFullScreenBinding
import com.example.telegram.ui.screens.BaseFragment
import com.example.telegram.utilits.downloadSetImage


class PhotoFullScreenFragment(val fileUrl: String,val fullname:String) : BaseFragment(R.layout.fragment_photo_full_screen) {

    private lateinit var mBinding: FragmentPhotoFullScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding=FragmentPhotoFullScreenBinding.inflate(layoutInflater,container,false)
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        mBinding.imageFullScreen.downloadSetImage(fileUrl)
    }

}