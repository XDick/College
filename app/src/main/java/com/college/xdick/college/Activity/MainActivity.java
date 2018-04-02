package com.college.xdick.college.Activity;

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
import android.view.Menu;
import android.view.MenuItem;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.college.xdick.college.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private BottomNavigationBar mBottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    public void initView(){
        Toolbar toolbar =findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mBottomNavigationBar =findViewById(R.id.bottom_navigation_bar);
        setBottomNav();   //设置底部导航栏

        NavigationView navigationView =findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

    }


    @Override                  //设置ToolBar上的按钮
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.toolbar_menu,menu);
       return true;
    }

    @Override                //ToolBar上面的按钮事件
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;

            default:
                break;
        }

        return true;
    }
     public void setBottomNav(){
         TextBadgeItem mBadgeItem = new TextBadgeItem().setBackgroundColor(Color.RED).setText("99");;
         mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
         mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        // mBottomNavigationBar.setInActiveColor("#FFFFFF");
         mBottomNavigationBar.setActiveColor(R.color.colorPrimaryDark);
        // mBottomNavigationBar.setBarBackgroundColor(R.color.colorPrimaryDark);

         mBottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "首页").setActiveColor(R.color.colorPrimary))
                 .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "发现").setActiveColor(R.color.colorPrimary))
                 .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "我").setActiveColor(R.color.colorPrimary))
                 .setFirstSelectedPosition(0)
                 .initialise();

         mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {//这里也可以使用SimpleOnTabSelectedListener
             @Override
             public void onTabSelected(int position) {//未选中 -> 选中
             }

             @Override
             public void onTabUnselected(int position) {//选中 -> 未选中
             }

             @Override
             public void onTabReselected(int position) {//选中 -> 选中


             }
         });
     }


    private void replaceFragment (Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.fragment,fragment);
        transaction.commit();

    }
}
