package com.nuzharukiya.authlauncher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.nuzharukiya.authlauncher.db.Collection.COL_ACCESS_TOKEN;
import static com.nuzharukiya.authlauncher.db.Collection.COL_AUTH_PIN;
import static com.nuzharukiya.authlauncher.db.Collection.COL_ID;
import static com.nuzharukiya.authlauncher.db.Collection.COL_REFRESH_TOKEN;
import static com.nuzharukiya.authlauncher.db.Collection.AUTH_INFO_TABLE;

/**
 * Created by Nuzha Rukiya on 18/04/26.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "DB_HZ";

    private static final String CREATE_TOKEN_TABLE = "CREATE TABLE IF NOT EXISTS " + AUTH_INFO_TABLE + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL , "
            + COL_ACCESS_TOKEN + " VARCHAR, "
            + COL_REFRESH_TOKEN + " VARCHAR, "
            + COL_AUTH_PIN + " VARCHAR"
            + ")";

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables here
        db.execSQL(CREATE_TOKEN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
