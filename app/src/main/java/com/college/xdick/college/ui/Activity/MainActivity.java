package com.college.xdick.college.ui.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;
import com.college.xdick.college.ui.Fragment.FindFragment;
import com.college.xdick.college.ui.Fragment.MainFragment;
import com.college.xdick.college.ui.Fragment.UserFragment;
import com.college.xdick.college.R;
import com.college.xdick.college.bean.Dynamics;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.notification.BmobNotificationManager;

public class MainActivity extends AppCompatActivity implements MessageListHandler {


    private BottomNavigationBar mBottomNavigationBar;
    private List<Dynamics>  dynamicsList = new ArrayList<>();
    private TextBadgeItem mBadgeItem;


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
     mBadgeItem = new TextBadgeItem().setBorderWidth(4)
                .setAnimationDuration(200)
                .setBackgroundColorResource(R.color.red)
                .setHideOnSelect(false)
                .hide();



        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        // mBottomNavigationBar.setInActiveColor("#FFFFFF");
        mBottomNavigationBar.setActiveColor(R.color.colorPrimaryDark);
        // mBottomNavigationBar.setBarBackgroundColor(R.color.colorPrimaryDark);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.main, "首页").setActiveColor(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.find, "发现").setActiveColor(R.color.colorPrimary).setBadgeItem(mBadgeItem))
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


    @Override
    protected void onResume() {
        setBadgeItem();
        BmobIM.getInstance().addMessageListHandler(this);
        //进入应用后，通知栏应取消
        BmobNotificationManager.getInstance(this).cancelNotification();
        super.onResume();
    }

    @Override
    protected void onPause() {
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {

        setBadgeItem();
    }


    public void setBadgeItem(){
        long num = BmobIM.getInstance().getAllUnReadCount();;
       if(num ==0){
           mBadgeItem.hide();
       } else{mBadgeItem.show();
        mBadgeItem.setText(String.valueOf(num));}
    }

}
