package com.college.xdick.findme.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/5/7.
 */

public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback)

    {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);

    }
}
