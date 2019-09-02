package com.college.xdick.findme.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2018/4/2.
 */

public class Dynamics extends BmobObject implements Serializable {


    private MyActivity activity;


    MyUser  myUser;
    private String content;
    private String[] picture;
    private String[] like;
    private Integer replycount;
    private Integer likeCount ;

    private boolean ifAdd2Gallery;


    public MyUser getMyUser() {
        return myUser;
    }

    public void setMyUser(MyUser myUser) {
        this.myUser = myUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }




    public int getReplycount() {
        return replycount;
    }

    public void setReplycount(int replycount) {
        this.replycount = replycount;
    }

    public String[] getPicture() {
        return picture;
    }

    public void setPicture(String[] picture) {
        this.picture = picture;
    }

    public String[] getLike() {
        return like;
    }

    public void setLike(String[] like) {
        this.like = like;
    }


    public MyActivity getActivity() {
        return activity;
    }

    public void setActivity(MyActivity activity) {
        this.activity = activity;
    }

    public boolean isIfAdd2Gallery() {
        return ifAdd2Gallery;
    }

    public void setIfAdd2Gallery(boolean ifAdd2Gallery) {
        this.ifAdd2Gallery = ifAdd2Gallery;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
