package com.example.telegram.utilits

import androidx.recyclerview.widget.DiffUtil
import com.example.telegram.models.CommonModel

class DiffUtilCallBack(
    private val oldlist: List<CommonModel>,
    private val newList: List<CommonModel>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int=oldlist.size

    override fun getNewListSize(): Int=newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldlist[oldItemPosition].timeStamp==newList[newItemPosition].timeStamp


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldlist[oldItemPosition]==newList[newItemPosition]

}