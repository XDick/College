package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/2.
 */

public class MyUser extends BmobUser {

    private String avatar ="http://bmob-cdn-18038.b0.upaiyun.com/2018/04/11/0552cec7404930c580071f80377f71d7.png";

    private String school="";

    private String[] gps;


    private String[] like;

    private String[] join;

    private String[] tag;

    private String setAcTime;

    MyUser[]fans;

    MyUser[] follow;







    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String[] getLike() {
        return like;
    }

    public void setLike(String[] like) {
        this.like = like;
    }

    public String[] getJoin() {
        return join;
    }

    public void setJoin(String[] join) {
        this.join = join;
    }

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

    public String getSetAcTime() {
        return setAcTime;
    }

    public void setSetAcTime(String setAcTime) {
        this.setAcTime = setAcTime;
    }


    public MyUser[] getFans() {
        return fans;
    }

    public void setFans(MyUser[] fans) {
        this.fans = fans;
    }

    public MyUser[] getFollow() {
        return follow;
    }

    public void setFollow(MyUser[] follow) {
        this.follow = follow;
    }
}

