package com.nguyentoan.bepngon.ui.user.tienich.chedoan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.databinding.ActivityCheDoAnBinding

class CheDoAnActivity : AppCompatActivity() {
    lateinit var binding: ActivityCheDoAnBinding

    private lateinit var navController: NavController
    private var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheDoAnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.containerFragment) as NavHostFragment
        navController = navHostFragment!!.navController
    }
}