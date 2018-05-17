package com.college.xdick.findme.ui.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.college.xdick.findme.R;

/**
 * Created by Administrator on 2018/5/6.
 */

public class NewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        String url=getIntent().getStringExtra("URL");
        WebView webView =findViewById(R.id.news_webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        //支持屏幕缩放
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        settings.setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }


}
