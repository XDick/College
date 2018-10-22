package com.college.xdick.findme.ui.Activity;


import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.college.xdick.findme.ui.Fragment.MessageFragment;
import com.college.xdick.findme.ui.Fragment.SearchFragment;
import com.college.xdick.findme.ui.Fragment.UserFragment;
import com.college.xdick.findme.R;
import com.college.xdick.findme.util.AppManager;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.update.BmobUpdateAgent;

public class    MainActivity extends AppCompatActivity implements MessageListHandler {


    private BottomNavigationBar mBottomNavigationBar;
    private TextBadgeItem mBadgeItem;
    public LocationClient mLocationClient;
    private int unReadNum=0;
    private long bmobTime=0;
    private ProgressDialog dialog=null;
    private AlertDialog.Builder builder=null ;
    private  AlertDialog dialog2=null;
    private Fragment mContent;
    private List<Fragment> mFragment;

    private AlertDialog bannedDialog=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CancelNotify();
        initFragment();
        bmobTime=getIntent().getLongExtra("TIME",0);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        initView();
        switchFragment(mContent,getFragment(0));
        if (BmobUser.getCurrentUser(MyUser.class) != null) {
            fetchUserInfo();
            requestLocation();

            if (BmobUser.getCurrentUser(MyUser.class).getMobilePhoneNumber()==null) {
                startActivity(new Intent(this, ModifyNameActivity.class));
                Toast.makeText(this, "完善一下信息吧(#^.^#)", Toast.LENGTH_SHORT).show();
                 return;
            }

            if (BmobUser.getCurrentUser(MyUser.class).getTag() == null) {
                startActivity(new Intent(this, InterestActivity.class));
                Toast.makeText(this, "请选择喜欢的标签", Toast.LENGTH_SHORT).show();
            }


        }

