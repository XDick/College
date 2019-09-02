package com.college.xdick.findme.BmobIM;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.college.xdick.findme.BmobIM.newClass.ActivityMessage;
import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.ActivityMessageBean;
import com.college.xdick.findme.bean.MyUser;
import com.college.xdick.findme.ui.Activity.ChatActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.util.AppManager;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;


import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMMessageType;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.bmob.newim.listener.BmobIMMessageHandler;
import cn.bmob.newim.notification.BmobNotificationManager;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

//TODO 集成：1.6、自定义消息接收器处理在线消息和离线消息
public class MyMessageHandler extends BmobIMMessageHandler {

    private static final int PUSH_NOTIFICATION_ID = (0x001);
    private static final int PUSH_NOTIFICATION_ID_CHAT = (0x002);
    private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
    private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";
    private Context context;


    public MyMessageHandler(Context context) {
        this.context = context; }

   /* public MyMessageHandler() {
       }
*/

    @Override
    public void onMessageReceive(final MessageEvent event) {
          executeMessage(event);

    }

    @Override
    public void onOfflineReceive(final OfflineMessageEvent event) {
        //每次调用connect方法时会查询一次离线消息，如果有，此方法会被调用
        Map<String, List<MessageEvent>> map = event.getEventMap();
       // Logger.i("有" + map.size() + "个用户发来离线消息");
        //挨个检测下离线消息所属的用户的信息是否需要更新
        for (Map.Entry<String, List<MessageEvent>> entry : map.entrySet()) {
            List<MessageEvent> list = entry.getValue();
            int size = list.size();
           // Logger.i("用户" + entry.getKey() + "发来" + size + "条消息");
            for (int i = 0; i < size; i++) {
                //处理每条消息
                executeMessage(list.get(i));
                ((BmobIMApplication)context).clearPushNum();


            }
        }

    }

private void executeMessage(final MessageEvent event){


    UserModel.getInstance().updateUserInfo(event, new UpdateCacheListener() {
        @Override
        public void done(BmobException e) {


            FutureTarget<Bitmap> bitmap = Glide.with(context)
                    .asBitmap()
                    .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                    .load(event.getFromUserInfo().getAvatar())
                    .submit();

             Bitmap  icon= null;
            try{
                icon = bitmap.get();
            }catch (Exception e1){
                e1.printStackTrace();
            }
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel);
                }
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,PUSH_CHANNEL_ID);
            if (BmobIMMessageType.getMessageTypeValue(event.getMessage().getMsgType()) == 0) {
                String time =String.valueOf(event.getMessage().getCreateTime());
                ActivityMessageBean msg =ActivityMessage.convert(event.getMessage());

                List<ActivityMessageBean>list =
                        DataSupport.where("username=?and time=? and content=?",msg.getUsername(),msg.getTime(),msg.getContent())
                        .find(ActivityMessageBean.class);


                if (list.isEmpty()) {

                    ActivityMessageBean messageBean = new ActivityMessageBean();
                    messageBean.setTime(time);
                    messageBean.setActivityname(msg.getActivityname());
                    messageBean.setActivityId(msg.getActivityId());
                    messageBean.setCurrentuserId(msg.getCurrentuserId());
                    messageBean.setUserId(msg.getUserId());
                    messageBean.setUserAvatar(msg.getUserAvatar());
                    messageBean.setUsername(msg.getUsername());
                    messageBean.setContent(msg.getContent());
                    messageBean.setType(msg.getType());
                    messageBean.save();



                    builder
                            .setContentIntent(pi) //设置通知栏点击意图
                            .setContentTitle(msg.getActivityname())
                            .setContentText("["+((BmobIMApplication)context).addPushNum()
                                    +"条] "+ ( msg.getType().equals("notify")||(msg.getType().equals("notifyPic"))
                                    ?"[活动通知]":(event.getMessage().getContent())))
                            .setTicker(( msg.getType().equals("notify")||(msg.getType().equals("notifyPic"))
                                    ?"[活动通知]":(event.getMessage().getContent()))) //通知首次出现在通知栏，带上升动画效果的
                            .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                            .setChannelId(PUSH_CHANNEL_ID)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(context.getApplicationInfo().icon)
                            .setLargeIcon(icon)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setDefaults(Notification.DEFAULT_LIGHTS)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            // 设置该通知优先级
                            .setPriority(Notification.PRIORITY_MAX);

                    Notification notification = builder.build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    if (notificationManager != null) {
                        notificationManager.notify(PUSH_NOTIFICATION_ID, notification);
                    }


                    EventBus.getDefault().post(new ReadEvent(""));



                }
            } else {



                if (!(AppManager.getAppManager().currentActivity() instanceof ChatActivity)){
                    builder
                            .setContentIntent(pi) //设置通知栏点击意图
                            .setContentTitle(event.getFromUserInfo().getName())
                            .setContentText("["+((BmobIMApplication)context).addPushNum_chat()
                                    +"条] "+ (event.getMessage().getMsgType()
                                    .equals(BmobIMMessageType
                                            .IMAGE.getType())?"[图片]":event.getMessage().getContent()))
                            .setTicker( (event.getMessage().getMsgType()
                                    .equals(BmobIMMessageType
                                            .IMAGE.getType())?"[图片]":event.getMessage().getContent()))//通知首次出现在通知栏，带上升动画效果的
                            .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                            .setChannelId(PUSH_CHANNEL_ID)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(context.getApplicationInfo().icon)
                            .setLargeIcon(icon)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setDefaults(Notification.DEFAULT_LIGHTS)
                            .setDefaults(Notification.DEFAULT_VIBRATE)
                            // 设置该通知优先级
                            .setPriority(Notification.PRIORITY_MAX);

                    Notification notification = builder.build();
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                if (notificationManager != null) {
                    notificationManager.notify(PUSH_NOTIFICATION_ID_CHAT, notification);
                }
                }else {
                   if (!((ChatActivity)AppManager.getAppManager()
                            .currentActivity()).isShown()){

                       builder
                               .setContentIntent(pi) //设置通知栏点击意图
                               .setContentTitle(event.getFromUserInfo().getName())
                               .setContentText("["+((BmobIMApplication)context).addPushNum_chat()
                                       +"条] "+ (event.getMessage().getMsgType()
                                       .equals(BmobIMMessageType
                                               .IMAGE.getType())?"[图片]":event.getMessage().getContent()))
                               .setTicker((event.getMessage().getMsgType()
                                       .equals(BmobIMMessageType
                                               .IMAGE.getType())?"[图片]":event.getMessage().getContent()))//通知首次出现在通知栏，带上升动画效果的
                               .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                               .setChannelId(PUSH_CHANNEL_ID)
                               .setDefaults(Notification.DEFAULT_ALL)
                               .setSmallIcon(context.getApplicationInfo().icon)
                               .setLargeIcon(icon)
                               .setDefaults(Notification.DEFAULT_SOUND)
                               .setDefaults(Notification.DEFAULT_LIGHTS)
                               .setDefaults(Notification.DEFAULT_VIBRATE)
                               // 设置该通知优先级
                               .setPriority(Notification.PRIORITY_MAX);

                       Notification notification = builder.build();
                       notification.flags |= Notification.FLAG_AUTO_CANCEL;
                       if (notificationManager != null) {

                           notificationManager.notify(PUSH_NOTIFICATION_ID_CHAT, notification);
                       }
                    }
                }
                EventBus.getDefault().post(new ReadEvent(""));

        } }
    });


}



}