package com.nguyentoan.bepngon.ui.user.tienich.chedoan

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.nguyentoan.bepngon.databinding.FragmentShowCheDoAnBinding
import com.nguyentoan.bepngon.view.show

class ShowCheDoAnFragment : Fragment() {
    lateinit var binding : FragmentShowCheDoAnBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentShowCheDoAnBinding.inflate(inflater, container, false)

        initData()

        initListener()

        return binding.root
    }

    private fun initData() {
        val answer = requireArguments().getInt("data")

        when (answer) {
            1 -> {
                binding.txtFullName.text = "Trẻ em"
                binding.llTreEm.show()
            }
            2 -> {
                binding.txtFullName.text = "Người lớn"
                binding.llNguoiLon.show()
            }
            3 -> {
                binding.txtFullName.text = "Người già"
                binding.llNguoiGia.show()
            }
            4 -> {
                binding.txtFullName.text = "Bệnh tim mạch"
                binding.llTimMach.show()
            }
            5 -> {
                binding.txtFullName.text = "Bệnh tiểu đường"
                binding.llTieuDuong.show()
            }
            6 -> {
                binding.txtFullName.text = "Bệnh huyết áp cao"
                binding.llHuyetApCao.show()
            }
            7 -> {
                binding.txtFullName.text = "Bệnh huyết áp thấp"
                binding.llHuyetApThap.show()
            }
            8 -> {
                binding.txtFullName.text = "Bệnh máu nhiểm mỡ"
                binding.llMauNhiemMo.show()
            }
            9 -> {
                binding.txtFullName.text = "Bệnh gout"
                binding.llGout.show()
            }
            else -> {
                binding.txtFullName.text = "Bệnh viêm dạ dày"
                binding.llViemDaDay.show()
            }
        }
    }

    private fun initListener() {
        binding.back.setOnClickListener {
            onBack()
        }
    }

    private fun onBack() {
        findNavController().popBackStack()
    }
}