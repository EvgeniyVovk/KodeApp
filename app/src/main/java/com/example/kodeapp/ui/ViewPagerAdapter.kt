package com.example.kodeapp.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.kodeapp.utils.Constants.tabNames

class ViewPagerAdapter(fm: FragmentManager,
                       lifecycle: Lifecycle
): FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int = tabNames.size

    override fun createFragment(position: Int): Fragment {
        return UserListFragment.newInstance(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}