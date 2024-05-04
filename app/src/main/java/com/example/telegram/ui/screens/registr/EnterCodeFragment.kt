package com.example.telegram.ui.screens.registr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.telegram.R
import com.example.telegram.databinding.FragmentEnterCodeBinding
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.database.AUTH
import com.example.telegram.utilits.AppTextWatcher
import com.example.telegram.database.CHILD_ID
import com.example.telegram.database.CHILD_PHONE
import com.example.telegram.database.CHILD_USERNAME
import com.example.telegram.database.NODE_PHONES
import com.example.telegram.database.NODE_USER
import com.example.telegram.database.REF_DATABASE_ROOT
import com.example.telegram.utilits.AppValueEventListner
import com.example.telegram.utilits.showToast
import com.example.telegram.utilits.restartActivity
import com.google.firebase.auth.PhoneAuthProvider


class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    private lateinit var mBinding: FragmentEnterCodeBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEnterCodeBinding.inflate(layoutInflater, container, false)
        return mBinding.root

    }


    override fun onStart() {
        APP_ACTIVITY.title = phoneNumber
        super.onStart()
        mBinding.registrInputCode.addTextChangedListener(AppTextWatcher {
            val string = mBinding.registrInputCode.text.toString()
            if (string.length == 6) {
                enterCode()
            }



        })
    }

    private fun enterCode() {
        /* Функция проверяет код, если все нормально, производит создания информации о пользователе в базе данных.*/
        val code = mBinding.registrInputCode.text.toString()


        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber


                REF_DATABASE_ROOT.child(NODE_USER).child(uid)
                    .addListenerForSingleValueEvent(AppValueEventListner{

                        if (!it.hasChild(CHILD_USERNAME)){
                            dateMap[CHILD_USERNAME] = uid
                        }
                        REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                            .addOnFailureListener {
                                showToast(it.message.toString())
                            }
                            .addOnCompleteListener {
                                REF_DATABASE_ROOT.child(NODE_USER).child(uid).updateChildren(dateMap)
                                    .addOnSuccessListener {
                                        showToast("Добро пожаловать")
                                        restartActivity()
                                    }
                                    .addOnFailureListener {
                                        showToast(it.message.toString())
                                    }
                            }
                    })


            } else showToast(task.exception?.message.toString())
        }
    }





}