package com.derekma.videogallery;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

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
 * Created by derekma on 16/2/14
 * Download data from NPR news API and When the data is ready
 * It will use adapter to notify the ListView to update.
 */
public class DownloadNewsTask extends AsyncTask<String, Void, String> {

    public NewsListViewAdapter adapter;

    private DatabaseManager databaseManager;

    private SQLiteDatabase sqLiteDatabase;

    /**
     * A set of ArrayList variables.
     * Manage the data reading from database.
     * Title, Image, ID, Description and Channel.
     */
    ArrayList<String> titles = new ArrayList<String>();
    ArrayList<Integer> ids = new ArrayList<Integer>();
    ArrayList<String> urls = new ArrayList<String>();
    ArrayList<String> dates = new ArrayList<>();

    public DownloadNewsTask(SQLiteDatabase db){
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

            sqLiteDatabase.execSQL("DELETE FROM news");

            JSONObject jsonObject = new JSONObject(result);

            String detail = jsonObject.getJSONObject("list").getString("story");

            JSONArray jsonArray = new JSONArray(detail);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonPart = jsonArray.getJSONObject(i);

                Log.i("Website Content", jsonPart.getString("id"));

                int id = Integer.valueOf(jsonPart.getString("id"));

                String urlSnippet = jsonPart.getJSONArray("link").getJSONObject(0).getString("$text");

                String titleSnippet = jsonPart.getJSONObject("title").getString("$text");

                String dateSnippet = jsonPart.getJSONObject("storyDate").getString("$text");

                /*
                String imageSnippet = jsonPart.getJSONArray("image").getJSONObject(0).getString("src");

                Log.i("Website Content", urlSnippet);

                url = new URL(imageSnippet);

                urlConnection = (HttpURLConnection) url.openConnection();

                inputStream = urlConnection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
                */

                String sql = "INSERT INTO news (news_id, title, date, url) VALUES (?, ?, ?, ?)";

                SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);

                statement.bindLong(1, id);
                statement.bindString(2, titleSnippet);
                statement.bindString(3, dateSnippet);
                statement.bindString(4, urlSnippet);

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

    }

    /**
     * A public method.
     * Update ListView when get new data from Google API.
     */
    public void updateListView() {

        try {

            Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM news", null);

            int newsIdIndex = c.getColumnIndex("news_id");
            int titleIndex = c.getColumnIndex("title");
            //int imgIndex = c.getColumnIndex("img");
            int urlIndex = c.getColumnIndex("url");
            int dateIndex = c.getColumnIndex("date");

            int scoreEdge = 0;
            c.moveToFirst();

            titles.clear();
            ids.clear();
            //imgs.clear();
            urls.clear();
            dates.clear();


            while ((!c.isAfterLast()) && scoreEdge < 11) {

                scoreEdge++;
                titles.add(c.getString(titleIndex));
                ids.add(c.getInt(newsIdIndex));
                //imgs.add(DbBitmapUtility.getImage(c.getBlob(imgIndex)));
                urls.add(c.getString(urlIndex));
                dates.add(c.getString(dateIndex));

                c.moveToNext();

            }

            NewsFragment.list.clear();

            if (!(ids.isEmpty())) {

                Map<String, Object> map;

                for (int i = 0; i < 10; i++) {
                    map = new HashMap<String, Object>();
                    //map.put("img", imgs.get(i));
                    map.put("title", titles.get(i));
                    map.put("news_id", ids.get(i));
                    map.put("date", dates.get(i));
                    map.put("url", urls.get(i));
                    NewsFragment.list.add(map);
                }
            }


        } catch (Exception e) {

            e.printStackTrace();

        }

    }


}
