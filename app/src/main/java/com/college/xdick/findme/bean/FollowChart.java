package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

public class FollowChart extends BmobObject {

    String hostId;
    Integer fansCount;
    String userName;



    public Integer getFansCount() {
        return fansCount;
    }

    public void setFansCount(Integer fansCount) {
        this.fansCount = fansCount;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
