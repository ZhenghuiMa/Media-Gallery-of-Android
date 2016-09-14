package com.derekma.videogallery;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A Login Class. This class provides Register and Login.
 */
public class MainActivity extends AppCompatActivity {

    public static SQLiteDatabase mySQLiteDB;
    public static DatabaseManager databaseManager;

    EditText textName; /**< EditText value textName. */
    EditText textPassword; /**< EditText value textPassword. */

    /**
     * Function for user register.
     * @param view
     */
    public void userRegister(View view){

        String sql = "SELECT * FROM user WHERE username=" + "'" + textName.getText().toString()+"'";

        Cursor c = mySQLiteDB.rawQuery(sql, null);

        if(c.getCount()>0){
            Toast.makeText(getApplicationContext(), "Username has been occupied, please change a new one.", Toast.LENGTH_LONG).show();
        }else{

            databaseManager.insertIntoUser(mySQLiteDB, textName.getText().toString(), textPassword.getText().toString());

            Toast.makeText(getApplicationContext(), "Register success, please log in!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * Function for user log in.
     * @param view a view argument.
     */
    public void userLogin(View view){

        Intent i = new Intent(getApplicationContext(), MixedActivity.class);

        String sql = "SELECT * FROM user WHERE username=" + "'" + textName.getText().toString()+"'" + " AND password="+ "'"+textPassword.getText().toString()+"'";

        Cursor c = mySQLiteDB.rawQuery(sql, null);

        if(c.getCount()>0 && textName.getText().toString()!="" && textPassword.getText().toString()!=""){
            c.moveToFirst();
            i.putExtra("uId", c.getInt(c.getColumnIndex("user_id")));
            startActivity(i);
        }else{
            Toast.makeText(getApplicationContext(), "Username or Password is wrong, please try again!", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * A method.
     * Go to information activity.
     * @param view
     */
    public void openInformation(View view){

        Intent i = new Intent(getApplicationContext(), InformationActivity.class);

        startActivity(i);
    }

    /**
     * Override function for onCreate.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        databaseManager = DatabaseManager.getInstance(getApplicationContext());

        mySQLiteDB = databaseManager.getWritableDatabase();

        databaseManager.onCreate(mySQLiteDB);

        textName = (EditText) findViewById(R.id.nameid);
        textPassword = (EditText) findViewById(R.id.passwordid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}

