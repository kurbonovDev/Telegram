package com.example.telegram.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegram.R
import com.example.telegram.databinding.FragmentChangeNameBinding
import com.example.telegram.utilits.showToast
import com.example.telegram.database.USER
import com.example.telegram.database.setNameToDatabase
import com.example.telegram.ui.screens.BaseChangeFragment

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    private lateinit var mBinding: FragmentChangeNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding=FragmentChangeNameBinding.inflate(layoutInflater,container,false)
        return mBinding.root
    }


    override fun onResume() {
        super.onResume()
        initFullname()
    }

    private fun initFullname() {
        val fullnameList = USER.fullname.split(" ")
        if (fullnameList.size > 1) {
            mBinding.settingsInputName.setText(fullnameList[0])
            mBinding.settingsInputSurname.setText(fullnameList[1])
        } else
            mBinding.settingsInputName.setText(fullnameList[0])
    }

    override fun change() {
        val name=mBinding.settingsInputName.text.toString()
        val surname=mBinding.settingsInputSurname.text.toString()
        if(name.isEmpty())
        {
            showToast(getString(R.string.settings_toast_name_isEmpty))
        }else
        {
            val fullname="$name $surname"
            setNameToDatabase(fullname)

        }
    }



}