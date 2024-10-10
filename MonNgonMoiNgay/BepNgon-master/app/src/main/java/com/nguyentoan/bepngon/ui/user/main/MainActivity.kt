package com.nguyentoan.bepngon.ui.user.main

import android.annotation.SuppressLint
import android.os.Handler
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import com.nguyentoan.bepngon.R
import com.nguyentoan.bepngon.base.BaseActivity
import com.nguyentoan.bepngon.databinding.ActivityMainBinding
import com.nguyentoan.bepngon.model.ProfileModel
import com.nguyentoan.bepngon.ui.search.SearchActivity
import com.nguyentoan.bepngon.util.DataHelper
import com.nguyentoan.bepngon.sever.FBConstant
import com.nguyentoan.bepngon.util.SharePreferenceUtils
import com.nguyentoan.bepngon.util.showToast
import com.nguyentoan.bepngon.view.gone
import com.nguyentoan.bepngon.view.openActivity
import com.nguyentoan.bepngon.view.show

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun onResume() {
        super.onResume()
        binding.viewPagerMain.currentItem = numberMenu
        binding.btNaviMain.selectedItemId = when (numberMenu) {
            0 -> {
                R.id.menu_home
            }
            2 -> {
                R.id.menu_friend
            }
            3 -> {
                R.id.menu_thong_bao
            }
            4 -> {
                R.id.menu_menu
            }
            else -> {
                R.id.menu_home
            }
        }

        getDataProfileUser()
    }

    private var numberMenu = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun initCreate() {
        val adapter = TabViewMainAdapter(this@MainActivity, supportFragmentManager)
        binding.viewPagerMain.adapter = adapter
        binding.btNaviMain.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    numberMenu = 0
                    binding.viewPagerMain.currentItem = 0
                }
                R.id.menu_search -> {
                    openActivity(SearchActivity::class.java)
                }
                R.id.menu_friend -> {
                    numberMenu = 2
                    binding.viewPagerMain.currentItem = 2
                }
                R.id.menu_thong_bao -> {
                    numberMenu = 3
                    binding.viewPagerMain.currentItem = 3
                }
                R.id.menu_menu -> {
                    numberMenu = 4
                    binding.viewPagerMain.currentItem = 4
                }
                else -> {
                    numberMenu = 0
                    binding.viewPagerMain.currentItem = 0
                }
            }
            true
        })

        binding.viewPagerMain.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                }

                MotionEvent.ACTION_MOVE -> {

                }

                MotionEvent.ACTION_UP -> {

                }

                MotionEvent.ACTION_CANCEL -> {

                }
            }
            true
        }
    }

    private val mDatabase = FirebaseDatabase.getInstance().getReference(FBConstant.ROOT)
    private fun getDataProfileUser() {
        val id = SharePreferenceUtils.getAccountID()
        mDatabase.child(FBConstant.PROFILE).child(id).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                val profileModel = result.getValue<ProfileModel>()
                if (profileModel != null) {
                    if (profileModel != DataHelper.profileUser.value )
                    DataHelper.profileUser.postValue(profileModel)
                } else {
                    showToast("Có lỗi kết nối!")
                }
            }
        }.addOnFailureListener {
            showToast("Có lỗi kết nối!")
        }
    }

    fun showBottomNavigation(isShow : Boolean = true) {
        if (isShow) {
            binding.btNaviMain.show()
        } else {
            binding.btNaviMain.gone()
        }
    }

    private var isClichBack = false
    override fun onBackPressed() {
        if (isClichBack) {
            finish()
        } else {
            Toast.makeText(this, getString(R.string.click_back), Toast.LENGTH_SHORT).show()
            isClichBack = true
            Handler().postDelayed({
                isClichBack = false
            }, 1000L)
        }
    }

    override fun inflateViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }
}