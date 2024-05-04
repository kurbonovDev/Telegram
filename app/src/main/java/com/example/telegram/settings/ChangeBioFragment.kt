package com.example.telegram.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegram.R
import com.example.telegram.databinding.FragmentChangeBioBinding
import com.example.telegram.database.USER
import com.example.telegram.database.setBioToDatabase
import com.example.telegram.ui.screens.BaseChangeFragment


class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    private lateinit var mBinding: FragmentChangeBioBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding=FragmentChangeBioBinding.inflate(layoutInflater,container,false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        mBinding.settingsInputBio.setText(USER.bio)

    }

    override fun change() {
        super.change()
        val newBio=mBinding.settingsInputBio.text.toString()
        setBioToDatabase(newBio)


    }


}