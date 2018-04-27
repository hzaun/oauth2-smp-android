package com.nuzharukiya.authlauncher.db;

/**
 * Created by Nuzha Rukiya on 18/04/26.
 */
public interface Collection {

    // TABLES
    String AUTH_INFO_TABLE = "AUTH_INFO_TABLE";

    // COLUMNS
    String COL_ID = "_id";
    String COL_ACCESS_TOKEN = "ACCESS_TOKEN";
    String COL_REFRESH_TOKEN = "REFRESH_TOKEN";
    String COL_AUTH_PIN = "AUTH_PIN";
}
