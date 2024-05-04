package com.example.telegram.ui.screens

import androidx.fragment.app.Fragment
import com.example.telegram.MainActivity


open class BaseFragment( layout: Int) : Fragment(layout) {


    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mAppDrawer.disableDrawer()
    }


}