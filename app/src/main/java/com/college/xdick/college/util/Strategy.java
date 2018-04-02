package com.college.xdick.college.util;

/**
 * Created by Administrator on 2018/4/2.
 */

public class Strategy {

    private String ContentImageUri;
    private String CoverImageUri;
    private String Name;
    private String Content;

    public Strategy(String contentImageUri, String coverImageUri, String name, String content) {
        ContentImageUri = contentImageUri;
        CoverImageUri = coverImageUri;
        Name = name;
        Content = content;
    }

    public String getContentImageUri() {
        return ContentImageUri;
    }

    public String getCoverImageUri() {
        return CoverImageUri;
    }

    public String getName() {
        return Name;
    }

    public String getContent() {
        return Content;
    }

    public void setContentImageUri(String contentImageUri) {
        ContentImageUri = contentImageUri;
    }

    public void setCoverImageUri(String coverImageUri) {
        CoverImageUri = coverImageUri;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setContent(String content) {
        Content = content;
    }
}