        if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.DISCONNECT)){
            IMconnectBomob();
        }

         setIMListener();


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

                    case 2:
                        if (MyUser.getCurrentUser(MyUser.class) == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();

                           // Toast.makeText(MainActivity.this, "请先登录（*＾-＾*）", Toast.LENGTH_SHORT).show();

                            return;

                        }


                        break;
                    case 3:
                        if (MyUser.getCurrentUser(MyUser.class) == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                            //Toast.makeText(MainActivity.this, "请先登录（*＾-＾*）", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    case 4:
                        if (MyUser.getCurrentUser(MyUser.class) == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                           // Toast.makeText(MainActivity.this, "请先登录（*＾-＾*）", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        break;
                    default:
                        break;
                }


                Fragment to = getFragment(position);
                switchFragment(mContent,to);

            }

            @Override
            public void onTabUnselected(int position) {//选中 -> 未选中
            }

            @Override
            public void onTabReselected(int position) {//选中 -> 选中


            }
        });
    }
    private void switchFragment(Fragment from, Fragment to) {
          if (from!=to){
              mContent=to;
              FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
              if (!to.isAdded())
              {
                   if (from!=null){
                       ft.hide(from);
                   }

                   if (to!=null){
                       ft.add(R.id.fragment,to).commit();
                   }
              }
              else {
                  if (from!=null){
                      ft.hide(from);
                  }

                  if (to!=null){
                      ft.show(to).commit();
                  }
              }
          }
    }

  /*private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
    }*/

  private void initFragment(){

      mFragment = new ArrayList<>();
      mFragment.add(new MainActivityFragment());
      mFragment.add(new SearchFragment());
      mFragment.add(new DynamicsFragment());
      mFragment.add(new MainMessageFragment());
      mFragment.add(new UserFragment());
  }
  private Fragment getFragment(int position){
      return mFragment.get(position);
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
        setIMListener();
        setBadgeItem();
        if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.DISCONNECT)){
            IMconnectBomob();
        }
        else if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.CONNECTED))
        {  if (dialog!=null){
            dialog.dismiss();}
        if (dialog2!=null){
            dialog2.dismiss();}
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        BmobIM.getInstance().removeMessageListHandler(this);
        super.onPause();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {

       // if (list.isEmpty()) {
            setBadgeItem();
    /*    } else {
            setBadgeItem(list);
        }*/
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
        if (gpsEvent.getMessage()==null){
            return;
        }
        if (myUser!=null&&!Arrays.asList(gpsEvent.getMessage()).contains(null)){
            MyUser myUser1 = new MyUser();
            myUser1.setGps(gpsEvent.getMessage());
            myUser1.update(myUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.d("TAG", "更新地址" + Arrays.toString(gpsEvent.getMessage()));
                    } else {

                        Log.d("TAG", "cuowu" + e.toString());
                    }
                }
            });
        }
        mLocationClient.stop();

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


    @Override
    public void onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            super.onBackPressed();
        }
    }


   private void CancelNotify(){

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(1);    }

    public  void setBmobTime(long bmobTime) {
        this.bmobTime = bmobTime;
    }
    public  long getBmobTime(){
        return this.bmobTime;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    private void IMconnectBomob() {

        final MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            if (!TextUtils.isEmpty(bmobUser.getObjectId())) {
                BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            try {
                                BmobIM.getInstance().
                                        updateUserInfo(new BmobIMUserInfo( bmobUser.getObjectId(),
                                                bmobUser.getUsername(),  bmobUser.getAvatar()));
                            }
                            catch (Exception e2){
                                e2.printStackTrace();
                            }

                        } else {
                            //连接失败


                            if (dialog!=null){
                                dialog.dismiss();
                            }

                            if (dialog2==null){
                                initDialog2();
                            }
                            dialog2.show();

                            //Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }



    private void initDialog1() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在连接服务器...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


    }

    private void initDialog2() {
        builder = new AlertDialog.Builder(this);
        builder.setTitle("连接断开");
        builder.setMessage("你可以点击重试重新连接");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();

            }
        });
        builder.setPositiveButton("重试", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                IMconnectBomob();
                dialog2.dismiss();
            }
        });


        dialog2 = builder.create();
        dialog2.setCancelable(false);
        dialog2.setCanceledOnTouchOutside(false);
    }

    private void setIMListener(){
        BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
            @Override
            public void onChange(ConnectionStatus status) {
                if (BmobUser.getCurrentUser()!=null){


                    if (status.getMsg().equals("connected")) {

                        if (dialog!=null){
                            dialog.dismiss();}
                        if (dialog2!=null){
                            dialog2.dismiss();}


                    }
                    else if (status.getMsg().equals("connecting")) {



                        if (dialog==null){
                            initDialog1();
                        }
                        dialog.show();
                        if (dialog2==null){
                            initDialog2();
                        }
                        if (dialog2!=null) {
                            dialog2.dismiss();
                        }


                    }



                    else {

                        IMconnectBomob();



                        if (dialog!=null){
                            dialog.dismiss();
                        }

                        if (dialog2==null){
                            initDialog2();
                        }
                        dialog2.show();


                    }
                }

               // Toast.makeText(getBaseContext(),status.getMsg(),Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void fetchUserInfo() {
        BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    try {
                        JSONObject json = new JSONObject(s);
                        if (json.getString("isBanned").equals("true")){

                            showBannedDialog(  json.getString("bannedReason")  );
                           }
                    }
                    catch (Exception e2){
                        e2.printStackTrace();
                    }

                } else {
                    //log(e);
                }
            }
        });
    }


    private void showBannedDialog(String reason){

        AlertDialog.Builder builder = new AlertDialog.Builder
                (this);

        builder.setTitle("你已经被封禁");

        builder.setMessage(reason);
        builder.setNegativeButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bannedDialog.dismiss();
                BmobUser.logOut();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });



        bannedDialog = builder.create();
        bannedDialog.setCancelable(false);
        bannedDialog.setCanceledOnTouchOutside(false);
        bannedDialog.show();
    }
}





