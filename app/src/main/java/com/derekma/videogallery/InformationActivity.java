package com.derekma.videogallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A class provides developer's information.
 */
public class InformationActivity extends AppCompatActivity {

    private String[] titles=new String[]{
            "Application Name",
            "Developer",
            "Overview",
            "Features",
            "References"
    };

    private String[] values = new String[]{
            "Video Gallery V2",
            "Zhenghui Ma",
            "Video Gallery is a media application designed to show how to use WebView and YouTube SDK Player in Android." +
                    "It also provides how to use internal database and external database and use timestamp to synchronize." +
                    "Using GCM to send notification from server side and recommend user to play video from message.",
            "1) Use ViewPager and Fragments to improve interaction<br>" +
                    "2) Connect to External Database<br>" +
                    "3) Use GCM to send notification to application<br>"+
                    "4) Build settings for application to control data update and play type<br>"+
                    "5) Build a information system to manager recommendation data",
            "1) <a href=\"http://developer.android.com/guide/topics/ui/settings.html\">Android Settings</a><br>"+
            "2) <a href=\"https://developers.google.com/cloud-messaging/\">Google Cloud Messaging</a>"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout parent = (LinearLayout)findViewById(R.id.scrollParent);
        for(int i=0; i<titles.length; i++){

            TextView title = new TextView(this);
            title.setText(Html.fromHtml("<b>" + titles[i] + "</b>"));
            title.setTextSize(16);
            parent.addView(title);

            TextView content = new TextView(this);
            content.setText(Html.fromHtml(values[i]+"<br>"));
            content.setMovementMethod(LinkMovementMethod.getInstance());
            parent.addView(content);
        }
    }
}
