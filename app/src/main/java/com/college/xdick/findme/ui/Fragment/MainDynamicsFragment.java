package com.college.xdick.findme.ui.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.college.xdick.findme.R;
import com.college.xdick.findme.adapter.DynamicsStatePagerAdapter;

import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.SetDynamicsActivity;
import com.college.xdick.findme.ui.Base.BaseFragment;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


/**
 * Created by Administrator on 2018/5/23.
 */

public class MainDynamicsFragment extends BaseFragment  {

    private ViewPager mViewPager1;
    private TabLayout mTabLayout;
    private String[] tabTitle = {"关注", "最近"};//每个页面顶部标签的名字
    private DynamicsStatePagerAdapter adapter;
    private ImageView setDynamics;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView =inflater.inflate(R.layout.fragment_main_dynamics,container,false);
         initView();
        return  rootView;
    }

    private void initView(){


        ImageView background = rootView.findViewById(R.id.background);
        Glide.with(this).load(R.drawable.findme_background)
                .apply(bitmapTransform(new BlurTransformation(4, 3)))
                .into(background);


        setDynamics= rootView.findViewById(R.id.set_dynamics);
        setDynamics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyUser.getCurrentUser()!=null){


                    Intent intent = new Intent(getContext(), SetDynamicsActivity.class);
                    startActivity(intent);}


                else
                {
                    Toast.makeText(getContext(),"请先登录",Toast.LENGTH_SHORT).show();
                }
            }
        });
        mViewPager1 = rootView.findViewById(R.id.mViewPager1);
        mTabLayout =  rootView.findViewById(R.id.mTabLayout);
        adapter = new DynamicsStatePagerAdapter(getChildFragmentManager(), tabTitle);

        for (int i = 0; i < tabTitle.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitle[i]));
        }
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);



        //这里注意的是，因为我是在fragment中创建MyFragmentStatePagerAdapter，所以要传getChildFragmentManager()
        mViewPager1.setAdapter(adapter);
        mViewPager1 .setOffscreenPageLimit(1);

        //在设置viewpager页面滑动监听时，创建TabLayout的滑动监听
        mViewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //在选中的顶部标签时，为viewpager设置currentitem
                mViewPager1.setCurrentItem(tab.getPosition());



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }





}
