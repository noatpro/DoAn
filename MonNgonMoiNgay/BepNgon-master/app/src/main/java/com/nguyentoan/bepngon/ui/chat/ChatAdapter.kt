package com.nguyentoan.bepngon.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.model.chat.MessageModel
import com.nguyentoan.bepngon.util.DataUtil
import com.nguyentoan.bepngon.util.SharePreferenceUtils

class ChatAdapter(
    context: Context,
    private var listData: ArrayList<MessageModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        return when (viewType) {
            ViewType.TYPE_ONE.type -> {
                val view = inflater.inflate(R.layout.message_send, viewGroup, false)
                TypeOneViewHodel(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.message_received, viewGroup, false)
                TypeTwoViewHodel(view)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        p0.apply {
            when (p0) {
                is TypeOneViewHodel -> p0.bind(listData[p1])
                is TypeTwoViewHodel -> p0.bind(listData[p1])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData[position].accountId == SharePreferenceUtils.getAccountID())
            ViewType.TYPE_ONE.type
        else
            ViewType.TYPE_TWO.type
    }

    inner class TypeOneViewHodel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MessageModel) {
            with(itemView) {
                this.findViewById<TextView>(R.id.txtMsg_send).text = item.content
                this.findViewById<TextView>(R.id.txtTime).text = DataUtil.showTime(item.crete_time)
            }

        }
    }

    inner class TypeTwoViewHodel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: MessageModel) {
            with(itemView) {
                this.findViewById<TextView>(R.id.txtMsg_received).text = item.content
                this.findViewById<TextView>(R.id.txtTime).text = DataUtil.showTime(item.crete_time)
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setListData(arr: ArrayList<MessageModel>) {
        listData = arr
        notifyDataSetChanged()
    }

    override fun getItemCount() = listData.size

}

enum class ViewType(val type: Int) {
    TYPE_ONE(0),
    TYPE_TWO(1)
}