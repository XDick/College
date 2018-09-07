package com.college.xdick.findme.BmobIM;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.college.xdick.findme.Broadcast.NetWorkChangReceiver;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.MainActivity;

import org.litepal.LitePalApplication;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.newim.listener.ConnectStatusChangeListener;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

//TODO 集成：1.7、自定义Application，并在AndroidManifest.xml中配置
public class BmobIMApplication extends Application {
    private NetWorkChangReceiver netWorkChangReceiver;
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

        LitePalApplication.initialize(this);
        setInstance(this);
        Connector.getDatabase();
        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            Bmob.initialize(this, "b689cf6ecc75e3fafd3588b88ede6fcc");
            //Bmob 服务器的初始化也放在Application
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new MyMessageHandler(this));


            //推送服务
            BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
                @Override
                public void done(BmobInstallation bmobInstallation, BmobException e) {
                    if (e == null) {
                        Log.i("成功",bmobInstallation.getObjectId() + "-" + bmobInstallation.getInstallationId());
                    } else {
                        Log.e("失败",e.getMessage());
                    }
                }
            });
// 启动推送服务
            BmobPush.startWork(this);


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









}