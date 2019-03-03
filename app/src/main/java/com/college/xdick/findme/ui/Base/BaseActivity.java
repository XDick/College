package com.college.xdick.findme.ui.Base;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.util.AppManager;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

public abstract class BaseActivity extends AppCompatActivity {
    private ProgressDialog dialog=null;
    private AlertDialog.Builder builder=null ;
    private  AlertDialog dialog2=null;
    static private long bmobTime=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将Activity实例添加到AppManager的堆栈
        AppManager.getAppManager().addActivity(this);


        if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.DISCONNECT)){
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将Activity实例从AppManager的堆栈中移除
        AppManager.getAppManager().finishActivity(this);
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
        if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.DISCONNECT)){
            IMconnectBomob();
        }
        else if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.CONNECTED))
        {  try {   if (dialog!=null){
            dialog.dismiss();}
            if (dialog2!=null){
                dialog2.dismiss();}

        }catch (Exception e){
            e.printStackTrace();
        }

        }
        super.onResume();
    }


    @Override
    protected void onStart() {

        if (BmobIM.getInstance().getCurrentStatus().equals(ConnectionStatus.DISCONNECT)){
            IMconnectBomob();
        }
        super.onStart();

    }
}