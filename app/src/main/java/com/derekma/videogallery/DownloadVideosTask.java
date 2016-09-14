package com.derekma.videogallery;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by derekma on 16/2/13.
 * Download data from YouTube data API and When the data is ready
 * It will invoke adapter to notify the ListView to update.
 */
public class DownloadVideosTask extends AsyncTask<String, Void, String> {

    public SimpleAdapter adapter;

    private DatabaseManager databaseManager;

    private SQLiteDatabase sqLiteDatabase;

    /**
     * A set of ArrayList variables.
     * Manage the data reading from database.
     * Title, Image, ID, Description and Channel.
     */
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<Bitmap> imgs = new ArrayList<Bitmap>();
    ArrayList<String> ids = new ArrayList<String>();
    ArrayList<String> descriptions = new ArrayList<String>();
    ArrayList<String> channels = new ArrayList<>();
    ArrayList<Integer> videoIds = new ArrayList<>();

    public DownloadVideosTask(SQLiteDatabase db){
        this.sqLiteDatabase = db;
    }

    @Override
    protected String doInBackground(String... params) {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL(params[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(inputStream);

            int data = reader.read();

            while (data != -1) {
                char current = (char) data;

                result += current;

                data = reader.read();
            }

            //MainActivity.mySQLiteDB.execSQL("DELETE FROM video");

            JSONObject jsonObject = new JSONObject(result);

            String detail = jsonObject.getString("items");

            JSONArray jsonArray = new JSONArray(detail);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonPart = jsonArray.getJSONObject(i);

                Log.i("Website Content", jsonPart.getString("id"));

                String urlSnippet = jsonPart.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("medium").getString("url");

                String titleSnippet = jsonPart.getJSONObject("snippet").getString("title");

                String descSnippet = jsonPart.getJSONObject("snippet").getString("description");

                String channelSnippet = jsonPart.getJSONObject("snippet").getString("channelTitle");

                Log.i("Website Content", urlSnippet);

                url = new URL(urlSnippet);

                urlConnection = (HttpURLConnection) url.openConnection();

                inputStream = urlConnection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                String sql = "INSERT INTO video (play_id, img, title, desc, channel) VALUES (?, ?, ?, ?, ?)";

                SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);

                statement.bindString(1, jsonPart.getString("id"));
                statement.bindBlob(2, DbBitmapUtility.getBytes(myBitmap));
                statement.bindString(3, titleSnippet);
                statement.bindString(4, descSnippet);
                statement.bindString(5, channelSnippet);

                statement.execute();
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();

            return "Failed";
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        updateListView();

        adapter.notifyDataSetChanged();

        if(MixedActivity.mDialog instanceof ProgressDialog){
            MixedActivity.mDialog.dismiss();
        }

    }

    /**
     * A public method.
     * Update ListView when get new data from Google API.
     */
    public void updateListView() {

        try {

            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM video ORDER BY video_id DESC LIMIT 10", null);

            int videoIdIndex = c.getColumnIndex("video_id");
            int playIdIndex = c.getColumnIndex("play_id");
            int titleIndex = c.getColumnIndex("title");
            int imgIndex = c.getColumnIndex("img");
            int descriptionIndex = c.getColumnIndex("desc");
            int channelIndex = c.getColumnIndex("channel");

            int scoreEdge = 0;
            c.moveToFirst();

            titles.clear();
            ids.clear();
            imgs.clear();
            descriptions.clear();
            channels.clear();
            videoIds.clear();


            while ((!c.isAfterLast()) && scoreEdge < 11) {

                scoreEdge++;
                titles.add(c.getString(titleIndex));
                ids.add(c.getString(playIdIndex));
                imgs.add(DbBitmapUtility.getImage(c.getBlob(imgIndex)));
                descriptions.add(c.getString(descriptionIndex));
                channels.add(c.getString(channelIndex));
                videoIds.add(c.getInt(videoIdIndex));

                c.moveToNext();

            }

            VideosFragment.list.clear();

            if (!(ids.isEmpty())) {

                Map<String, Object> map;

                for (int i = 0; i < 10; i++) {
                    map = new HashMap<String, Object>();
                    map.put("img", imgs.get(i));
                    map.put("title", titles.get(i));
                    map.put("play_id", ids.get(i));
                    map.put("description", descriptions.get(i));
                    map.put("channel", channels.get(i));
                    map.put("video_id", videoIds.get(i));
                    VideosFragment.list.add(map);
                }
            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }


}
