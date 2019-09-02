package com.college.xdick.findme.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;

public class Goods extends BmobObject implements Serializable {
    String title;
    double price;
    double originalPrice;
    String cover;
    int saleNumber;
    String[] picture;
    String content;
    String payPicture;
     String[] tag;
     MyActivity activity;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(int saleNumber) {
        this.saleNumber = saleNumber;
    }

    public String[] getPicture() {
        return picture;
    }

    public void setPicture(String[] picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPayPicture() {
        return payPicture;
    }

    public void setPayPicture(String payPicture) {
        this.payPicture = payPicture;
    }

    public String[] getTag() {
        return tag;
    }

    public void setTag(String[] tag) {
        this.tag = tag;
    }

    public MyActivity getActivity() {
        return activity;
    }

    public void setActivity(MyActivity activity) {
        this.activity = activity;
    }
}
