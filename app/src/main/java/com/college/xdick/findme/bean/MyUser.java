package com.college.xdick.findme.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/2.
 */

public class MyUser extends BmobUser implements Serializable {



    private String avatar ="http://bmob-cdn-18038.b0.upaiyun.com/2018/05/18/425ce45f40a6b2208074aa1dbce9f76c.png";

    private String school="";

    private String[] gps;

    private String[] setAc;

    private String[] like;

    private String[] join;

    private String[] tag;

    private String setAcTime;

    private String[] dynamics;

    private String[] following;

    private String[] fans;





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

    public String[] getSetAc() {
        return setAc;
    }

    public void setSetAc(String[] setAc) {
        this.setAc = setAc;
    }

    public String[] getDynamics() {
        return dynamics;
    }

    public void setDynamics(String[] dynamics) {
        this.dynamics = dynamics;
    }


    public String[] getFollowing() {
        return following;
    }

    public void setFollowing(String[] following) {
        this.following = following;
    }

    public String[] getFans() {
        return fans;
    }

    public void setFans(String[] fans) {
        this.fans = fans;
    }
}

