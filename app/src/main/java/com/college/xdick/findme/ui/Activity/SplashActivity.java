package com.college.xdick.findme.ui.Activity;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.college.xdick.findme.R;

import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.bean.MyUser;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by Administrator on 2018/4/4.
 */

public class SplashActivity extends Activity {

   private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
   private Intent intent;



    @Override
    protected void onCreate(Bundle arg0) {
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        super.onCreate(arg0);
        List<MyActivity> list = new ArrayList<>();
        intent= new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("TIME", 0);
        intent.putExtra("LISTDATA", (Serializable) list);

    }

    @Override
    protected void onStart() {
        super.onStart();
        askPermissions();


    }


    private void initData(){

        //进入主页面
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(final Long aLong, BmobException e) {

                    BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
                    List<BmobQuery<MyActivity>> queries = new ArrayList<>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
                    if (bmobUser != null) {
                      /*  String gps[] = bmobUser.getGps();
                        if (gps != null) {
                            query.addWhereEqualTo("gps", gps[1]);
                        }*/


                           String tag[] = bmobUser.getTag();
                           if (tag!=null){
                               for (int i =0; i<tag.length;i++){
                                   BmobQuery<MyActivity> q = new BmobQuery<MyActivity>();
                                   q.addWhereContainsAll("tag",Arrays.asList(tag[i]));
                                   queries.add(q);
                                   query.or(queries);
                               }

                           }




                    }
                if (e == null) {
                    query.addWhereGreaterThan("date", aLong * 1000L - 2.5*60 * 60 * 24 * 1000);
                }



                     query.setLimit(10);

                    query.order("-createdAt");
//执行查询方法
                    query.findObjects(new FindListener<MyActivity>() {
                        @Override
                        public void done(List<MyActivity> object, BmobException e) {



                                IMconnectBomob();

                            if (e == null) {

                                intent.putExtra("LISTDATA", (Serializable)object);
                                intent.putExtra("TIME", aLong * 1000L);


                            } else {
                                Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            if (bmobUser!=null){
                                BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                                    @Override
                                    public void onChange(ConnectionStatus status) {

                                        if (status.getMsg().equals("connected")) {
                                            startActivity(intent);

                                            finish();

                                        }
                                        else if (status.getMsg().equals("disconnect")){
                                            IMconnectBomob();

                                        }
                                    }
                                });
                            }
                            else {
                                startActivity(intent);
                                finish();
                            }
                        }
                    });



            }
        });





    }



    private void askPermissions() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.
                PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.
                PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }


        if (!permissionList.isEmpty()) {
            String[] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(SplashActivity.this, permission, 1);
        } else {
            initData();
        }
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
                          // startActivity(intent);
                           //finish();
                            //连接失败
                            Toast.makeText(getBaseContext(),"无法连接服务器", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
