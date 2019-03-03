package com.college.xdick.findme.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/5/9.
 */

public class MainTagBean extends BmobObject implements Serializable {

    String mainTag;
    String[] subTag;
    int order;



    public MainTagBean(String mainTag) {
        this.mainTag = mainTag;
    }





    public String getMainTag() {
        return mainTag;
    }

    public void setMainTag(String mainTag) {
        this.mainTag = mainTag;
    }

    public String[] getSubTag() {
        return subTag;
    }

    public void setSubTag(String[] subTag) {
        this.subTag = subTag;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
