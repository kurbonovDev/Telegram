package com.example.telegram.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.databinding.FragmentContactsBinding
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.screens.single_chat.SingleChatFragment
import com.example.telegram.utilits.APP_ACTIVITY
import com.example.telegram.utilits.AppValueEventListner
import com.example.telegram.database.CURRENT_UID
import com.example.telegram.database.NODE_PHONES_CONTACTS
import com.example.telegram.database.NODE_USER
import com.example.telegram.database.REF_DATABASE_ROOT
import com.example.telegram.utilits.downloadSetImage
import com.example.telegram.database.getCommonModel
import com.example.telegram.utilits.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView


class  ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private lateinit var mBinding: FragmentContactsBinding
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUserListener:AppValueEventListner
    private  var mapListeners= hashMapOf<DatabaseReference,AppValueEventListner>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mBinding = FragmentContactsBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Контакты"
        initRecyclerView()
    }

    override fun onStop() {
        super.onStop()
        APP_ACTIVITY.title = "Telegram"
    }

    private fun initRecyclerView() {
        mRecyclerView = mBinding.contactsRecycleView
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USER).child(model.id)
                mRefUserListener=AppValueEventListner{
                    val contact = it.getCommonModel()
                    if(contact.fullname.isEmpty())
                    {
                        holder.name.text=model.fullname
                    }else{
                        holder.name.text = contact.fullname
                    }

                    holder.status.text = contact.state
                    holder.photo.downloadSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener { replaceFragment(SingleChatFragment(model))}
                }
               mRefUsers.addValueEventListener(mRefUserListener)
                mapListeners[mRefUsers]=mRefUserListener


            }

        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contact_fullname)
        val status: TextView = view.findViewById(R.id.contact_status)
        val photo: CircleImageView = view.findViewById(R.id.contact_photo)
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()

        mapListeners.forEach{
            it.key.removeEventListener(it.value)
        }

    }


}

