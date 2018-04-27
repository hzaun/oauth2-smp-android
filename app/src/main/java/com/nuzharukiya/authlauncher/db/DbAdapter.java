package com.nuzharukiya.authlauncher.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Nuzha Rukiya on 18/04/26.
 */
public class DbAdapter implements Collection {

    private static SQLiteDatabase sqliteDB;
    private static DbAdapter dbAdapter;

    public static DbAdapter getDbAdapterInstance() {
        if (null == dbAdapter) {
            dbAdapter = new DbAdapter();
        }
        return dbAdapter;
    }

    public DbAdapter open(Context context) {
        DbHelper dbHelperTasks = new DbHelper(context);
        sqliteDB = dbHelperTasks.getWritableDatabase();
        dbHelperTasks.onCreate(sqliteDB);
        return this;
    }

    public long insert(String tableName, ContentValues contentValues) {
        return sqliteDB.insert(tableName, null, contentValues);
    }

    public long update(String tableName, ContentValues contentValues) {
        return sqliteDB.update(tableName, contentValues, COL_ID + "=?", new String[]{"1"});
    }

    public Cursor getResultSet(String tableName, String[] columns, Context c) throws SQLException {
        Cursor mCursor = null;

        if (sqliteDB == null) {
            DbAdapter.getDbAdapterInstance().open(c);
            return mCursor;
        }

        mCursor = sqliteDB.query(true, tableName, columns, null, null, null,
                null, null, null);

        return mCursor;
    }
}
