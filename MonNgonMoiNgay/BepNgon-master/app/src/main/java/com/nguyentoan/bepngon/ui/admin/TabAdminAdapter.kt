package com.nguyentoan.bepngon.ui.admin

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nguyentoan.bepngon.ui.user.main.home.HomeFragment
import com.nguyentoan.bepngon.ui.search.SearchFragment

class TabAdminAdapter (private val myContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> SearchFragment()
            2 -> ManageAccountFragment()
            3 -> ReportFragment()
            4 -> MenuAdminFragment()
            else -> HomeFragment()
        }
    }

    override fun getCount(): Int {
        return 5
    }
}