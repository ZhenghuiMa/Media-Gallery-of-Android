package com.derekma.videogallery;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by derekma on 16/1/31.
 * Static class.
 * Hold Image, Title and Information.
 */
public class ViewHolder {

    /**
     * A ImageView.
     * Provide image information.
     */
    public ImageView img;

    /**
     * A TextView.
     * Provide a TextView for video title.
     */
    public TextView title;

    /**
     * A TextView.
     * Provide a TextView for video information.
     */
    public TextView info;

    /**
     * A RatingBar
     * Provide Rating function
     */
    public RatingBar rating;
}