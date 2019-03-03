package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/5/3.
 */

public class Comment extends BmobObject {


    private MyUser user,replyUser;
    private  String content;
    private  MyActivity activity;
    private  Comment replyComment;
    private  Integer replyNum;







    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public MyUser getUser() {
        return user;
    }

    public void setUser(MyUser user) {
        this.user = user;
    }

    public MyUser getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(MyUser replyUser) {
        this.replyUser = replyUser;
    }

    public MyActivity getActivity() {
        return activity;
    }

    public void setActivity(MyActivity activity) {
        this.activity = activity;
    }

    public Integer getReplyNum() {
        return replyNum;
    }

    public void setReplyNum(Integer replyNum) {
        this.replyNum = replyNum;
    }

    public Comment getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(Comment replyComment) {
        this.replyComment = replyComment;
    }

    public void addReply(){
        replyNum++;
    }
}
