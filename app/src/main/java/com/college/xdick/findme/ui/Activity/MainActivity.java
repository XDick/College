package com.college.xdick.findme.ui.Activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.ashokvarma.bottomnavigation.TextBadgeItem;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.college.xdick.findme.Listener.MyLocationListener;
import com.college.xdick.findme.MyClass.BackHandlerHelper;
import com.college.xdick.findme.MyClass.GpsEvent;
import com.college.xdick.findme.MyClass.IMMLeaks;
import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.bean.ActivityMessageBean;

import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Fragment.MainActivityFragment;
import com.college.xdick.findme.ui.Fragment.MainMessageFragment;
import com.college.xdick.findme.ui.Fragment.DynamicsFragment;
import com.college.xdick.findme.ui.Fragment.SearchFragment;
import com.college.xdick.findme.ui.Fragment.UserFragment;
import com.college.xdick.findme.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

public class    MainActivity extends AppCompatActivity implements MessageListHandler {


    private BottomNavigationBar mBottomNavigationBar;
    private TextBadgeItem mBadgeItem;
    public LocationClient mLocationClient;
    private int unReadNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CancelNotify();
        IMconnectBomob();



        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        initView();
        replaceFragment(new MainActivityFragment());
        if (BmobUser.getCurrentUser(MyUser.class) != null) {
            requestLocation();
            if (BmobUser.getCurrentUser(MyUser.class).getTag() == null) {
                startActivity(new Intent(this, InterestActivity.class));
                Toast.makeText(this, "请选择喜欢的标签", Toast.LENGTH_SHORT).show();
            }

            IMconnectBomob();

            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {

                   if (BmobUser.getCurrentUser() != null && status.getMsg().equals("disconnect")) {
                        IMconnectBomob();
                    }
                    //Toast.makeText(getApplicationContext(),status.getMsg(),Toast.LENGTH_SHORT).show();

                }
            });

        }
        IMMLeaks.fixFocusedViewLeak(getApplication());

        //自动检查更新
        BmobUpdateAgent.setUpdateOnlyWifi(false);
        BmobUpdateAgent.update(this);

        //EventBus
        EventBus.getDefault().register(this);


    }


    public void initView() {

        mBottomNavigationBar = findViewById(R.id.bottom_navigation_bar);
        setBottomNav();   //设置底部导航栏

    }


    public void setBottomNav() {
        mBadgeItem = new TextBadgeItem()
                .setGravity(Gravity.CENTER | Gravity.TOP)
                .setBorderWidth(1)
                .setAnimationDuration(200)
                .setBackgroundColorResource(R.color.red)
                .setHideOnSelect(false)
                .hide();


        mBottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED_NO_TITLE);
        mBottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        // mBottomNavigationBar.setInActiveColor("#FFFFFF");
        mBottomNavigationBar.setActiveColor(R.color.colorPrimary);
        // mBottomNavigationBar.setBarBackgroundColor(R.color.colorPrimaryDark);

        mBottomNavigationBar.addItem(new BottomNavigationItem(R.drawable.main, "首页"))
                .addItem(new BottomNavigationItem(R.drawable.find, "发现"))
                .addItem(new BottomNavigationItem(R.drawable.talk, "动态"))
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
                        if (MyUser.getCurrentUser(MyUser.class) == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(MainActivity.this, "请先登录（*＾-＾*）", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        replaceFragment(new DynamicsFragment());

                        break;
                    case 3:
                        if (MyUser.getCurrentUser(MyUser.class) == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(MainActivity.this, "请先登录（*＾-＾*）", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        replaceFragment(new MainMessageFragment());
                        break;
                    case 4:
                        if (MyUser.getCurrentUser(MyUser.class) == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            Toast.makeText(MainActivity.this, "请先登录（*＾-＾*）", Toast.LENGTH_SHORT).show();
                            break;
                        }
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


    @Override
    protected void onStart() {
        setBadgeItem();
        CancelNotify();
        super.onStart();

    }

    @Override
    protected void onResume() {
        BmobIM.getInstance().addMessageListHandler(this);
        setBadgeItem();
        super.onResume();
    }

    @Override
    protected void onPause() {
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {

        if (list.isEmpty()) {
            setBadgeItem();
        } else {
            setBadgeItem(list);
        }
    }


    public void setBadgeItem() {
        if (BmobUser.getCurrentUser() != null) {
            List<ActivityMessageBean> list = DataSupport.where("currentuserId =? and ifCheck =?"
                    , BmobUser.getCurrentUser().getObjectId(), "false").find(ActivityMessageBean.class);
            int msgNum = list.size();
            int conversationNum = (int) BmobIM.getInstance().getAllUnReadCount();
            unReadNum = msgNum + conversationNum;

            if (unReadNum == 0) {
                mBadgeItem.hide();
            } else {
                mBadgeItem.show();
                mBadgeItem.setText(unReadNum + "");
            }
        }
    }


    public void setBadgeItem(List<MessageEvent> list) {

        int sum = list.size();
        unReadNum = unReadNum + sum;
        if (unReadNum == 0) {
            mBadgeItem.hide();
        } else {
            mBadgeItem.show();
            mBadgeItem.setText(unReadNum + "");
        }
    }






    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }


    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(final GpsEvent gpsEvent) {

        MyUser myUser = BmobUser.getCurrentUser(MyUser.class);
        if (myUser!=null&&gpsEvent.getMessage()!=null){
            myUser.setGps(gpsEvent.getMessage());
            myUser.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        mLocationClient.stop();
                        Log.d("TAG", "更新地址" + Arrays.toString(gpsEvent.getMessage()));
                    } else {

                        Log.d("TAG", "cuowu" + e.toString());
                    }
                }
            });
        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(final ReadEvent readEvent) {
        setBadgeItem();
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        mLocationClient.stop();
        BmobIM.getInstance().clear();

    }

    private void IMconnectBomob() {

        final MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            if (!TextUtils.isEmpty(bmobUser.getObjectId())) {
                BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            if (BmobUser.getCurrentUser() != null){
                                BmobIM.getInstance().
                                        updateUserInfo(new BmobIMUserInfo( bmobUser.getObjectId(),
                                                bmobUser.getUsername(),  bmobUser.getAvatar()));
                            }

                        } else {
                            //连接失败
                            Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }


    void CancelNotify(){

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);    }
}




