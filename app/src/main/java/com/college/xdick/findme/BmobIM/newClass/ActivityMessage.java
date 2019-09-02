package com.college.xdick.findme.BmobIM.newClass;

import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;

import com.college.xdick.findme.bean.ActivityMessageBean;

import org.json.JSONObject;

import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;

/**
 * Created by Administrator on 2018/5/22.
 */

public class ActivityMessage extends BmobIMExtraMessage {



    @Override
    public String getMsgType() {
        //自定义一个`join`的消息类型
        return "join";
    }

    @Override
    public boolean isTransient() {
        return true;
    }



    public static ActivityMessageBean convert(BmobIMMessage msg) {
        ActivityMessageBean acmsg = new ActivityMessageBean();
        String content = msg.getContent();
        acmsg.setContent(content);
        acmsg.setTime(String.valueOf(msg.getCreateTime()));
        try {
            String extra = msg.getExtra();
            if (!TextUtils.isEmpty(extra)) {
                JSONObject json = new JSONObject(extra);
                String username = json.getString("username");
                acmsg.setUsername(username);
                String useravatar = json.getString("useravatar");
                acmsg.setUserAvatar(useravatar);
                String userid = json.getString("userid");
                acmsg.setUserId(userid);
                String currentuser = json.getString("currentuser");
                acmsg.setCurrentuserId(currentuser);
                String activityid = json.getString("activityid");
                acmsg.setActivityId(activityid);
                String activityname = json.getString("activityname");
                acmsg.setActivityname(activityname);
                String objectType = json.getString("type");
                acmsg.setType(objectType);

            } else {
                Log.d("","AddFriendMessage的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return acmsg;
    }


}
