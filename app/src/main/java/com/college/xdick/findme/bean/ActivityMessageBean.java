package com.college.xdick.findme.bean;

import android.webkit.DateSorter;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2018/5/22.
 */

public class ActivityMessageBean extends DataSupport implements Comparable< ActivityMessageBean >{

    String userId;
    String username;
    String userAvatar;
    String content;
    String activityname;
    String activityId;
    String currentuserId;
    String time;
    String ifCheck ="false";
    String type;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getActivityname() {
        return activityname;
    }

    public void setActivityname(String activityname) {
        this.activityname = activityname;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getCurrentuserId() {
        return currentuserId;
    }

    public void setCurrentuserId(String currentuserId) {
        this.currentuserId = currentuserId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIfCheck() {
        return ifCheck;
    }

    public void setIfCheck(String ifCheck) {
        this.ifCheck = ifCheck;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int compareTo(ActivityMessageBean bean) {
        //自定义比较方法，如果认为此实体本身大则返回1，否则返回-1
        if(Long.valueOf(this.time) <= Long.valueOf(bean.getTime())){
            return 1;
        }
        return -1;
    }


}
