package com.college.xdick.findme.BmobIM.newClass;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.college.xdick.findme.BmobIM.BmobIMApplication;
import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.R;
import com.college.xdick.findme.bean.MyActivity;
import com.college.xdick.findme.ui.Activity.ActivityActivity;
import com.college.xdick.findme.ui.Activity.MainActivity;
import com.college.xdick.findme.ui.Activity.WebActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

import static com.bumptech.glide.request.RequestOptions.diskCacheStrategyOf;

public class MyPushMessageReceiver extends BroadcastReceiver {
    private static final int PUSH_NOTIFICATION_ID_SYSTEM = (0x003);
    private static final String PUSH_CHANNEL_ID = "PUSH_NOTIFY_ID";
    private static final String PUSH_CHANNEL_NAME = "PUSH_NOTIFY_NAME";
    private Intent intent2=new Intent() ;
    private String content = null;
    private String url=null;
    private String type=null;
    private String title=null;
    private String cover=null;
    private Bitmap  icon= null;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){

            String jsonStr = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

            try {
                // 处理JSON
                JSONObject jsonObject = new JSONObject(jsonStr);
                content = jsonObject.getString("alert");
                url=jsonObject.getString("url");
                type=jsonObject.getString("type");
                title=jsonObject.getString("title");
                cover=jsonObject.getString("cover");
            } catch (JSONException e) {
                e.printStackTrace();
            }

           FutureTarget<Bitmap> bitmap = Glide.with(context)
                    .asBitmap()
                   .apply(diskCacheStrategyOf(DiskCacheStrategy.RESOURCE))
                    .load(R.drawable.batman)
                    .submit();

            try {
              icon = Glide.with(context)
                        .asBitmap() //必须
                        .load(cover)
                        .apply(new RequestOptions()
                                .centerCrop())
                        .into(500, 500)
                        .get();

            }
            catch (Exception e){
                e.printStackTrace();
            }


            try{
                icon = bitmap.get();
            }catch (Exception e1){
                e1.printStackTrace();
            }
            if (!type.equals("activity")){
                if (type==null){
                    intent2 = new Intent(context, MainActivity.class);
                }
                else if (type.equals("web")){
                    intent2 = new Intent(context, WebActivity.class);
                    intent2.putExtra("URL",url);
                }
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pi = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    if (notificationManager != null) {
                        notificationManager.createNotificationChannel(channel);
                    }
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context,PUSH_CHANNEL_ID);
                builder
                        .setContentIntent(pi) //设置通知栏点击意图
                        .setSmallIcon(R.drawable.findme)
                        .setContentTitle(title==null?"收到一条推送":title)
                        .setContentText(content)
                        .setTicker(content) //通知首次出现在通知栏，带上升动画效果的
                        .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                        .setChannelId(PUSH_CHANNEL_ID)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.findme)
                        .setLargeIcon(icon)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setDefaults(Notification.DEFAULT_VIBRATE)
                        // 设置该通知优先级
                        .setPriority(Notification.PRIORITY_MAX);

                Notification notification = builder.build();
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                if (notificationManager != null) {
                    notificationManager.notify(PUSH_NOTIFICATION_ID_SYSTEM, notification);
                }


            }

            else {
                 intent2 = new Intent(context, ActivityActivity.class);
                BmobQuery<MyActivity> query =new BmobQuery<>();
                query.include("host[avatar|username]");
                query.getObject(url, new QueryListener<MyActivity>() {
                    @Override
                    public void done(MyActivity myActivity, BmobException e) {
                        if (e==null){
                            intent2.putExtra("ACTIVITY",myActivity);
                            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            PendingIntent pi = PendingIntent.getActivity(context, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel channel = new NotificationChannel(PUSH_CHANNEL_ID, PUSH_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                                if (notificationManager != null) {
                                    notificationManager.createNotificationChannel(channel);
                                }
                            }

                            NotificationCompat.Builder builder = new NotificationCompat.Builder(context,PUSH_CHANNEL_ID);
                            builder
                                    .setContentIntent(pi) //设置通知栏点击意图
                                    .setSmallIcon(R.drawable.findme)
                                    .setContentTitle(title==null?"收到一条推送":title)
                                    .setContentText(content)
                                    .setTicker(content) //通知首次出现在通知栏，带上升动画效果的
                                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                                    .setChannelId(PUSH_CHANNEL_ID)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setSmallIcon(R.drawable.findme)
                                    .setLargeIcon(icon)
                                    .setDefaults(Notification.DEFAULT_SOUND)
                                    .setDefaults(Notification.DEFAULT_LIGHTS)
                                    .setDefaults(Notification.DEFAULT_VIBRATE)
                                    // 设置该通知优先级
                                    .setPriority(Notification.PRIORITY_MAX);

                            Notification notification = builder.build();
                            notification.flags |= Notification.FLAG_AUTO_CANCEL;
                            if (notificationManager != null) {
                                notificationManager.notify(PUSH_NOTIFICATION_ID_SYSTEM, notification);
                            }


                        }
                    }
                });
            }



          //  Log.d("bmob", "客户端收到推送内容："+intent.getStringExtra("msg"));

        }
    }

}