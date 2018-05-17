package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2018/5/9.
 */

public class AddTagBean extends BmobObject implements Comparable<AddTagBean> {

    String addTag;

    String providerName;

    int useCount=0;


    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }


    public void UserCountAdd1(){
        useCount++;
    }

    public AddTagBean(String addTag) {
        this.addTag = addTag;
    }

    public AddTagBean() {

    }

    public String getAddTag() {
        return addTag;
    }

    public void setAddTag(String addTag) {
        this.addTag = addTag;
    }

    @Override
     public int compareTo(AddTagBean tag) {
                 //自定义比较方法，如果认为此实体本身大则返回1，否则返回-1
               if(this.useCount >= tag.getUseCount()){
                        return 1;
                     }
                 return -1;
            }

}
