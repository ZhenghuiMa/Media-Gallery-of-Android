package com.derekma.videogallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * This class is to synchronize data in SQLite and MySQL
 * Created by derekma on 16/3/2.
 */
public class SyncSQLiteMySQL {

    private final String GET_USERS_URL = "http://people.cs.clemson.edu/~zhenghm/android/get_users.php";
    private final String UPDATE_SYNCS = "http://people.cs.clemson.edu/~zhenghm/android/updatesyncs.php";
    private final String INSERT_USER = "http://people.cs.clemson.edu/~zhenghm/android/insert_user.php";
    private final String GET_RECOMMENDATION = "http://people.cs.clemson.edu/~zhenghm/android/get_recommendations.php";

    public HashMap<String, Object> queryValues;

    private Context context;

    private SQLiteDatabase sqLiteDatabase;
    private DatabaseManager databaseManager;

    public SyncSQLiteMySQL(Context context) {
        this.context = context;
    }

    public SyncSQLiteMySQL(){

    }

    public void syncRecommend(ContentValues cv, Integer id, String time){

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();

        String query = "SELECT recommend_id from recommend WHERE recommend_id = "+id;

        int update;

        update = sqLiteDatabase.update("recommend", cv, "recommend_id = " + id + " AND ts < " + "'"+time+"'", null);

        if(update == 0){
            Cursor c = sqLiteDatabase.rawQuery(query, null);
            if(c.getCount()==0){
                sqLiteDatabase.insert("recommend", null, cv);
            }
        }

    }

    public void syncSQLiteFromMySQLDB() {

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();

        client.post(GET_USERS_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String response = new String(bytes);

                Log.i("Check Update", "OK");

                updateSQLite(response);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }


    public void userRegisterMySQL() {

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();

        client.post(INSERT_USER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String response = new String(bytes);

                updateSQLite(response);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    public void syncMySQLFromSQLiteDB() {

        final SyncSQLiteMySQL that = this;

        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        ArrayList<HashMap<String, Object>> userList = this.getAllUsers();

        if (userList.size() != 0) {
            Log.i("Check Size", String.valueOf(this.dbSyncCount()));
            if (this.dbSyncCount() != 0) {

                params.put("usersJSON", this.composeJSONfromSQLite());
                client.post(INSERT_USER, params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {

                        try {
                            String response = new String(bytes);

                            JSONArray arr = new JSONArray(response);

                            System.out.println(arr.length());

                            for (int j = 0; j < arr.length(); j++) {
                                JSONObject obj = (JSONObject) arr.get(j);
                                Log.i("Check update", "Here");
                                that.updateSyncStatus(obj.get("username").toString(), obj.get("password").toString());
                            }

                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    }
                });
            } else {
            }
        } else {
        }
    }

    public void updateSQLite(String response) {

        ArrayList<HashMap<String, String>> usersynclist;
        usersynclist = new ArrayList<HashMap<String, String>>();
        // Create GSON object
        Gson gson = new GsonBuilder().create();
        try {
            // Extract JSON array from the response
            JSONArray arr = new JSONArray(response);
            // If no of array elements is not zero
            if (arr.length() != 0) {
                // Loop through each array element, get JSON object which has userid and username
                for (int i = 0; i < arr.length(); i++) {
                    // Get JSON object
                    JSONObject obj = (JSONObject) arr.get(i);

                    // DB QueryValues Object to insert into SQLite
                    queryValues = new HashMap<String, Object>();
                    // Add userID extracted from Object
                    queryValues.put("userId", obj.get("userId"));
                    queryValues.put("userName", obj.get("userName").toString());
                    // Add userName extracted from Object
                    queryValues.put("passWord", obj.get("passWord").toString());

                    // Insert User into SQLite DB
                    insertUser(queryValues);
                    HashMap<String, String> map = new HashMap<String, String>();
                    // Add status for each User in Hashmap
                    map.put("Id", obj.get("userId").toString());
                    map.put("status", "1");
                    usersynclist.add(map);
                }
                // Inform Remote MySQL DB about the completion of Sync activity by passing Sync status of Users
                updateMySQLSyncSts(gson.toJson(usersynclist));
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Method to inform remote MySQL DB about completion of Sync activity
    public void updateMySQLSyncSts(String json) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("syncsts", json);
        // Make Http call to updatesyncsts.php with JSON parameter which has Sync statuses of Users
        client.post(UPDATE_SYNCS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Log.i("Update Check", "OK");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    /**
     * Inserts User into SQLite DB
     *
     * @param queryValues
     */
    public void insertUser(HashMap<String, Object> queryValues) {

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();

        ContentValues values = new ContentValues();
        //values.put("user_id", (Integer) queryValues.get("userId"));
        values.put("username", queryValues.get("userName").toString());
        values.put("password", queryValues.get("passWord").toString());

        databaseManager.insertIntoUser(sqLiteDatabase, queryValues.get("userName").toString(), queryValues.get("passWord").toString());
        //sqLiteDatabase.insertWithOnConflict("user", null, values, 0);
        //sqLiteDatabase.close();
    }

    /**
     * Get all users from SQLite
     */
    /**
     * Get list of Users from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, Object>> getAllUsers() {
        ArrayList<HashMap<String, Object>> wordList;
        wordList = new ArrayList<HashMap<String, Object>>();
        String selectQuery = "SELECT * FROM user";

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("userId", cursor.getInt(0));
                map.put("userName", cursor.getString(1));
                map.put("passWord", cursor.getString(2));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //sqLiteDatabase.close();
        return wordList;
    }

    /**
     * Compose JSON out of SQLite records
     *
     * @return
     */
    public String composeJSONfromSQLite() {

        ArrayList<HashMap<String, Object>> wordList;
        wordList = new ArrayList<HashMap<String, Object>>();
        String selectQuery = "SELECT * FROM user where status = 0";

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("userId", cursor.getInt(0));
                map.put("userName", cursor.getString(1));
                map.put("passWord", cursor.getString(2));
                map.put("token", cursor.getString(4));
                wordList.add(map);
            } while (cursor.moveToNext());
        }

        //sqLiteDatabase.close();

        Gson gson = new GsonBuilder().create();

        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }

    /**
     * Get Sync status of SQLite
     *
     * @return
     */
    public String getSyncStatus() {
        String msg = null;
        if (this.dbSyncCount() == 0) {
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        } else {
            msg = "DB Sync error!";
        }
        return msg;
    }

    /**
     * Get SQLite records that are yet to be Synced
     *
     * @return
     */
    public int dbSyncCount() {

        int count = 0;
        String selectQuery = "SELECT * FROM user where status = 0";

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        count = cursor.getCount();
        //sqLiteDatabase.close();
        return count;
    }

    /**
     * Update Sync status against each User ID
     *
     * @param username
     * @param password
     */
    public void updateSyncStatus(String username, String password) {

        databaseManager = DatabaseManager.getInstance(context);
        sqLiteDatabase = databaseManager.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("password", password);
        cv.put("status", 1);

        int update = sqLiteDatabase.update("user", cv, "username = " + "'"+username+"'", null);

        if(update == 0){
            sqLiteDatabase.insertWithOnConflict("user", null, cv, 0);
        }
    }
}
