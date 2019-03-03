package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/5/3.
 */

public class DynamicsComment extends BmobObject {


    private MyUser user,replyUser;
    private String content;
    private Dynamics dynamics;
    private DynamicsComment replyComment;
    private Integer replyNum;


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

    public Dynamics getDynamics() {
        return dynamics;
    }

    public void setDynamics(Dynamics dynamics) {
        this.dynamics = dynamics;
    }

    public void setReplyNum(Integer replyNum) {
        this.replyNum = replyNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public DynamicsComment getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(DynamicsComment replyComment) {
        this.replyComment = replyComment;
    }

    public Integer getReplyNum() {
        return replyNum;
    }
}
