package com.example.telegram.settings

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.example.telegram.R
import com.example.telegram.databinding.FragmentSettingsBinding
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.database.AUTH
import com.example.telegram.utilits.AppStates
import com.example.telegram.database.FOLDER_PROFILE_IMAGE
import com.example.telegram.database.REF_STORAGE_ROOT
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.utilits.showToast
import com.example.telegram.database.USER
import com.example.telegram.utilits.downloadSetImage
import com.example.telegram.database.getUrlFromStorage
import com.example.telegram.database.putFileToStorage
import com.example.telegram.database.putUrlTodatabase
import com.example.telegram.ui.screens.BaseFragment
import com.example.telegram.utilits.replaceFragment
import com.example.telegram.utilits.restartActivity
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var mBinding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentSettingsBinding.inflate(layoutInflater,container,false)
       return mBinding.root
    }


    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

   private fun initFields() {
        mBinding.settingsBio.text = USER.bio
        mBinding.settingFullName.text = USER.fullname
        mBinding.settingsPhoneNumber.text = USER.phone
        mBinding.settingsStatus.text = USER.state
        mBinding.settingsUsername.text = USER.username
        mBinding.settingsBtnChangeUserName.setOnClickListener {
            replaceFragment(ChangeUsernameFragment())
        }
        mBinding.settingsBtnChangeBio.setOnClickListener {
            replaceFragment(ChangeBioFragment())
        }
        mBinding.settingChangePhoto.setOnClickListener {
            changePhotoUser()
        }
        mBinding.settingUserPhoto.downloadSetImage(USER.photoUrl)

    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start((APP_ACTIVITY), this)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity?.menuInflater?.inflate(R.menu.setting_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }

            R.id.setting_menu_change_name -> replaceFragment(ChangeNameFragment())
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)

            putFileToStorage(uri, path)
            {
                getUrlFromStorage(path)
                {
                    putUrlTodatabase(it){
                        mBinding.settingUserPhoto.downloadSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }

        }
    }


}