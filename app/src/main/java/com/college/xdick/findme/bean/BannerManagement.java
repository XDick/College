package com.college.xdick.findme.bean;

import cn.bmob.v3.BmobObject;

public class BannerManagement extends BmobObject {
    private String type;
    private String coverUri;
    private String webUri;
    private String webTitle;
    private MyActivity activity;


    public BannerManagement( String type, String coverUri, String webUri, String webTitle, MyActivity activity) {
        this.type = type;
        this.coverUri = coverUri;
        this.webUri = webUri;
        this.webTitle = webTitle;
        this.activity = activity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(String coverUri) {
        this.coverUri = coverUri;
    }

    public String getWebUri() {
        return webUri;
    }

    public void setWebUri(String webUri) {
        this.webUri = webUri;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    public MyActivity getActivity() {
        return activity;
    }

    public void setActivity(MyActivity activity) {
        this.activity = activity;
    }
}
