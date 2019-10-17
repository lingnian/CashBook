package com.example.application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 25485 on 2018/6/6.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private  static final String DATABSENAME = "mydata";
    private static final int VERSION = 1;
    private static final String CreateTable =
            "Create Table record(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name text," +
                    "Brief text," +
                    "date text," +
                    "type text," +
                    "objname text," +
                    "money int," +
                    "record text)";
    public  MyDatabaseHelper(Context context){
        super(context,DATABSENAME,null,VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
          db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
