package com.college.xdick.college.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2018/4/2.
 */

public class MyUser extends BmobUser {

    private String avatar ="http://bmob-cdn-18038.b0.upaiyun.com/2018/04/11/0552cec7404930c580071f80377f71d7.png";

    private String school;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String head) {
        this.avatar = head;
    }
}
