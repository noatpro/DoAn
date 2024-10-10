package com.nguyentoan.bepngon.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.model.AccountModel
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.squareup.picasso.Picasso

class FriendAdapter(
    var context: Context,
    private var listData: ArrayList<String>,
    private val listener: EventClickFriendAdapterListener
) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var imgMore: ImageView = view.findViewById(R.id.imgMore)
        var txtName: TextView = view.findViewById(R.id.txtName)
        var txtStatus: TextView = view.findViewById(R.id.txtStatus)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_friend, viewGroup, false)

        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val friend = listData[position]


        getDataProfile(friend, viewHolder.imgAvatar, viewHolder.txtName, viewHolder.txtStatus)

        viewHolder.imgMore.setOnClickListener {
            listener.clickMoreFriend(friend)
        }

        viewHolder.imgAvatar.setOnClickListener {
            listener.clickAvatarFriend(friend)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getDataProfile(accountID: String, imgAvatar : ImageView, txtName : TextView, txtStatus : TextView) {
        FirebaseDatabase.getInstance().getReference(FBConstant.ROOT).child(FBConstant.PROFILE).child(accountID).get().addOnCompleteListener{ task->
            if (task.isSuccessful) {
                val result = task.result
                val post = result.getValue<ProfileModel>()
                post?.let {
                    Picasso.get().load(it.avt).into(imgAvatar)

                    txtName.text = it.name
                }
            }

        }.addOnFailureListener {

        }

        FirebaseDatabase.getInstance().getReference(FBConstant.ROOT).child(FBConstant.ACCOUNT).child(accountID).get().addOnCompleteListener{ task->
            if (task.isSuccessful) {
                val result = task.result
                val post = result.getValue<AccountModel>()
                post?.let {
                    if (it.status) {
                        txtStatus.text = "Đã khóa"
                        txtStatus.setTextColor(context.getColor(R.color.khoa_khoa))
                    } else {
                        txtStatus.text = "Hoạt động"
                        txtStatus.setTextColor(context.getColor(R.color.hoatdong_khoa))
                    }
                }
            }

        }.addOnFailureListener {

        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setListData(arr : ArrayList<String>) {
        listData.clear()
        listData.addAll(arr)
        notifyDataSetChanged()
    }

    override fun getItemCount() = listData.size

}

interface EventClickFriendAdapterListener {
    fun clickMoreFriend(accountID : String)
    fun clickAvatarFriend(accountID : String)
}