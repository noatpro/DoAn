package com.nguyentoan.bepngon.ui.sign.logout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.FragmentLogUpBinding
import com.nguyentoan.bepngon.sever.AccountFBUtil
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.setOnSafeClick


class LogUpFragment : Fragment() {

    lateinit var binding : FragmentLogUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLogUpBinding.inflate(inflater, container, false)

        initListener()

        return binding.root
    }

    private fun initListener() {
        binding.llSignup.setOnSafeClick {
            if (binding.edtTaiKhoan.text.toString().trim().isEmpty()){
                requireContext().showToast("Tài khoản trống!")
            } else if (binding.edtMatKhau.text.toString().trim().isEmpty()){
                requireContext().showToast("Mật khẩu khoản trống!")
            } else if (binding.edtXacNhan.text.toString().trim().isEmpty()){
                requireContext().showToast("Xác nhận mật khẩu khoản trống!")
            } else if (binding.edtXacNhan.text.toString().trim() != binding.edtMatKhau.text.toString().trim()){
                requireContext().showToast("Xác nhận mật khẩu không đúng!")
            } else {
                AccountFBUtil.logUp(
                    requireContext(),
                    binding.edtTaiKhoan.text.toString().trim(),
                    binding.edtMatKhau.text.toString().trim()
                ) {
                    //requireContext().openActivity(MainActivity::class.java)
                    findNavController().navigate(R.id.action_logUpFragment_to_createInfoFragment)
                }
            }
        }

        binding.txtSignIn.setOnSafeClick {
            findNavController().popBackStack()
        }
    }
}