package com.college.xdick.findme.IM_util;

import android.app.Application;
import android.text.TextUtils;

import com.college.xdick.findme.bean.MyUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

//TODO 集成：1.7、自定义Application，并在AndroidManifest.xml中配置
public class BmobIMApplication extends Application {

    private static BmobIMApplication INSTANCE;

    public static BmobIMApplication INSTANCE() {
        return INSTANCE;
    }

    private void setInstance(BmobIMApplication app) {
        setBmobIMApplication(app);
    }

    private static void setBmobIMApplication(BmobIMApplication a) {
        BmobIMApplication.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO 集成：1.8、初始化IM SDK，并注册消息接收器
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            Bmob.initialize(this, "b689cf6ecc75e3fafd3588b88ede6fcc");
            //Bmob 服务器的初始化也放在Application
            BmobIM.init(this);
            IMconnectBomob();
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this));
        }
    }

    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private void IMconnectBomob() {

        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        MyUser bmobUser = BmobUser.getCurrentUser(MyUser.class);
        if (bmobUser != null) {
            if (!TextUtils.isEmpty(bmobUser.getObjectId())) {
                BmobIM.connect(bmobUser.getObjectId(), new ConnectListener() {
                    @Override
                    public void done(String uid, BmobException e) {
                        if (e == null) {
                            //Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            //连接成功
                        } else {
                            //连接失败
                            //Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }



    }

}