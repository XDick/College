package com.college.xdick.college.IM_util;

import com.college.xdick.college.util.User;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/4/4 0004.
 */

public class Friend extends BmobObject {
    //用户
    private User user;
    //好友
    private User friendUser;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(User friendUser) {
        this.friendUser = friendUser;
    }
}
