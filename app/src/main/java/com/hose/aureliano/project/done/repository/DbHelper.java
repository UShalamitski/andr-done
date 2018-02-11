package com.hose.aureliano.project.done.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by evere on 05.02.2018.
 */

public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "done.db";
    private static final int DATABASE_VERSION = 2;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createListsTableSql = "CREATE TABLE lists ( " +
                "_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL);";
        String createItemsTableSql = "CREATE TABLE items ( " +
                "_id TEXT PRIMARY KEY, " +
                "list_id TEXT NOT NULL, " +
                "name TEXT NOT NULL, " +
                "FOREIGN KEY (list_id) REFERENCES lists(_id));";
        db.execSQL(createListsTableSql);
        db.execSQL(createItemsTableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS lists;");
            onCreate(db);
        }
    }
}
