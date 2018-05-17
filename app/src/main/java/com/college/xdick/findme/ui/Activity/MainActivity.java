package com.college.xdick.findme.ui.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.college.xdick.findme.Listener.MyLocationListener;
import com.college.xdick.findme.bean.MyActivity;

import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Fragment.MainActivityFragment;
import com.college.xdick.findme.ui.Fragment.MessageFragment;
import com.college.xdick.findme.ui.Fragment.MainFragment;
import com.college.xdick.findme.ui.Fragment.SearchFragment;
import com.college.xdick.findme.ui.Fragment.UserFragment;
import com.college.xdick.findme.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.notification.BmobNotificationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;

public class MainActivity extends AppCompatActivity implements MessageListHandler {


    private BottomNavigationBar mBottomNavigationBar;
    private List<MyActivity>  acList = new ArrayList<>();
    private TextBadgeItem mBadgeItem;
    public LocationClient mLocationClient;
    public static int flag =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        getListData();
        initView();
        askPermissions();
        replaceFragment(new MainActivityFragment());

        //自动检查更新
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);





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
        mBottomNavigationBar.setActiveColor(R.color.colorPrimary);
        // mBottomNavigationBar.setBarBackgroundColor(R.color.colorPrimaryDark);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.main, "首页"))
                .addItem(new BottomNavigationItem( R.drawable.find,"发现"))
                .addItem(new BottomNavigationItem(R.drawable.comment, "吐槽"))
                .addItem(new BottomNavigationItem(R.drawable.message, "消息").setBadgeItem(mBadgeItem))
                .addItem(new BottomNavigationItem(R.drawable.user, "我"))
                .setFirstSelectedPosition(0)
                .initialise();

        mBottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {//这里也可以使用SimpleOnTabSelectedListener
            @Override
            public void onTabSelected(int position) {//未选中 -> 选中
                switch (position) {
                    case 0:
                        replaceFragment(new MainActivityFragment());
                        break;
                    case 1:

                        replaceFragment(new SearchFragment());
                        break;
                    case 2:

                        replaceFragment(new MainFragment());
                        break;
                    case 3 :

                        replaceFragment(new MessageFragment());
                        break;
                    case 4 :
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


    public List<MyActivity> getListData(){
        Intent intent= getIntent();
       acList = (List<MyActivity>)intent.getSerializableExtra("LISTDATA");
        return acList;
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
    protected void onStart() {
        setBadgeItem();
        super.onStart();

    }

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
        long num = BmobIM.getInstance().getAllUnReadCount();
       if(num ==0){
           mBadgeItem.hide();
       } else{mBadgeItem.show();
        mBadgeItem.setText(String.valueOf(num));}
    }

    private void askPermissions(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.
                PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.
                PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.
                PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }


        if (!permissionList.isEmpty()){
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permission,1);}

        else{
            requestLocation();}
    }



    private void requestLocation(){
        initLocation();
        mLocationClient.start();
    }

    public void onRequestPermissionResult(int requestCode, String[] permission, int[] grantResults) {

        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:

        }

    }
        private void initLocation(){
            LocationClientOption option = new LocationClientOption();
            option.setScanSpan(5000);
            option.setIsNeedAddress(true);
            mLocationClient.setLocOption(option);
        }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocationClient.stop();

    }





    }
