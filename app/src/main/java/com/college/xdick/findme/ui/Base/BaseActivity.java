package com.college.xdick.findme.ui.Base;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.college.xdick.findme.MyClass.BackHandlerHelper;
import com.college.xdick.findme.MyClass.Screensaver;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.InterestActivity;
import com.college.xdick.findme.ui.Activity.LoginActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Activity.ModifyNameActivity;
import com.college.xdick.findme.ui.Activity.SignupActivity;
import com.college.xdick.findme.ui.Activity.SplashActivity;
import com.college.xdick.findme.util.AppManager;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public abstract class BaseActivity extends AppCompatActivity /*implements Screensaver.OnTimeOutListener*/ {
    private ProgressDialog dialog=null;
    private AlertDialog.Builder builder=null ;
    private  AlertDialog dialog2=null;
    static protected long bmobTime;
    static int pushNum=0;

   // static private Screensaver mScreensaver;
   // static private boolean TIMEOUT=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    /*    TIMEOUT=false;

            mScreensaver = new Screensaver(1000*10); //定时5秒
            mScreensaver.setOnTimeOutListener(this); //监听
            mScreensaver.start(); //开始计时*/



        AppManager.getAppManager().addActivity(this);
        if (!(this instanceof MainActivity)&&!(this instanceof ModifyNameActivity)
        &&!(this instanceof InterestActivity)
                &&!(this instanceof LoginActivity)
                &&!(this instanceof SignupActivity)){

            //将Activity实例添加到AppManager的堆栈

            if (bmobTime==0){
                AppManager.getAppManager().finishAllActivity();
               startActivity(new Intent(this, MainActivity.class));
            }




            if (BmobIM.getInstance().getCurrentStatus().getMsg().equals("disconnect")){
                IMconnectBomob();
            }

            BmobIM.getInstance().setOnConnectStatusChangeListener(new ConnectStatusChangeListener() {
                @Override
                public void onChange(ConnectionStatus status) {
                    if (getBaseContext()==null){
                        return;
                    }

                    if (BmobUser.getCurrentUser()!=null){
                        if (status.getMsg().equals("connected")) {
                            try {
                                if (dialog!=null){
                                    dialog.dismiss();}
                                if (dialog2!=null){
                                    dialog2.dismiss();}

                            }catch (Exception e1){
                                e1.printStackTrace();
                            }


                        }
                        else if (status.getMsg().equals("connecting")) {


                            try {
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

                            }catch (Exception e1){
                                e1.printStackTrace();
                            }

                        }



                        else {

                            IMconnectBomob();


                            try {
                                if (dialog!=null){
                                    dialog.dismiss();
                                }

                                if (dialog2==null){
                                    initDialog2();
                                }
                                dialog2.show();


                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                    }
                    // Toast.makeText(getBaseContext(),status.getMsg(),Toast.LENGTH_SHORT).show();


                }
            });
        }


    }


    @Override
    protected void onDestroy() {

       // TIMEOUT=false;
        if (!this.getClass().getName().equals("MainActivity")){

            //将Activity实例从AppManager的堆栈中移除
            AppManager.getAppManager().finishActivity(this);
        }
        //mScreensaver.stop(); //停止计时
        super.onDestroy();

    }

    private void initDialog1() {
        dialog = new ProgressDialog(AppManager.getAppManager().currentActivity());
        dialog.setMessage("正在连接服务器...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


    }

    private void initDialog2() {
        builder = new AlertDialog.Builder(AppManager.getAppManager().currentActivity());
        builder.setTitle("连接断开");
        builder.setMessage("你可以点击重试重新连接");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog2.dismiss();
                startActivity(new Intent(AppManager.getAppManager().currentActivity(),MainActivity.class));
                finish();
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
                          try {
                              if (dialog!=null){
                                  dialog.dismiss();
                              }

                              if (dialog2==null){
                                  initDialog2();
                              }
                              dialog2.show();

                          }catch (Exception e1){
                              e1.printStackTrace();
                          }
                            //连接失败



                            //Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {

        if (BmobIM.getInstance().getCurrentStatus().getMsg()
                .equals("disconnected")){
            IMconnectBomob();
        }
        else if (BmobIM.getInstance().getCurrentStatus().getMsg()
                .equals("connected"))
        {  try {   if (dialog!=null){
            dialog.dismiss();}
            if (dialog2!=null){
                dialog2.dismiss();}

        }catch (Exception e){
            e.printStackTrace();
        }

        }

     /*   if (TIMEOUT)
        {
            TIMEOUT=false;
            Intent intent=new Intent(this, SplashActivity.class);
            String className = this.getLocalClassName();
            intent.putExtra("CLASS",className);
            Log.d("aaaa",this.getLocalClassName());
            startActivity(intent);
            mScreensaver.start();
        }*/
        super.onResume();
    }



    @Override
    protected void onStart() {
        pushNum=0;
        if (!this.getClass().getName().equals("MainActivity")){
            if (BmobIM.getInstance().getCurrentStatus().getMsg()
                    .equals("disconnected")){
                IMconnectBomob();
            }
        }


        super.onStart();

    }
    public long getBmobTime(){
         return bmobTime;
    }

    public void setBmobTime(long bmobTime1){
       bmobTime=bmobTime1;
    }
  /*  *//**
     * 当触摸就会执行此方法
     *//*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScreensaver.resetTime(); //重置时间
        TIMEOUT=false;
        return super.dispatchTouchEvent(ev);
    }

  *//*  *//**//**
     * 当使用键盘就会执行此方法
     *//*
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        mScreensaver.resetTime(); //重置时间
         TIMEOUT=false;
        return super.dispatchKeyEvent(event);

    }

    *//**
     * 时间到就会执行此方法
     *//*
    @Override
    public void onTimeOut(Screensaver screensaver) {

        TIMEOUT=true;
        mScreensaver.stop();

    }*/


    @Override
    public void onBackPressed() {
        //TIMEOUT=false;
        super.onBackPressed();
    }
}



