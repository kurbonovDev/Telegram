package com.example.telegram.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.telegram.R
import com.example.telegram.databinding.FragmentChangeUsernameBinding
import com.example.telegram.utilits.AppValueEventListner
import com.example.telegram.database.NODE_USER_NAME
import com.example.telegram.database.REF_DATABASE_ROOT
import com.example.telegram.utilits.showToast
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.USER
import com.example.telegram.database.updateCurrentusername
import com.example.telegram.ui.screens.BaseChangeFragment
import java.util.Locale


class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    private lateinit var mBinding: FragmentChangeUsernameBinding
    lateinit var mNewusername:String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding=FragmentChangeUsernameBinding.inflate(layoutInflater,container,false)
        return mBinding.root
    }


    override fun onResume() {
        super.onResume()
        mBinding.settingsInputUsername.setText(USER.username)
    }



    override fun change() {
        mNewusername=mBinding.settingsInputUsername.text.toString().toLowerCase(Locale.getDefault())
        if(mNewusername.isEmpty())
        {
            showToast("Поле пустое")
        }else{
            REF_DATABASE_ROOT.child(NODE_USER_NAME).addListenerForSingleValueEvent(AppValueEventListner{
                if (it.hasChild(mNewusername))
                {
                    showToast("Такой пользователь уже существуеть")
                }else
                {
                    changeUsername()
                }
            })

        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USER_NAME).child(mNewusername).setValue(CURRENT_UID)
            .addOnCompleteListener {
                if (it.isSuccessful)
                {
                    updateCurrentusername(mNewusername)
                }
            }
    }




}