package com.derekma.videogallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * This class extends AppCompatActivity implement WebView to display NPR news.
 */
public class NewsWebActivity extends AppCompatActivity {

    String url = "http://developer.android.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_web);

        Intent i = getIntent();
        url = i.getStringExtra("newsUrl");

        WebView newsWeb = (WebView) findViewById(R.id.newsWebView);
        newsWeb.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = newsWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        newsWeb.loadUrl(url);
        newsWeb.canGoBack();
        //newsWeb.setWebChromeClient(new WebChromeClient() {});

    }
}
