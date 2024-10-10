package com.nguyentoan.bepngon.ui.baidang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.ActivityDetailBaiDangBinding
import com.nguyentoan.bepngon.databinding.ActivityShowAnhBinding
import com.squareup.picasso.Picasso

class ShowAnhActivity : AppCompatActivity() {

    lateinit var binding : ActivityShowAnhBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowAnhBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Picasso.get().load(
            intent.getStringExtra("link")
        ).into(binding.imgShow)

        initListener()
    }

    private fun initListener() {
        binding.imgBack.setOnClickListener {
            onBackPressed()
        }
    }
}