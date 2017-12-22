package com.study.fldemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.study.fldemo.fragment.BaseFragment;

import java.util.List;

/**
 * Created by zouqianyu on 2017/9/16.
 */

public class VpAdapter extends FragmentPagerAdapter {
    private final FragmentManager fm;
    private final List<BaseFragment> lists;
    private final List<String> titles;

    public VpAdapter(FragmentManager fm, List<BaseFragment> lists, List<String> titles) {
        super(fm);
        this.fm = fm;
        this.lists = lists;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("resume::","加载完了===");
        return lists.get(position);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
