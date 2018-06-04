package com.college.xdick.findme.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import com.college.xdick.findme.ui.Fragment.ActivitygpsFragment;

import com.college.xdick.findme.ui.Fragment.MessageFragment;
import com.college.xdick.findme.ui.Fragment.PrivateConversationFragment;

/**
 * Created by Administrator on 2018/5/6.
 */

public class MessageFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private String[] tabTilte;

    public MessageFragmentStatePagerAdapter(FragmentManager fm, String[] tabTitle) {
        super(fm);
        this.tabTilte = tabTitle;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PrivateConversationFragment();//第一页面的fragment
            case 1:
                return new MessageFragment();//第二页面的fragment

        }
        return null;
    }

    @Override
    public int getCount() {
        return tabTilte.length;
    }


}


