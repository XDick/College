package com.college.xdick.college.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.college.xdick.college.Fragment.FindFragment;
import com.college.xdick.college.Fragment.MainFragment;
import com.college.xdick.college.Fragment.UserFragment;
import com.college.xdick.college.R;
import com.college.xdick.college.util.Dynamics;
import com.college.xdick.college.util.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationBar mBottomNavigationBar;
    private List<Dynamics>  dynamicsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getListData();
        initView();
        replaceFragment(new MainFragment());


    }


    public void initView() {

        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        setBottomNav();   //设置底部导航栏

    }


    public void setBottomNav() {
        TextBadgeItem mBadgeItem = new TextBadgeItem().setBackgroundColor(Color.RED).setText("99");
        ;
        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        // mBottomNavigationBar.setInActiveColor("#FFFFFF");
        mBottomNavigationBar.setActiveColor(R.color.colorPrimaryDark);
        // mBottomNavigationBar.setBarBackgroundColor(R.color.colorPrimaryDark);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.main, "首页").setActiveColor(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.find, "发现").setActiveColor(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.user, "我").setActiveColor(R.color.colorPrimary))
                .setFirstSelectedPosition(0)
                .initialise();

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {//这里也可以使用SimpleOnTabSelectedListener
            @Override
            public void onTabSelected(int position) {//未选中 -> 选中
                switch (position) {
                    case 0:
                        replaceFragment(new MainFragment());
                        break;
                    case 1:
                        replaceFragment(new FindFragment());
                        break;
                    case 2:
                        replaceFragment(new UserFragment());
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
        });
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }


    public List<Dynamics> getListData(){
        Intent intent= getIntent();
        dynamicsList = (List<Dynamics>)intent.getSerializableExtra("LISTDATA");
        return dynamicsList;
    }



    /*--------------------------------监听返回键--------------------*/
   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        User bmobUser = BmobUser.getCurrentUser(User.class);
        if (bmobUser != null) {

        }
    }*/






}