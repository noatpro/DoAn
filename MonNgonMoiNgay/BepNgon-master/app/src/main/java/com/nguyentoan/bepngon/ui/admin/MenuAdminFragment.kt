package com.nguyentoan.bepngon.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.base.BaseFragment
import com.nguyentoan.bepngon.databinding.FragmentMenuAdminBinding
import com.nguyentoan.bepngon.databinding.FragmentMenuBinding
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.ui.change.ChangePasswordActivity
import com.nguyentoan.bepngon.ui.personalpage.ProfileActivity
import com.nguyentoan.bepngon.ui.sign.SignActivity
import com.nguyentoan.bepngon.util.DataHelper
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.view.openActivity
import com.squareup.picasso.Picasso

class MenuAdminFragment : BaseFragment<FragmentMenuAdminBinding>() {



    override fun initViewCreated() {
        initView()
        DataHelper.profileUser.observe(viewLifecycleOwner){
            updateUI(it)
        }

        initListener()
    }

    private fun initListener() {
        binding.btnLogout.setOnClickListener {
            SharePreferenceUtils.setAccountID(null)
            SharePreferenceUtils.setUserName(null)
            SharePreferenceUtils.setPassword(null)
            requireContext().openActivity(SignActivity::class.java, true)
        }

        binding.llChangePassword.setOnClickListener {
            requireContext().openActivity(ChangePasswordActivity::class.java)
        }

        binding.llChangeProfile.setOnClickListener {
            requireContext().openActivity(ProfileActivity::class.java)
        }
    }

    private fun updateUI(profileModel: ProfileModel) {
        profileModel.let {
            binding.userName.text = profileModel.name
            Picasso.get().load(profileModel.avt).into(binding.userAvatar)
        }
    }

    private fun initView() {
        binding.toolBar.txtTitle.text = "Menu"
    }

    override fun inflateLayout(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMenuAdminBinding {
        return FragmentMenuAdminBinding.inflate(inflater)
    }
}