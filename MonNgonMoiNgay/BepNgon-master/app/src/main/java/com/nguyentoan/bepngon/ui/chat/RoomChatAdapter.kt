package com.nguyentoan.bepngon.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.model.CommentModel
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.model.chat.MessageModel
import com.nguyentoan.bepngon.model.chat.RoomChatModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.sever.ProfileFBListener
import com.nguyentoan.bepngon.sever.ProfileFBUtil
import com.nguyentoan.bepngon.util.DataUtil
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.view.setOnSafeClick
import com.squareup.picasso.Picasso

class RoomChatAdapter(
    var context: Context,
    private var listData: ArrayList<RoomChatModel>,
    val listener: RoomChatAdapterListener
) : RecyclerView.Adapter<RoomChatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var imAvatar: ImageView = view.findViewById(R.id.imAvatar)
        var txtName: TextView = view.findViewById(R.id.txtName)
        var txtContent: TextView = view.findViewById(R.id.txtContent)
        var txtTime: TextView = view.findViewById(R.id.txtTime)
        var llView: LinearLayout = view.findViewById(R.id.llView)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_roomchat, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val message = listData[position]

        val accountId = if (message.accountId_1 == SharePreferenceUtils.getAccountID()) {
            message.accountId_2
        } else {
            message.accountId_1
        }

        showInforRoom(viewHolder.imAvatar, viewHolder.txtName, accountId)

        showLastMess(viewHolder.txtContent, viewHolder.txtTime, message.room_id)

        viewHolder.llView.setOnSafeClick {
            listener.click(message)
        }

    }

    private fun showInforRoom(imAvatar: ImageView, txtName: TextView, accountId: String) {
        ProfileFBUtil.getProfile(accountId, object : ProfileFBListener{
            override fun actionSuccess(profileModel: ProfileModel) {
                Picasso.get().load(profileModel.avt).into(imAvatar)
                txtName.text = profileModel.name
            }
            override fun actionFail() {}
        })
    }

    private fun showLastMess(txtContent: TextView, txtTime: TextView, roomId : String) {
        FirebaseDatabase.getInstance().getReference(FBConstant.CHAT_F).child(FBConstant.MESSAGE)
            .child(roomId).limitToLast(1).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (issue in dataSnapshot.children) {
                            issue.getValue<MessageModel>()?.let {
                                if (it.accountId == SharePreferenceUtils.getAccountID()) {
                                    txtContent.text = "Bạn: " + it.content
                                } else {
                                    txtContent.text = it.content
                                }

                                txtTime.text = DataUtil.showTime(it.crete_time)
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setListData(arr : ArrayList<RoomChatModel>) {
        listData.clear()
        listData = arr
        notifyDataSetChanged()
    }

    override fun getItemCount() = listData.size

}

interface RoomChatAdapterListener {
    fun click(roomChatModel : RoomChatModel)
}