package com.college.xdick.findme.bean;

/**
 * Created by Administrator on 2018/5/6.
 */

public class FindNews {


    String title;
    String pre;
    String pic;
    String content;


    public FindNews(String title, String pre, String pic, String content) {
        this.title = title;
        this.pre = pre;
        this.pic = pic;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPre() {
        return pre;
    }

    public void setPre(String pre) {
        this.pre = pre;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
