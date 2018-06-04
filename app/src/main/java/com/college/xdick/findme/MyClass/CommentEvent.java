package com.college.xdick.findme.MyClass;

/**
 * Created by Administrator on 2018/5/21.
 */

public class CommentEvent {

    public String comment;
    public String towhich;
    public String fromWho;
    public  String  userId;

    public CommentEvent(String comment,String towhich,String fromWho,String userId) {
        this.comment = comment;
        this.fromWho = fromWho;
        this.towhich= towhich;
        this.userId = userId;
    }
}
