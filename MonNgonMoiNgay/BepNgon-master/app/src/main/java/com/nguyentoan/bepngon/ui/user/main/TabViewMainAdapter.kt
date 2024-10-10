package com.nguyentoan.bepngon.ui.user.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.nguyentoan.bepngon.ui.user.main.friend.FriendFragment
import com.nguyentoan.bepngon.ui.user.main.home.HomeFragment
import com.nguyentoan.bepngon.ui.user.main.menu.MenuFragment
import com.nguyentoan.bepngon.ui.user.main.noti.NotificationFragment
import com.nguyentoan.bepngon.ui.search.SearchFragment

class TabViewMainAdapter (private val myContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> SearchFragment()
            2 -> FriendFragment()
            3 -> NotificationFragment()
            4 -> MenuFragment()
            else -> HomeFragment()
        }
    }

    override fun getCount(): Int {
        return 5
    }
}