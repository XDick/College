package com.college.xdick.findme.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/4/11.
 */

public class MyActivity extends BmobObject implements Serializable ,Comparable<MyActivity>{

    private String title;
    private String hostSchool;
    private  String time;
    private String endTime;
    private String place;
    private  String content;
    private   String cover;
    private  String[] gps;
    private  String[]joinUser;
    private  String[] picture;
    private String[] tag;
    private Integer commentCount;
    private MyUser host;
    private long date;
    private long endDate;
    private String[] editTime;
    private String[] editContent;

    public String[] getGps() {
        return gps;
    }

    public void setGps(String[] gps) {
        this.gps = gps;
    }



    public String[] getTag() {
        return tag;
    }

    public void setTag(String[] tag) {
        this.tag = tag;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }



    public String getHostSchool() {
        return hostSchool;
    }

    public void setHostSchool(String hostSchool) {
        this.hostSchool = hostSchool;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String[] getJoinUser() {
        return joinUser;
    }

    public void setJoinUser(String[] joinUser) {
        this.joinUser = joinUser;
    }


    public String[] getPicture() {
        return picture;
    }

    public void setPicture(String[] picture) {
        this.picture = picture;
    }


    public MyUser getHost() {
        return host;
    }

    public void setHost(MyUser host) {
        this.host = host;
    }

    @Override
    public int compareTo(MyActivity activity) {
        //自定义比较方法，如果认为此实体本身大则返回1，否则返回-1
        if(this.date <= activity.getDate()){
            return 1;
        }
        return -1;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String[] getEditTime() {
        return editTime;
    }

    public void setEditTime(String[] editTime) {
        this.editTime = editTime;
    }

    public String[] getEditContent() {
        return editContent;
    }

    public void setEditContent(String[] editContent) {
        this.editContent = editContent;
    }
}
