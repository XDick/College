package com.college.xdick.findme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.college.xdick.findme.ui.Fragment.MessageFragment;
import com.college.xdick.findme.ui.Fragment.PrivateConversationFragment;
import com.college.xdick.findme.ui.Fragment.UserCenterActivityFragment;
import com.college.xdick.findme.ui.Fragment.UserCenterDynamicsFragment;

/**
 * Created by Administrator on 2018/5/6.
 */

public class UserCenterFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabTilte;

    public UserCenterFragmentStatePagerAdapter(FragmentManager fm, String[] tabTitle) {
        super(fm);
        this.tabTilte = tabTitle;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UserCenterDynamicsFragment();//第一页面的fragment
            case 1:
                return new UserCenterActivityFragment();//第二页面的fragment

        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTilte.length;
    }


}


