package com.nguyentoan.bepngon.ui.personalpage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.ActivityDangBaiBinding
import com.nguyentoan.bepngon.databinding.ActivityPersonalPageBinding
import com.nguyentoan.bepngon.databinding.LayoutBottomSheetPostBinding
import com.nguyentoan.bepngon.model.PostModel
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.model.ReactionModel
import com.nguyentoan.bepngon.model.SaveModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.sever.FirebaseDatabaseUtil
import com.nguyentoan.bepngon.sever.ProfileFBListener
import com.nguyentoan.bepngon.sever.ProfileFBUtil
import com.nguyentoan.bepngon.ui.adapter.EventClickPostsAdapterListener
import com.nguyentoan.bepngon.ui.adapter.PostsAdapter
import com.nguyentoan.bepngon.util.AdminHelper
import com.nguyentoan.bepngon.util.DataHelper
import com.nguyentoan.bepngon.util.DataUtil
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.gone
import com.nguyentoan.bepngon.view.openActivity
import com.nguyentoan.bepngon.view.setOnSafeClick
import com.nguyentoan.bepngon.view.show
import com.squareup.picasso.Picasso

class PersonalPageActivity : AppCompatActivity() , SwipeRefreshLayout.OnRefreshListener,
    EventClickPostsAdapterListener {

    lateinit var binding: ActivityPersonalPageBinding
    lateinit var adapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolBar.txtTitle.text = "Trang cá nhân"
        binding.toolBar.imgBack.show()
        binding.toolBar.imgBack.setOnSafeClick { onBackPressed() }

        adapter = PostsAdapter(this@PersonalPageActivity, ArrayList<PostModel>(), this)
        binding.rcyBaiDang.adapter = adapter

        DataHelper.profileUser.observe(this){
            updateUiProfile(it)
            getPostData()
        }

        binding.btnChange.setOnSafeClick {
            openActivity(ProfileActivity::class.java)
        }

        binding.swipLayout.setOnRefreshListener(this)

    }

    private fun getDataUser() {
        ProfileFBUtil.getProfile(SharePreferenceUtils.getAccountID(), object : ProfileFBListener {
            override fun actionSuccess(profileModel: ProfileModel) {
                DataHelper.profileUser.postValue(profileModel)
            }

            override fun actionFail() {

            }
        })
    }


    private val mDatabase = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
    private fun getPostData() {
        mDatabase.child(FBConstant.POST_F).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listData = ArrayList<PostModel>()
                    for (postSnapshot in snapshot.children) {
                        postSnapshot.getValue<PostModel>()?.let {
                            if (it.accountId == SharePreferenceUtils.getAccountID())
                            listData.add(
                                it
                            )
                        }
                    }
                    adapter.setListData(listData)
                }
                binding.swipLayout.isRefreshing = false
            }

            override fun onCancelled(error: DatabaseError) {
                binding.swipLayout.isRefreshing = false
            }
        })
    }

    private fun updateUiProfile(it: ProfileModel) {
        Picasso.get().load(it.avt).into(binding.userAvatar)
        binding.toolBar.txtTitle.text = it.name
        binding.userName.text = it.name
        binding.txtAddress.text = it.address
        binding.txtBirthDay.text = it.birthDay
        binding.txtPhone.text = it.phoneNumber
        binding.txtGmail.text = it.gmail
        binding.txtGender.text = if (it.gender) "Nam" else "Nữ"
    }

    override fun onRefresh() {
        getDataUser()
    }

    override fun clickPost(post : PostModel, position : Int) {
        showBottomSheet(post.accountId == SharePreferenceUtils.getAccountID(), post, position)
    }

    private fun showBottomSheet(boolean: Boolean, post : PostModel, position : Int) {
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
                    adapter.notifyItemRemoved(position)
                }
        }

        bottomSheetBinding.llReport.setOnClickListener {
            moreBottomSheet.dismiss()
            AdminHelper.showDialogReport(
                this@PersonalPageActivity,
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
}