package com.example.blindfoldchess.ui.history

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HistoryPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> GameListFragment.newInstance(isManual = false)
            1 -> GameListFragment.newInstance(isManual = true)
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}