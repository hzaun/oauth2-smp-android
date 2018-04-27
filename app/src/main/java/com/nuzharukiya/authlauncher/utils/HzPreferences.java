package com.nuzharukiya.authlauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Nuzha Rukiya on 18/01/10.
 */

public class HzPreferences {

    public static final String EMPTY_STRING_DEFAULT_VALUE = "null";
    private static final boolean BOOLEAN_DEFAULT_VALUE = false;
    private static final String PREFERENCE_NAME = "HZ_PREFS";

    private static final String USER_LOGGED_IN = "LOGGED_IN";
    private static final String AUTH_PIN = "AUTH_PIN";
    private static final String FINGERPRINT_ENABLED = "FINGERPRINT_ENABLED";
    // OAuth
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

    // Last Service Call
    private static final String LAST_SERVICE_CALLED = "SERVICE_CALL_TIME";

    private static SharedPreferences _sharedPreferences;

    /**
     * Get the shared preferences object.
     */
    public static void init(Context context) {
        if (_sharedPreferences == null) {
            _sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        }
    }

    //    Is the user logged in
    public static boolean isUserLoggedIn() {
        return _sharedPreferences.getBoolean(USER_LOGGED_IN, BOOLEAN_DEFAULT_VALUE);
    }

    public static void putUserLoggedIn(boolean isUserLoggedIn) {
        SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putBoolean(USER_LOGGED_IN, isUserLoggedIn);
        _editor.apply();
        _editor = null;
    }

    //    Authentication PIN
    public static String getAuthPin() {
        return _sharedPreferences.getString(AUTH_PIN, EMPTY_STRING_DEFAULT_VALUE);
    }

    public static void setAuthPin(String authPin) {
        SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putString(AUTH_PIN, authPin);
        _editor.commit();
        _editor = null;
    }

    //    Is fingerprint enabled
    public static boolean isFingerprintEnabled() {
        return _sharedPreferences.getBoolean(FINGERPRINT_ENABLED, BOOLEAN_DEFAULT_VALUE);
    }

    public static void putFingerprintEnabled(boolean isFingerprintEnabled) {
        SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putBoolean(FINGERPRINT_ENABLED, isFingerprintEnabled);
        _editor.apply();
        _editor = null;
    }

    public static long getLastUpdatedTime() {
        return _sharedPreferences.getLong(LAST_SERVICE_CALLED, 0);
    }

    public static String getAccessToken() {
        return _sharedPreferences.getString(ACCESS_TOKEN, EMPTY_STRING_DEFAULT_VALUE);
    }

    public static void setAccessToken(String accessToken) {
        android.content.SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putString(ACCESS_TOKEN, accessToken);
        _editor.apply();
        _editor = null;
    }

    public static String getRefreshToken() {
        return _sharedPreferences.getString(REFRESH_TOKEN, EMPTY_STRING_DEFAULT_VALUE);
    }

    public static void setRefreshToken(String refreshToken) {
        android.content.SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putString(REFRESH_TOKEN, refreshToken);
        _editor.apply();
        _editor = null;
    }

    public static void setLastUpdatedTime(long time) {
        SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.putLong(LAST_SERVICE_CALLED, time);
        _editor.apply();
        _editor = null;
    }

    public static void resetPreferences() {
        SharedPreferences.Editor _editor = _sharedPreferences.edit();
        _editor.clear();
        _editor.commit();
    }
}
