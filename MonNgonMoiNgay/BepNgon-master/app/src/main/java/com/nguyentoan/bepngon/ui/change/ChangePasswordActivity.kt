package com.nguyentoan.bepngon.ui.change

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.FirebaseDatabase
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.ActivityChangePasswordBinding
import com.nguyentoan.bepngon.databinding.ActivityProfileBinding
import com.nguyentoan.bepngon.model.AccountModel
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }

        binding.txtChangePassword.setOnClickListener {
            if (checkNull()) {
                showToast("Dữ liệu không thể bỏ trống!")
            } else if (checkConfirm()) {
                showToast("Xác nhận mật khẩu không giống!")
            } else if (checkOldPass()) {
                showToast("Mật khẩu cũ không giống!")
            } else {

                val account = AccountModel(
                    SharePreferenceUtils.getAccountID(),
                    SharePreferenceUtils.getUserName(),
                    binding.edtConfirmNewPass.text.toString().trim(),
                    SharePreferenceUtils.getRole(),
                    false
                )

                FirebaseDatabase.getInstance().getReference(FBConstant.ROOT).child(FBConstant.ACCOUNT)
                    .child(SharePreferenceUtils.getAccountID()).setValue(account)

                showToast("Đổi thành công!")

                SharePreferenceUtils.setPassword(account.password)
                onBackPressed()
            }
        }
    }

    private fun checkNull(): Boolean {
        if (binding.edtOldPass.text.toString().trim().isEmpty()) {
            return true
        }
        if (binding.edtNewPass.text.toString().trim().isEmpty()) {
            return true
        }
        if (binding.edtConfirmNewPass.text.toString().trim().isEmpty()) {
            return true
        }
        return false
    }

    private fun checkConfirm(): Boolean {
        if (binding.edtConfirmNewPass.text.toString() == binding.edtNewPass.text.toString()) {
            return false
        }
        return true
    }

    private fun checkOldPass(): Boolean {
        if (binding.edtOldPass.text.toString() == SharePreferenceUtils.getPassword()) {
            return false
        }
        return true
    }
}