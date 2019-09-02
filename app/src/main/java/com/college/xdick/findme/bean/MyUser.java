package com.college.xdick.findme.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2018/4/2.
 */

public class MyUser extends BmobUser implements Serializable {



    private String avatar ;

    private String school;

    private String[] gps;



    private String[] like;

    private String[] tag;

    private String setAcTime;


    private String[] following;


    private Boolean isGod;

    private Boolean isBanned;

    private Integer setAcCount;

    private Integer dynamicsCount;



    private String bannedReason;

    private Integer Exp;

    private String registerTime;




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






    public String[] getFollowing() {
        return following;
    }

    public void setFollowing(String[] following) {
        this.following = following;
    }


    public boolean isGod() {
        return isGod;
    }

    public void setGod(boolean god) {
        isGod = god;
    }

    public int getSetAcCount() {
        return setAcCount;
    }

    public void setSetAcCount(int setAcCount) {
        this.setAcCount = setAcCount;
    }

    public int getDynamicsCount() {
        return dynamicsCount;
    }

    public void setDynamicsCount(int dynamicsCount) {
        this.dynamicsCount = dynamicsCount;
    }



    public Boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
    }



    public String getBannedReason() {
        return bannedReason;
    }

    public void setBannedReason(String bannedReason) {
        this.bannedReason = bannedReason;
    }

    public Integer getExp() {
        return Exp;
    }

    public void setExp(Integer exp) {
        Exp = exp;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }
}

