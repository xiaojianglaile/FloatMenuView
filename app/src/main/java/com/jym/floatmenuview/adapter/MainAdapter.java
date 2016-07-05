package com.jym.floatmenuview.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.jym.floatmenuview.fragment.OneFragment;
import com.jym.floatmenuview.fragment.ThreeFragment;
import com.jym.floatmenuview.fragment.TwoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 2016/7/5 0005.
 */
public class MainAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;

    public MainAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        fragments.add(new OneFragment());
        fragments.add(new TwoFragment());
        fragments.add(new ThreeFragment());
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
