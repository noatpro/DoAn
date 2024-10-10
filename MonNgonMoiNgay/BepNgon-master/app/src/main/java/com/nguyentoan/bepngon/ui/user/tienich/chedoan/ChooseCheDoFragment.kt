package com.nguyentoan.bepngon.ui.user.tienich.chedoan

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.FragmentChooseCheDoBinding

class ChooseCheDoFragment : Fragment() {

    lateinit var binding : FragmentChooseCheDoBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChooseCheDoBinding.inflate(inflater, container, false)

        initListener()

        return binding.root
    }

    private fun initListener() {
        binding.back.setOnClickListener {
            onBack()
        }

        binding.llTreEm.setOnClickListener {
            showChoose(1)
        }

        binding.llNguoiLon.setOnClickListener {
            showChoose(2)
        }

        binding.llNguoiGia.setOnClickListener {
            showChoose(3)
        }

        binding.llTimMach.setOnClickListener {
            showChoose(4)
        }

        binding.llTieuDuong.setOnClickListener {
            showChoose(5)
        }

        binding.llHuyetApCao.setOnClickListener {
            showChoose(6)
        }

        binding.llHuyetApThap.setOnClickListener {
            showChoose(7)
        }

        binding.llMauNhiemMo.setOnClickListener {
            showChoose(8)
        }

        binding.llGout.setOnClickListener {
            showChoose(9)
        }

        binding.llViemDaDay.setOnClickListener {
            showChoose(10)
        }

    }

    private fun showChoose(data : Int) {
        val bundle = bundleOf("data" to data)
        findNavController().navigate(R.id.action_chooseCheDoFragment_to_showCheDoAnFragment, bundle
        )
    }

    private fun onBack() {
        requireActivity().onBackPressed()
    }
}