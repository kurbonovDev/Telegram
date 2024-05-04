package com.example.telegram.ui.screens.registr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.telegram.R
import com.example.telegram.databinding.FragmentEnterPhoneNumberBinding
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.database.AUTH
import com.example.telegram.utilits.showToast
import com.example.telegram.utilits.replaceFragment
import com.example.telegram.utilits.restartActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit


class EnterPhoneNumberFragment : Fragment(R.layout.fragment_enter_phone_number) {

   private lateinit var mPhoneNumber:String
    private lateinit var mCallback:PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var mBinding: FragmentEnterPhoneNumberBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding=FragmentEnterPhoneNumberBinding.inflate(layoutInflater,container,false)
        return mBinding.root
    }




    override fun onStart() {
        super.onStart()
        /* Callback который возвращает результат верификации */
        mCallback=object:PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        {
            /* Функция срабатывает если верификация уже была произведена,
                * пользователь авторизируется в приложении без потверждения по смс */
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                AUTH.signInWithCredential(credential).addOnCompleteListener{ task->
                    if(task.isSuccessful)
                    {
                        showToast("Добро пожаловать")
                      restartActivity()

                    }else showToast(task.exception?.message.toString())
                }
            }
            /* Функция срабатывает если верификация не удалась*/
            override fun onVerificationFailed(p0: FirebaseException) {
                showToast(p0.message.toString())
            }
            /* Функция срабатывает если верификация впервые, и отправлена смс */
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                    replaceFragment(EnterCodeFragment(mPhoneNumber,id))
            }
        }
        mBinding.registrBtnNext.setOnClickListener {
            sendCode()

        }
    }

    private fun sendCode() {
        /* Функция проверяет поле для ввода номер телефона, если поле пустое выводит сообщение.
        * Если поле не пустое, то начинает процедуру авторизации/ регистрации */
        if (mBinding.registrInputPhoneNumber.text.toString().isEmpty()) {
            showToast(getString(R.string.registr_toast_enter_phone))

        } else {
             authUser()
        }
    }

    private fun authUser() {
        mPhoneNumber="+992"+mBinding.registrInputPhoneNumber.text.toString()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            mPhoneNumber,
            60,
            TimeUnit.SECONDS,
           APP_ACTIVITY,
            mCallback

        )
    }

}