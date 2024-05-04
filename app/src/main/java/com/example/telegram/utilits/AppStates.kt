package com.example.telegram.utilits

import com.example.telegram.database.AUTH
import com.example.telegram.database.CHILD_STATE
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.NODE_USER
import com.example.telegram.database.REF_DATABASE_ROOT
import com.example.telegram.database.USER

enum class AppStates (val state:String){
    ONLINE("В сети"),
    OFFLINE("был(а) недавно"),
    TYPING("печатает...");

    companion object{
            fun updateState(appStates: AppStates){

                if(AUTH.currentUser!=null)
                {
                    REF_DATABASE_ROOT.child(NODE_USER).child(CURRENT_UID).child(CHILD_STATE)
                        .setValue(appStates.state)
                        .addOnCompleteListener { USER.state=appStates.state }
                        .addOnFailureListener {
                            showToast(it.message.toString())
                        }
                }

            }
    }

}