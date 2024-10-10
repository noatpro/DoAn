package com.nguyentoan.bepngon.ui.baidang

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.ActivityDetailBaiDangBinding
import com.nguyentoan.bepngon.databinding.LayoutBottomSheetPostBinding
import com.nguyentoan.bepngon.model.CommentModel
import com.nguyentoan.bepngon.model.NotificationModel
import com.nguyentoan.bepngon.model.PostModel
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.model.ReactionModel
import com.nguyentoan.bepngon.model.ReportModel
import com.nguyentoan.bepngon.model.SaveModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.sever.FirebaseDatabaseUtil
import com.nguyentoan.bepngon.ui.adapter.CommentAdapter
import com.nguyentoan.bepngon.ui.adapter.EventClickCommentListener
import com.nguyentoan.bepngon.ui.personalpage.PersonalPageActivity
import com.nguyentoan.bepngon.ui.personalpage.WithoutPageActivity
import com.nguyentoan.bepngon.util.AdminHelper
import com.nguyentoan.bepngon.util.DataHelper
import com.nguyentoan.bepngon.util.DataUtil
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.gone
import com.nguyentoan.bepngon.view.openActivity
import com.nguyentoan.bepngon.view.show
import com.squareup.picasso.Picasso


class DetailBaiDangActivity : AppCompatActivity() {

    lateinit var binding : ActivityDetailBaiDangBinding

    private var postModel : PostModel? = null
    private var reportModel : ReportModel? = null

    lateinit var adapterComment : CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBaiDangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postModel = PostModel.toPostModel(intent.getStringExtra("post_data").toString())
        reportModel = ReportModel.toReportModel(intent.getStringExtra("report_data").toString())

        adapterComment = CommentAdapter(this@DetailBaiDangActivity, ArrayList(), object : EventClickCommentListener{
            override fun click(commentModel: CommentModel) {

            }
        })

        postModel?.let {
            getPost(it)
            getComment(it)
            listerComment(it)
        }

        reportModel?.let { noti ->
            binding.lineReport.show()
            binding.llReport.show()
            Picasso.get().load(noti.img).into(binding.imgAvatarReport)

            binding.txtNameReport.text = noti.name
            binding.txtContentReport.text = "Đã báo cáo bài viết: "+noti.content
            binding.txtTimeReport.text = DataUtil.showTime(noti.create_time)
        }

        binding.rcyComment.adapter = adapterComment

        binding.image01.setOnClickListener {
            openActivity(ShowAnhActivity::class.java,

            bundleOf("link" to postModel?.let {it.img} ))
        }


        initListener()


