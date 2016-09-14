package com.derekma.videogallery;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A class implement WebView.
 * Implement WebView in App.
 */
public class WebPlayerActivity extends AppCompatActivity {

    String videoId = "";
    String description = "";

    /**
     * Create WebView inside the WebPlayerActivity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_player);

        /**
         * Get the screen size of your device.
         */
        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);

        int widthWebView = size.x-50;

        Intent i = getIntent();
        videoId = i.getStringExtra("videoId");
        description = i.getStringExtra("description");

        /**
         * Implement html5 youtube player here.
         */
        String frameVideo = "<html><body>Popular Video From YouTube<br><iframe width=\""+widthWebView+"\" height=\"400\" src=\"https://www.youtube.com/embed/"+videoId+"\" frameborder=\"0\" allowfullscreen></iframe><br><h>Description:</h><p>"+description+"</p></body></html>";

        WebView displayYoutubeVideo = (WebView) findViewById(R.id.mWebView);
        displayYoutubeVideo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        WebSettings webSettings = displayYoutubeVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        displayYoutubeVideo.loadData(frameVideo, "text/html", "utf-8");
        displayYoutubeVideo.canGoBack();
        displayYoutubeVideo.setWebChromeClient(new WebChromeClient() {});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
