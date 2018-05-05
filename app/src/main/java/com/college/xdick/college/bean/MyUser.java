package com.college.xdick.college.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2018/4/2.
 */

public class MyUser extends BmobUser {

    private String avatar ="http://bmob-cdn-18038.b0.upaiyun.com/2018/04/11/0552cec7404930c580071f80377f71d7.png";

    private String school;

    private Boolean ifConfirm = false;

    private String[] like;

    private String[] join;

    public Boolean getIfConfirm() {
        return ifConfirm;
    }

    public void setIfConfirm(Boolean ifConfirm) {
        this.ifConfirm = ifConfirm;
    }

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
}
