package com.derekma.videogallery;

/**
 * Created by derekma on 16/2/1.
 * A class provides API key and urls for downloading data from API services.
 */
public final class Config {

    private Config() {

    }

    /**
     * A static final variable.
     * Youtube API Key.
     */
    public static final String YOUTUBE_API_KEY = "AIzaSyAKf2_-ilhM-6lEQAo7BiCcss-rGr1IbO0";

    /**
     * A static final variable.
     * YouTube popular videos API.
     */
    public static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet&chart=mostPopular&maxResults=10&key=AIzaSyBGpMMSB8Qii7UMDCSQ-PCOPV1sB-E3K1g";

    /**
     * A static final variable.
     * NPR news latest news API.
     */
    public static final String NPR_NEWS_API = "http://api.npr.org/query?fields=title,storyDate,image,titles&requiredAssets=text,image&title=npr_news&dateType=story&sort=dateDesc&output=JSON&numResults=10&apiKey=MDIyNzUxMDU2MDE0NTU0MTE2NDM5Y2UyYw000";
}