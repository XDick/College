package com.college.xdick.college.ui.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.college.xdick.college.R;
import com.college.xdick.college.bean.Dynamics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2018/4/4.
 */

public class SplashActivity extends Activity {
    static boolean ifshow = true;
    private static final int sleepTime = 2000;
   private List<Dynamics> dynamicsList= new ArrayList<>();



    @Override
    protected void onCreate(Bundle arg0) {
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);

        if(!ifshow()){
           startActivity(new Intent(SplashActivity.this,MainActivity.class));
           finish();
        }
        if(ifshow){
        initData();}

        super.onCreate(arg0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Thread(new Runnable() {
            public void run() {
                long start = System.currentTimeMillis();
                long costTime = System.currentTimeMillis() - start;
                //等待sleeptime时长
                if (sleepTime - costTime > 0) {
                    try {
                        Thread.sleep(sleepTime - costTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                ifshow= false;
                //进入主页面
               Intent intent = new Intent(SplashActivity.this,MainActivity.class);
               List<Dynamics> list = dynamicsList;
               intent.putExtra("LISTDATA",(Serializable) list);
                startActivity(intent);
                finish();
            }
        }).start();
    }


    private void initData(){
        BmobQuery<Dynamics> query = new BmobQuery<Dynamics>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(99);
//执行查询方法
        query.findObjects(new FindListener<Dynamics>() {
            @Override
            public void done(List<Dynamics> object, BmobException e) {
                if(e==null){
                    for (Dynamics dynamics : object) {
                        dynamicsList.add(dynamics);}
                    Collections.reverse(dynamicsList); // 倒序排列
                    //Toast.makeText(SplashActivity.this,"成功接收内容",Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(SplashActivity.this,"网络不佳",Toast.LENGTH_SHORT).show();
                }
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
