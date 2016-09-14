package com.derekma.videogallery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.derekma.videogallery.dummy.DummyContent;

/**
 * Created by derekma on 16/2/16.
 * A class extends SQLiteOpenHelper to provide DAO action.
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager dmIstance = null;
    //private static SQLiteDatabase db;
    private Context mCtx;

    private static final String DB_NAME = "video_gallery";
    private static final int DB_VERSION = 1;

    /**
     * Define each table name.
     */
    private static final String FAVORITES_TABLE_NAME = "favorite";
    private static final String NEWS_RATING_TABLE_NAME = "news_rating";

    /**
     * Define each create table statement.
     */
    private static final String VIDEOS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS video (video_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, play_id VARCHAR, img Blob, title VARCHAR, desc VARCHAR, channel VARCHAR)";

    private static final String NEWS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS news (news_id INTEGER PRIMARY KEY NOT NULL, title VARCHAR, date VARCHAR, url VARCHAR)";

    private static final String USER_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS user (user_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, username VARCHAR, password VARCHAR, token VARCHAR)";

    private static final String FAVORITES_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS favorite (favorite_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, user_id INTEGER, video_id INTEGER, FOREIGN KEY(user_id) REFERENCES user(user_id), FOREIGN KEY(video_id) REFERENCES video(video_id))";

    private static final String NEWS_RATING_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS news_rating (news_rating_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, user_id INTEGER, news_id INTEGER, rating FLOAT, comment VARCHAR, FOREIGN KEY(user_id) REFERENCES user(user_id), FOREIGN KEY(news_id) REFERENCES news(news_id))";

    private static final String RECOMMEND_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS recommend (recommend_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title VARCHAR, id VARCHAR, comment VARCHAR, ts TimeStamp NOT NULL DEFAULT (datetime('now','localtime')))";

    private static final String RECOMMEND_LIST_TABLE_CREATE ="CREATE TABLE IF NOT EXISTS recommend_list (\n" +
            "    recommend_list_id integer  NOT NULL   PRIMARY KEY AUTOINCREMENT,\n" +
            "    recommend_id integer  NOT NULL,\n" +
            "    user_id integer  NOT NULL,\n" +
            "    FOREIGN KEY (user_id) REFERENCES user (user_id),\n" +
            "    FOREIGN KEY (user_id) REFERENCES recommend (recommend_id)\n" +
            ")";
    /*
    public DatabaseManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    */

    private  DatabaseManager(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.mCtx = context;
    }

    public static DatabaseManager getInstance(Context ctx){
        if(dmIstance==null){
            dmIstance = new DatabaseManager(ctx);
        }

        return dmIstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * Execute each SQL to create tables
         */

        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(VIDEOS_TABLE_CREATE);
        db.execSQL(NEWS_TABLE_CREATE);
        db.execSQL(FAVORITES_TABLE_CREATE);
        db.execSQL(NEWS_RATING_TABLE_CREATE);
        db.execSQL(RECOMMEND_TABLE_CREATE);
        db.execSQL(RECOMMEND_LIST_TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * This method is to insert user information into user table.
     * @param db SQLiteDatabase
     * @param name String
     * @param password String
     */
    public void insertIntoUser(SQLiteDatabase db, String name, String password){

        String sql = "INSERT INTO user (username, password) VALUES (?, ?)";

        SQLiteStatement statement = db.compileStatement(sql);

        statement.bindString(1, name);
        statement.bindString(2, password);

        statement.execute();
    }

    /**
     * This method is to update rating information into news_rating table.
     * @param db SQLiteDatabase
     * @param cv ContentValues
     * @param id news_id
     * @return int
     */
    public int updateNewsRatingList(SQLiteDatabase db, ContentValues cv, int id) {

        int update;

        update = db.update(NEWS_RATING_TABLE_NAME, cv, "user_id = " + MixedActivity.uId + " AND news_id=" + id, null);

        return update;
    }

    /**
     * This method is to insert rating information into news_rating table
     * @param db SQLiteDatabase
     * @param cv ContentValues
     */
    public void insertNewsRatingList(SQLiteDatabase db, ContentValues cv) {

        db.insertWithOnConflict(NEWS_RATING_TABLE_NAME, null, cv, 0);
    }

    public int updateFavoriteList(SQLiteDatabase db, ContentValues cv, int id) {

        int update;

        update = db.update(FAVORITES_TABLE_NAME, cv, "user_id = " + MixedActivity.uId + " AND video_id=" + id, null);

        return update;
    }

    public void insertFavoriteList(SQLiteDatabase db, ContentValues cv) {

        db.insertWithOnConflict(FAVORITES_TABLE_NAME, null, cv, 0);
    }

    public int deleteFromFavoriteList(SQLiteDatabase db, int id) {

        return db.delete("favorite", "favorite_id = " + id, null);
    }

    public Cursor queryDataFromTable(SQLiteDatabase db, String query) {
        return db.rawQuery(query, null);
    }

    public int updateToken(SQLiteDatabase db, ContentValues cv) {

        int update;

        update = db.update("user", cv, "user_id = " + MixedActivity.uId, null);

        return update;
    }

    public void insertToken(SQLiteDatabase db, ContentValues cv) {

        db.insertWithOnConflict("user", null, cv, 0);
    }

    public void getRecommendFromSQLite(SQLiteDatabase db){
        try {

            Cursor c = db.rawQuery("SELECT * FROM recommend limit 25", null);

            int recommendIdIndex = c.getColumnIndex("recommend_id");
            int titleIndex = c.getColumnIndex("title");
            int idIndex = c.getColumnIndex("id");
            int commentIndex = c.getColumnIndex("comment");
            int tsIndex = c.getColumnIndex("ts");

            int scoreEdge = 0;
            c.moveToFirst();

            DummyContent.ITEMS.clear();

            while ((!c.isAfterLast()) && scoreEdge < 26) {

                scoreEdge++;


                DummyContent.ITEMS.add(new DummyContent.DummyItem(c.getInt(recommendIdIndex), c.getString(titleIndex), c.getString(idIndex),
                        c.getString(commentIndex), c.getString(tsIndex)));

                c.moveToNext();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}