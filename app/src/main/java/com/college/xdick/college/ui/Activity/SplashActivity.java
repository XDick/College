package com.college.xdick.college.ui.Activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.college.xdick.college.R;

import com.college.xdick.college.bean.MyActivity;


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
   private List<MyActivity> acList= new ArrayList<>();



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
        BmobQuery<MyActivity> query = new BmobQuery<MyActivity>();
//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(99);
//执行查询方法
        query.findObjects(new FindListener<MyActivity>() {
            @Override
            public void done(List<MyActivity> object, BmobException e) {
                if(e==null){
                    for (MyActivity ac : object) {
                        acList.add(ac);}
                    Collections.reverse(acList); // 倒序排列
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    List<MyActivity> list = acList;
                    intent.putExtra("LISTDATA",(Serializable) list);
                    startActivity(intent);
                    finish();
                    //Toast.makeText(SplashActivity.this,"成功接收内容",Toast.LENGTH_SHORT).show();

                }else{
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
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
