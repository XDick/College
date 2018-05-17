package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/5/9.
 */

public class MainTagBean extends BmobObject {

    String mainTag;




    public MainTagBean(String mainTag) {
        this.mainTag = mainTag;
    }


    public MainTagBean() {

    }



    public String getMainTag() {
        return mainTag;
    }

    public void setMainTag(String mainTag) {
        this.mainTag = mainTag;
    }


}
