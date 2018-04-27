package com.nuzharukiya.authlauncher.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.nuzharukiya.authlauncher.utils.HzPreferences;

/**
 * Created by Nuzha Rukiya on 18/04/26.
 */
public class DbUtil implements
        Collection {

    /**
     * Inserts the Access and Refresh token into the db
     * Update this every time a new token is fetched
     *
     * @param accessToken
     * @param refreshToken
     */
    public static void updateOAuthTokens(String accessToken, String refreshToken) {
        ContentValues values = new ContentValues();
        values.put(COL_ACCESS_TOKEN, accessToken);
        values.put(COL_REFRESH_TOKEN, refreshToken);
        updateRow(values);
    }

    /**
     * Inserts a new row if it doesn't exist,
     * Else updates it
     */
    private static void updateRow(ContentValues cv) {
        if (DbAdapter.getDbAdapterInstance().update(AUTH_INFO_TABLE, cv) == 0) {
            DbAdapter.getDbAdapterInstance().insert(AUTH_INFO_TABLE, cv);
        }
    }

    /**
     * Retrieves the OAuthToken and AuthPin
     * Updates the preferences
     */
    public static boolean retrieveAuthInfo() {
        Cursor cursor = DbAdapter.getDbAdapterInstance().getResultSet(
                AUTH_INFO_TABLE,
                new String[]{COL_ACCESS_TOKEN, COL_REFRESH_TOKEN, COL_AUTH_PIN},
                null);

        if (null != cursor) {
            if (cursor.moveToFirst()) {
                HzPreferences.setAccessToken(cursor.getString(cursor
                        .getColumnIndexOrThrow(COL_ACCESS_TOKEN)));
                HzPreferences.setRefreshToken(cursor.getString(cursor
                        .getColumnIndexOrThrow(COL_REFRESH_TOKEN)));
                HzPreferences.setAuthPin(cursor.getString(cursor
                        .getColumnIndexOrThrow(COL_AUTH_PIN)));

                return true;
            }
            cursor.close();
        }
        return false;
    }

    /**
     * Update Auth PIN
     * @param authPin
     */
    public static void updateAuthPin(String authPin) {
        ContentValues values = new ContentValues();
        values.put(COL_AUTH_PIN, authPin);
        updateRow(values);
    }
}
