package com.college.xdick.findme.ui.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    static boolean ifshow = true;
    private static final int sleepTime = 2000;
   private List<MyActivity> acList= new ArrayList<>();
   private MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);



    @Override
    protected void onCreate(Bundle arg0) {
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        super.onCreate(arg0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!ifshow()){
            startActivity(new Intent(SplashActivity.this,MainActivity.class));
            finish();
        }
       else{
            initData();
        }




    }


    private void initData(){
        ifshow= false;
        //进入主页面
        Bmob.getServerTime(new QueryListener<Long>() {
            @Override
            public void done(Long aLong, BmobException e) {

                    BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
                    if (bmobUser != null) {
                      /*  String gps[] = bmobUser.getGps();
                        if (gps != null) {
                            query.addWhereEqualTo("gps", gps[1]);
                        }*/
                    }
                if (e == null) {
                    query.addWhereGreaterThan("date", aLong * 1000L - 1.5*60 * 60 * 24 * 1000);
                }
                    query.setLimit(99);
//执行查询方法
                    query.findObjects(new FindListener<MyActivity>() {
                        @Override
                        public void done(List<MyActivity> object, BmobException e) {
                            if (e == null) {
                                for (MyActivity ac : object) {
                                    if (bmobUser != null) {
                                        String acTag[] = ac.getTag();
                                        String tag[] = bmobUser.getTag();
                                        int ArrayLength = acTag.length;
                                        if (Arrays.toString(tag).contains(acTag[0]) || Arrays.toString(tag).contains(acTag[ArrayLength - 1])) {
                                            acList.add(ac);
                                        }
                                    } else {
                                        acList.add(ac);

                                    }


                                }


                                Collections.reverse(acList); // 倒序排列
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                List<MyActivity> list = acList;
                                intent.putExtra("LISTDATA", (Serializable) list);
                                startActivity(intent);
                                finish();
                                //Toast.makeText(SplashActivity.this,"成功接收内容",Toast.LENGTH_SHORT).show();

                            } else {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                List<MyActivity> list = new ArrayList<>();
                                intent.putExtra("LISTDATA", (Serializable) list);
                                Toast.makeText(SplashActivity.this, "网络不佳", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



            }
        });





    }


   static private boolean ifshow(){
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                ifshow = true;
            }
        };
        mHandler.sendEmptyMessageDelayed(0, 120000);
        return ifshow;
    }
}
