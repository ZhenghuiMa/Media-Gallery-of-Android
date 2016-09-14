package com.derekma.videogallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Created by derekma on 16/2/20.
 * This class customizes an adapter to represent and update data in ListView.
 * In this class, RatingBar is a new widget added in each item
 * to provide rating action for user.
 */
public class NewsListViewAdapter extends BaseAdapter {

    /**
     * mInflater instantiates a layout XML file into its corresponding View objects.
     */
    private LayoutInflater mInflater = null;

    private List<Map<String, Object>> data;

    private DatabaseManager databaseManager;

    private SQLiteDatabase sqLiteDatabase;

    NewsListViewAdapter(Context context, List<Map<String, Object>> content){
        this.mInflater = LayoutInflater.from(context);
        this.data = content;

        this.databaseManager = DatabaseManager.getInstance(context);
        this.sqLiteDatabase = databaseManager.getWritableDatabase();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.news_list_item, null);
            holder.title = (TextView) convertView.findViewById(R.id.news_title);
            holder.rating = (RatingBar) convertView.findViewById(R.id.ratingBar);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(data.get(position).get("title").toString());
        holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                ContentValues cv = new ContentValues();

                cv.put("news_id", (Integer) data.get(position).get("news_id"));
                cv.put("user_id", MixedActivity.uId);
                cv.put("rating", rating);

                int update = databaseManager.updateNewsRatingList(sqLiteDatabase, cv, (Integer) data.get(position).get("news_id"));

                if (update == 0) {

                    databaseManager.insertNewsRatingList(sqLiteDatabase, cv);
                    if (fromUser) {
                        Toast.makeText(mInflater.getContext(), "Insert rating successfully!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (fromUser) {
                        Toast.makeText(mInflater.getContext(), "Update rating successfully!", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        Cursor c = databaseManager.queryDataFromTable(sqLiteDatabase, "SELECT news.news_id, news_rating.rating FROM news INNER JOIN news_rating ON news.news_id = news_rating.news_id where news_rating.user_id=" + MixedActivity.uId + " AND news_rating.rating>0");

        c.moveToFirst();

        while(c.getCount()>0 && (!c.isAfterLast()) ){

            if(c.getInt(c.getColumnIndex("news_id")) == (Integer) data.get(position).get("news_id")){
                holder.rating.setRating(c.getFloat(c.getColumnIndex("rating")));
            }

            c.moveToNext();
        }

        return convertView;
    }
}
