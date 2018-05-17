package com.college.xdick.findme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.college.xdick.findme.ui.Fragment.ActivityFragment;
import com.college.xdick.findme.ui.Fragment.ActivitygpsFragment;
import com.college.xdick.findme.ui.Fragment.ActivityschoolFragment;

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
                return new ActivityFragment();//第一页面的fragment
            case 1:
                return new ActivitygpsFragment();//第二页面的fragment
            case 2:
                return new ActivityschoolFragment();//第三页面的fragment
        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTilte.length;
    }


}
