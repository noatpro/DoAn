package com.nguyentoan.bepngon.ui.sign.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.FragmentLoginBinding
import com.nguyentoan.bepngon.ui.user.main.MainActivity
import com.nguyentoan.bepngon.sever.AccountFBUtil
import com.nguyentoan.bepngon.ui.admin.MainAdminActivity
import com.nguyentoan.bepngon.ui.user.main.menu.MenuFragment
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.openActivity
import com.nguyentoan.bepngon.view.setOnSafeClick

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        initListener()

        return binding.root
    }

    private fun initListener() {
        binding.llSignIn.setOnSafeClick {
            if (binding.edtTaiKhoan.text.toString().trim().isEmpty()) {
                requireContext().showToast("Tài khoản trống!")
            } else if (binding.edtMatKhau.text.toString().trim().isEmpty()) {
                requireContext().showToast("Mật khẩu khoản trống!")
            } else {
                AccountFBUtil.logIn(
                    requireContext(),
                    binding.edtTaiKhoan.text.toString().trim(),
                    binding.edtMatKhau.text.toString().trim(), {
                        requireContext().openActivity(MainActivity::class.java, true)
                    }, {
                        requireContext().openActivity(MainAdminActivity::class.java, true)
                    })
            }
        }

        binding.txtSignUp.setOnSafeClick {
            findNavController().navigate(R.id.action_loginFragment_to_logUpFragment)
        }

        binding.txtHelp.setOnClickListener {
            support(requireContext())
        }

    }

    private fun support(context: Context) {
        val mailIntent = Intent(Intent.ACTION_VIEW)
        val data =
            Uri.parse("mailto:?SUBJECT=${MenuFragment.FeedBack}&body=&to=${MenuFragment.EMAIL}")
        mailIntent.data = data
        context.startActivity(Intent.createChooser(mailIntent, "Gửi mail..."))
    }

}