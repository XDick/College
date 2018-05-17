package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/5/3.
 */

public class Comment extends BmobObject {


    String userName;
    String content;
    String ActivityID;
    String userID;
    String replyusername;
    String replyuserId;
    String replycontent;
    int replyNum=0;



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public int getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(int replyNum) {
        this.replyNum = replyNum;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getReplyusername() {
        return replyusername;
    }

    public void setReplyusername(String replyusername) {
        this.replyusername = replyusername;
    }

    public String getReplyuserId() {
        return replyuserId;
    }

    public void setReplyuserId(String replyuserId) {
        this.replyuserId = replyuserId;
    }

    public String getReplycontent() {
        return replycontent;
    }

    public void setReplycontent(String replycontent) {
        this.replycontent = replycontent;
    }

    public void addReply(){
        replyNum++;
    }
}
