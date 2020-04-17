package com.study.fldemo.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.util.Log

import com.study.fldemo.fragment.BaseFragment

/**
 */

class VpAdapter(private val fm: FragmentManager, private val lists: List<BaseFragment>, private val titles: List<String>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return lists[position]
    }

    override fun getCount(): Int {
        return lists.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}
