package com.college.xdick.findme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.college.xdick.findme.ui.Fragment.ActivityFollowFragment;
import com.college.xdick.findme.ui.Fragment.ActivityFragment;
import com.college.xdick.findme.ui.Fragment.ActivitygpsFragment;
import com.college.xdick.findme.ui.Fragment.ActivityschoolFragment;
import com.college.xdick.findme.ui.Fragment.GoodsFragment;

/**
 * Created by Administrator on 2018/5/6.
 */

public class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabTilte;

    public MyFragmentStatePagerAdapter(FragmentManager fm, String[] tabTitle) {
        super(fm);
        this.tabTilte = tabTitle;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new GoodsFragment();
            case 1:
                return new ActivityschoolFragment();
            case 2:
                return new ActivitygpsFragment();
            case 3:
               return new ActivityFollowFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTilte.length;
    }




}


