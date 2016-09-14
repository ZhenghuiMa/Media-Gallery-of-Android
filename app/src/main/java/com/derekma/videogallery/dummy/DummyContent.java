package com.derekma.videogallery.dummy;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.util.Log;

import com.derekma.videogallery.DatabaseManager;
import com.derekma.videogallery.MyRecommendRecyclerViewAdapter;
import com.derekma.videogallery.SyncSQLiteMySQL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent extends AsyncTask {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private DatabaseManager databaseManager;
    private SQLiteDatabase sqLiteDatabase;

    public MyRecommendRecyclerViewAdapter myRecommendRecyclerViewAdapter;

    @Override
    protected Object doInBackground(Object[] params) {

        String result = "";
        URL url;
        HttpURLConnection urlConnection = null;

        try {
            url = new URL("http://people.cs.clemson.edu/~zhenghm/android/get_recommendations.php");

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = urlConnection.getInputStream();

            InputStreamReader reader = new InputStreamReader(inputStream);

            int data = reader.read();

            while (data != -1) {
                char current = (char) data;

                result += current;

                data = reader.read();
            }

            //sqLiteDatabase.execSQL("DELETE FROM ");

            JSONArray jsonArray = new JSONArray(result);

            ITEMS.clear();

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonPart = jsonArray.getJSONObject(i);

                int id = jsonPart.getInt("id");

                String title = jsonPart.getString("movieTitle");

                String playUrl = jsonPart.getString("playUrl");

                String comment = jsonPart.getString("comment");

                String ts = jsonPart.getString("ts");

                ContentValues cv = new ContentValues();
                cv.put("recommend_id", id);
                cv.put("title", title);
                cv.put("id", playUrl);
                cv.put("comment", comment);
                cv.put("ts", ts);

                ITEMS.add(new DummyItem(id, title, playUrl, comment, ts));

                SyncSQLiteMySQL syncSQLiteMySQL = new SyncSQLiteMySQL();
                syncSQLiteMySQL.syncRecommend(cv, Integer.valueOf(id), ts);
            }

            return ITEMS;

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    protected void onPostExecute(Object s) {
        super.onPostExecute(s);

        myRecommendRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {

        public final int id;
        public final String title;
        public final String playId;
        public final String comment;
        public final String ts;

        public DummyItem(int id, String title, String playId, String comment, String ts) {
            this.id = id;
            this.title = title;
            this.playId = playId;
            this.comment = comment;
            this.ts = ts;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
