package com.college.xdick.findme.MyClass;

import com.bumptech.glide.load.model.GlideUrl;

public class mGlideUrl extends GlideUrl {

    private String mUrl;

    // 构造函数里 传入 带有token参数的图片Url地址
    public mGlideUrl(String url) {
        super(url);
        mUrl = url;
    }

    // 2. 重写getCacheKey()
    @Override
    public String getCacheKey() {
        return mUrl.replace(deleteToken(), "");
        // 通过 deleteToken() 从 带有token参数的图片Url地址中 去掉 token参数
        // 最终返回一个没有token参数、初始的图片URL地址
        // ->>分析1
    }

    // 分析1：deleteToken()
    private String deleteToken() {
        String tokenParam = "";
        int tokenKeyIndex = mUrl.indexOf("?token=") >= 0 ? mUrl.indexOf("?token=") : mUrl.indexOf("&token=");
        if (tokenKeyIndex != -1) {
            int nextAndIndex = mUrl.indexOf("&", tokenKeyIndex + 1);
            if (nextAndIndex != -1) {
                tokenParam = mUrl.substring(tokenKeyIndex + 1, nextAndIndex + 1);
            } else {
                tokenParam = mUrl.substring(tokenKeyIndex);
            }
        }
        return tokenParam;
    }

}
