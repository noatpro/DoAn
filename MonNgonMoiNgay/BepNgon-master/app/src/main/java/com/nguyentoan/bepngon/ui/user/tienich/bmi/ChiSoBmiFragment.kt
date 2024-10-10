package com.nguyentoan.bepngon.ui.user.tienich.bmi

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.FragmentChiSoBmiBinding
import com.nguyentoan.bepngon.util.showToast

class ChiSoBmiFragment : Fragment() {

    lateinit var binding : FragmentChiSoBmiBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChiSoBmiBinding.inflate(inflater, container, false)

        initListener()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initListener() {
        binding.back.setOnClickListener {
            onBack()
        }

        binding.txtTinh.setOnClickListener{
            if (binding.edtChieuCao.text.toString().trim().isEmpty()
                || binding.edtCanNang.text.toString().trim().isEmpty()
            ) {
                requireContext().showToast("Nhập thiếu dữ liệu!")
            } else {
                val canNang = binding.edtCanNang.text.toString().trim().toFloat()
                val chieuCao = binding.edtChieuCao.text.toString().trim().toFloat()

                val bmi = canNang / ((chieuCao * chieuCao) / 10000)

                showAns(bmi)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAns(answer : Float) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.layout_dialog_show_ans_bmi)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val tvBMI = dialog.findViewById<TextView>(R.id.tvBMI)
        val tvKetQua = dialog.findViewById<TextView>(R.id.tvKetQua)
        val txtThap = dialog.findViewById<TextView>(R.id.txtThap)
        val textClose = dialog.findViewById<ImageView>(R.id.textView1)
        var data = 1
        if (answer < 16) {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_1))
            tvKetQua.text = "Bạn đang gầy cấp độ 3."
            data = 1
        } else if (answer < 17) {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_1))
            tvKetQua.text = "Bạn đang gầy cấp độ 2."
            data = 1
        } else if (answer < 18.5) {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_1))
            tvKetQua.text = "Bạn đang gầy cấp độ 1."
            data = 1
        } else if (answer < 25) {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_2))
            tvKetQua.text = "Bạn đang ở trạng thái bình thường."
            data = 2
        } else if (answer < 30) {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_3))
            tvKetQua.text = "Bạn đang thừa cân."
            data = 3
        } else if (answer < 35) {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_3))
            tvKetQua.text = "Bạn đang béo phì độ 1."
            data = 3
        } else if (answer < 40) {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_3))
            tvKetQua.text = "Bạn đang béo phì cấp độ 2."
            data = 3
        } else {
            tvBMI.text = "$answer"
            tvBMI.setTextColor(requireContext().getColor(R.color.bmi_3))
            tvKetQua.text = "Bạn đang béo phì cấp độ 3."
            data = 3
        }

        textClose.setOnClickListener {
            dialog.dismiss()
        }

        txtThap.setOnClickListener {
            dialog.dismiss()
            val bundle = bundleOf("data" to data)
            findNavController().navigate(R.id.action_chiSoBmiFragment_to_thapDinhDuongFragment, bundle
            )
        }

        dialog.show()
    }

    private fun onBack() {
        requireActivity().onBackPressed()
    }
}