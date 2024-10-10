package com.nguyentoan.bepngon.ui.user.tienich.policy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.ActivityChangePasswordBinding
import com.nguyentoan.bepngon.databinding.ActivityPolicyBinding
import com.nguyentoan.bepngon.model.AccountModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast

class PolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}