package com.nguyentoan.bepngon.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.model.NotificationModel
import com.nguyentoan.bepngon.model.PostModel
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.model.ReactionModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.sever.FirebaseDatabaseUtil
import com.nguyentoan.bepngon.ui.baidang.DetailBaiDangActivity
import com.nguyentoan.bepngon.ui.personalpage.PersonalPageActivity
import com.nguyentoan.bepngon.ui.personalpage.WithoutPageActivity
import com.nguyentoan.bepngon.util.Constant
import com.nguyentoan.bepngon.util.DataHelper
import com.nguyentoan.bepngon.util.DataUtil
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.view.gone
import com.nguyentoan.bepngon.view.hide
import com.nguyentoan.bepngon.view.openActivity
import com.nguyentoan.bepngon.view.show
import com.squareup.picasso.Picasso

class PostsAdapter(
    var context: Context,
    private var listData: ArrayList<PostModel>,
    val listener: EventClickPostsAdapterListener
) : RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        var imgMore: ImageView = view.findViewById(R.id.imgMore)
        var imgComment: ImageView = view.findViewById(R.id.imgComment)

        var txtName: TextView = view.findViewById(R.id.txtName)
        var txtTime: TextView = view.findViewById(R.id.txtTime)
        var txtContent: TextView = view.findViewById(R.id.txtContent)
        var txtTag: TextView = view.findViewById(R.id.txtTag)
        var numberLike: TextView = view.findViewById(R.id.numberLike)


        var layoutImage: LinearLayout = view.findViewById(R.id.layoutImage)
        var image01: ImageView = view.findViewById(R.id.image01)
        var llImage02: LinearLayout = view.findViewById(R.id.llImage02)
        var image02: ImageView = view.findViewById(R.id.image02)
        var image03: ImageView = view.findViewById(R.id.image03)


        var rlReaction: RelativeLayout = view.findViewById(R.id.rlReaction)
        var imgHeart: ImageView = view.findViewById(R.id.imgHeart)
        var imgHeartFill: ImageView = view.findViewById(R.id.imgHeartFill)


        var viewRoot: LinearLayout = view.findViewById(R.id.viewRoot)


    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_bai_viet, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val post = listData[position]

        viewHolder.txtContent.text = DataUtil.cutTextLong(post.content, 256)

        viewHolder.txtTag.text = DataUtil.cutTextLong(post.tag, 256)

        viewHolder.txtTime.text = DataUtil.showTime(post.create_time)

        if (post.img != "") {
            viewHolder.layoutImage.show()
            Picasso.get().load(post.img).into(viewHolder.image01)
        } else {
            viewHolder.layoutImage.gone()
        }

        FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
            .child(FBConstant.PROFILE).child(post.accountId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val profileModel = result.getValue<ProfileModel>()
                    if (profileModel != null) {
                        viewHolder.txtName.text = profileModel.name
                        Picasso.get().load(profileModel.avt).into(viewHolder.imgAvatar)
                    }
                }
            }

        viewHolder.rlReaction.setOnClickListener {
            if (viewHolder.imgHeart.isVisible) {
                val reactionModel = ReactionModel(
                    DataUtil.getIdByTime(),
                    SharePreferenceUtils.getAccountID(),
                    post.postId,
                    DataUtil.getTime()
                )
                FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
                    .child(FBConstant.REACTION_F).child(
                        reactionModel.reactionId
                ).setValue(reactionModel).addOnSuccessListener {
                        viewHolder.imgHeart.gone()
                        viewHolder.imgHeartFill.show()
                        addNotification(post, "Đã thích một bài viết của bạn.")
                    }

            } else {
                val query2: Query = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
                    .child(FBConstant.REACTION_F)
                    .orderByChild("accountId").equalTo(SharePreferenceUtils.getAccountID())
                query2.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (appleSnapshot in dataSnapshot.children) {
                                val reactionModel = appleSnapshot.getValue<ReactionModel>()
                                if (reactionModel?.postId == post.postId) {
                                    appleSnapshot.ref.removeValue()
                                    viewHolder.imgHeart.show()
                                    viewHolder.imgHeartFill.gone()
                                    return
                                }
                            }

                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
        }

        val query2: Query = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
            .child(FBConstant.REACTION_F)
            .orderByChild("accountId").equalTo(SharePreferenceUtils.getAccountID())
        query2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (appleSnapshot in dataSnapshot.children) {
                        val reactionModel = appleSnapshot.getValue<ReactionModel>()
                        if (reactionModel?.postId == post.postId) {
                            viewHolder.imgHeart.gone()
                            viewHolder.imgHeartFill.show()
                            return
                        }
                    }
                    viewHolder.imgHeart.show()
                    viewHolder.imgHeartFill.gone()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        val reference = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)

        val query: Query =
            reference.child(FBConstant.REACTION_F).orderByChild("postId").equalTo(post.postId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    viewHolder.numberLike.text = String.format("%d", dataSnapshot.childrenCount)
                } else {
                    viewHolder.numberLike.text = "0"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        viewHolder.imgAvatar.setOnClickListener {
            if (post.accountId == SharePreferenceUtils.getAccountID()) {
                context.openActivity(
                    PersonalPageActivity::class.java
                )
            } else {
                context.openActivity(
                    WithoutPageActivity::class.java,
                    bundleOf("idUser" to post.accountId)
                )
            }

        }

        viewHolder.viewRoot.setOnClickListener {
            context.openActivity(
                DetailBaiDangActivity::class.java,
                bundleOf("post_data" to post.toJson())
            )
        }

        viewHolder.imgMore.setOnClickListener {
            listener.clickPost(post, position)
        }
    }

    private fun addNotification(post: PostModel, content : String) {
        if (post.accountId == SharePreferenceUtils.getAccountID())
            return
        val notifi = NotificationModel(
            DataUtil.ConvertToMD5(DataUtil.getTime()),
            post.accountId,
            post.postId,
            DataHelper.profileUser.value?.avt?: "",
            DataHelper.profileUser.value?.name?: "",
            content,
            DataUtil.getTime()
        )
        FirebaseDatabase.getInstance().getReference(FirebaseDatabaseUtil.ROOT)
            .child(FBConstant.NOTI_F).child(post.accountId)
            .child(notifi.create_time)
            .setValue(notifi)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setListData(arr: List<PostModel>) {
        listData.clear()
        listData.addAll(arr)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun addFirstData(arr: PostModel) {
        listData.add(0, arr)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(arr: PostModel) {
        listData.add(arr)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addListData(arr: List<PostModel>) {
        listData.addAll(arr)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeItemAt(position: Int) {
        listData.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount() = listData.size

}

interface EventClickPostsAdapterListener {
    fun clickPost(post: PostModel, position : Int)
}