        if (SharePreferenceUtils.isAdmin()) {
            binding.llComment.gone()
        } else {
            binding.llComment.show()
        }
    }

    private fun initListener() {
        binding.imgBack.setOnClickListener { finish() }
        binding.imgAvatar.setOnClickListener {
            if (postModel?.accountId == SharePreferenceUtils.getAccountID()) {
                openActivity(
                    PersonalPageActivity::class.java
                )
            } else {
                openActivity(WithoutPageActivity::class.java, bundleOf("idUser" to postModel?.accountId))
            }
        }

        binding.imgSendComment.setOnClickListener {
            postComment()
        }

        binding.imgMore.setOnClickListener {
            postModel?.let { post ->
                showBottomSheet(post.accountId == SharePreferenceUtils.getAccountID(), post)
            }
        }

    }

    private fun showBottomSheet(boolean: Boolean, post : PostModel) {
        val bottomSheetBinding = LayoutBottomSheetPostBinding.inflate(layoutInflater)
        val moreBottomSheet =
            BottomSheetDialog(this)
        moreBottomSheet.setContentView(bottomSheetBinding.root)

        if (boolean) {
            bottomSheetBinding.llDelete.show()
            bottomSheetBinding.llReport.gone()
        } else {
            bottomSheetBinding.llDelete.gone()
            bottomSheetBinding.llReport.show()
        }

        if (SharePreferenceUtils.isAdmin()) {
            bottomSheetBinding.llDelete.show()
            bottomSheetBinding.llReport.gone()
            bottomSheetBinding.llSave.gone()
        }

        val querySave: Query = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
            .child(FBConstant.SAVE_F)
            .orderByChild("accountId").equalTo(SharePreferenceUtils.getAccountID())
        querySave.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (appleSnapshot in dataSnapshot.children) {
                        val reactionModel = appleSnapshot.getValue<ReactionModel>()
                        if (reactionModel?.postId == post.postId) {
                            bottomSheetBinding.llSave.gone()
                            bottomSheetBinding.llSaved.show()
                            return
                        }
                    }
                    bottomSheetBinding.llSave.show()
                    bottomSheetBinding.llSaved.gone()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                bottomSheetBinding.llSave.show()
                bottomSheetBinding.llSaved.gone()
            }
        })

        bottomSheetBinding.llDelete.setOnClickListener {
            FirebaseDatabase.getInstance().getReference(FirebaseDatabaseUtil.ROOT)
                .child(FBConstant.POST_F).child(post.postId).removeValue().addOnSuccessListener {
                    moreBottomSheet.dismiss()
                    onBackPressed()
                }
        }

        bottomSheetBinding.llReport.setOnClickListener {
            moreBottomSheet.dismiss()
            AdminHelper.showDialogReport(
                this,
                post.postId
            )
        }

        bottomSheetBinding.llSave.setOnClickListener {
            val saveModel = SaveModel(
                DataUtil.ConvertToMD5(DataUtil.getTime()),
                SharePreferenceUtils.getAccountID(),
                post.postId,
                DataUtil.getTime()
            )
            FirebaseDatabase.getInstance().getReference(FirebaseDatabaseUtil.ROOT)
                .child(FBConstant.SAVE_F)
                .child(saveModel.saveId).setValue(saveModel).addOnSuccessListener {
                    moreBottomSheet.dismiss()
                }
        }

        bottomSheetBinding.llSaved.setOnClickListener {
            val query2: Query = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
                .child(FBConstant.SAVE_F)
                .orderByChild("accountId").equalTo(SharePreferenceUtils.getAccountID())
            query2.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (appleSnapshot in dataSnapshot.children) {
                            val reactionModel = appleSnapshot.getValue<ReactionModel>()
                            if (reactionModel?.postId == post.postId) {
                                appleSnapshot.ref.removeValue()
                                moreBottomSheet.dismiss()
                                return
                            }
                        }

                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

        moreBottomSheet.show()
    }

    private fun getPost(postData : PostModel) {
        binding.txtContent.text = postData.content
        binding.txtTime.text = DataUtil.showTime(postData.create_time)
        binding.txtTag.text = postData.tag

        if (postData.img != ""){
            binding.layoutImage.show()
            Picasso.get().load(postData.img).into(binding.image01)
        }

        FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
            .child(FBConstant.PROFILE).child(postData.accountId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    val profileModel = result.getValue<ProfileModel>()
                    if (profileModel != null) {
                        binding.txtName.text = profileModel.name
                        Picasso.get().load(profileModel.avt).into(binding.imgAvatar)
                    }
                }
            }
    }

    private fun listerComment(post : PostModel) {
        val reference = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)

        val query: Query = reference.child(FBConstant.COMMENT_F).orderByChild("postId").equalTo(post.postId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val listData = ArrayList<CommentModel>()
                    for (issue in dataSnapshot.children) {
                        issue.getValue<CommentModel>()?.let {
                            listData.add(
                                it
                            )
                        }
                    }

                    adapterComment.setListData(listData)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getComment(post : PostModel) {
        binding.rlReaction.setOnClickListener {
            if (binding.imgHeart.isVisible) {
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
                        binding.imgHeart.gone()
                        binding.imgHeartFill.show()
                        addNotification(postModel!!, "Đã thích một bài viết của bạn.")
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
                                    binding.imgHeart.show()
                                    binding.imgHeartFill.gone()
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
                            binding.imgHeart.gone()
                            binding.imgHeartFill.show()
                            return
                        }
                    }
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
                    binding.numberLike.text = String.format("%d", dataSnapshot.childrenCount)
                } else {
                    binding.numberLike.text = "0"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
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

    private fun postComment() {
        if (binding.edtComment.text.toString().trim().isNotEmpty()) {
            if (DataUtil.checkSpam(binding.edtComment.text.toString().trim())) {
                showToast("Nội dụng bình luận vi phạm chính sách!")
            } else {
                insertComment(
                    CommentModel(
                        DataUtil.getTime(),
                        SharePreferenceUtils.getAccountID(),
                        postModel!!.postId,
                        binding.edtComment.text.toString(),
                        DataUtil.getTime()
                    )
                )
                binding.edtComment.setText("")
                addNotification(postModel!!, "Đã bình luận một bài viết của bạn.")
            }

        }
    }

    fun insertComment(commentModel: CommentModel) {
        val mDatabase = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
        mDatabase.child(FBConstant.COMMENT_F).child(
            commentModel.commentId
        ).setValue(commentModel)
    }

}