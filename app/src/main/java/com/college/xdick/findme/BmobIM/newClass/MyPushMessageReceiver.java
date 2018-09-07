package com.college.xdick.findme.BmobIM.newClass;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.college.xdick.findme.MyClass.ReadEvent;
import com.college.xdick.findme.R;
import com.college.xdick.findme.ui.Activity.MainActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){

            String jsonStr = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            String content = null;
            try {
                // 处理JSON
                JSONObject jsonObject = new JSONObject(jsonStr);
                content = jsonObject.getString("alert");
            } catch (JSONException e) {
                e.printStackTrace();
            }
   /*         Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent2, 0);*/

            NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
            Notification notify = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.findme)
                    .setContentTitle("收到一条推送")
                    .setContentText(content)
                  //  .setContentIntent(pi)
                    .build();
            manager.notify(1, notify);

            Log.d("bmob", "客户端收到推送内容："+intent.getStringExtra("msg"));

        }
    }

}