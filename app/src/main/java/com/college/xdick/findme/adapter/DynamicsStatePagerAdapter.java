package com.college.xdick.findme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.college.xdick.findme.ui.Fragment.ActivityFollowFragment;
import com.college.xdick.findme.ui.Fragment.ActivitygpsFragment;
import com.college.xdick.findme.ui.Fragment.ActivityschoolFragment;
import com.college.xdick.findme.ui.Fragment.AllDynamicsFragment;
import com.college.xdick.findme.ui.Fragment.GoodsFragment;
import com.college.xdick.findme.ui.Fragment.MyDynamicsFragment;

/**
 * Created by Administrator on 2018/5/6.
 */

public class DynamicsStatePagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabTilte;

    public DynamicsStatePagerAdapter(FragmentManager fm, String[] tabTitle) {
        super(fm);
        this.tabTilte = tabTitle;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MyDynamicsFragment();
            case 1:
                return new AllDynamicsFragment();


        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTilte.length;
    }




}


