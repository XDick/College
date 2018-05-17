package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class Friend extends BmobObject {
    //用户
    private MyUser user;
    //好友
    private MyUser friendUser;

    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public MyUser getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(MyUser friendUser) {
        this.friendUser = friendUser;
    }
}
