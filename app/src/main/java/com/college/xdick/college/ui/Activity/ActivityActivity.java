package com.college.xdick.college.ui.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.college.xdick.college.R;
import com.college.xdick.college.ui.Fragment.ActivityFragment;
import com.college.xdick.college.ui.Fragment.CommentactivityFragment;
import com.college.xdick.college.ui.Fragment.MainFragment;
import com.college.xdick.college.ui.Fragment.MessageFragment;
import com.college.xdick.college.ui.Fragment.SearchFragment;
import com.college.xdick.college.ui.Fragment.StartactivityFragment;
import com.college.xdick.college.ui.Fragment.UserFragment;

/**
 * Created by Administrator on 2018/4/12.
 */

public class ActivityActivity extends AppCompatActivity {
    private BottomNavigationBar mBottomNavigationBar;




    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        initView();
        replaceFragment(new StartactivityFragment());




    }




    private void initView(){


        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar_activity);
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
       // mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        // mBottomNavigationBar.setInActiveColor("#FFFFFF");
        mBottomNavigationBar.setActiveColor(R.color.colorPrimary);
        // mBottomNavigationBar.setBarBackgroundColor(R.color.colorPrimaryDark);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.main, "活动"))
                .addItem(new BottomNavigationItem( R.drawable.find,"评论"))
                .setFirstSelectedPosition(0)
                .initialise();
        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {//这里也可以使用SimpleOnTabSelectedListener
            @Override
            public void onTabSelected(int position) {//未选中 -> 选中
                switch (position) {
                    case 0:
                        replaceFragment(new StartactivityFragment());
                        break;
                    case 1:
                        replaceFragment(new CommentactivityFragment());
                        break;
                    default:
                        break;
                }
            }


            @Override
            public void onTabUnselected(int position) {//选中 -> 未选中
            }

            @Override
            public void onTabReselected(int position) {//选中 -> 选中


            }

        } );


        }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_ac, fragment);
        transaction.commit();
    }


}




