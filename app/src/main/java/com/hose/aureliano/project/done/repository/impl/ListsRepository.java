package com.hose.aureliano.project.done.repository.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hose.aureliano.project.done.repository.DbHelper;
import com.hose.aureliano.project.done.repository.api.IListRepository;

import java.util.Objects;

/**
 * Created by everest on 05.02.2018.
 */

public class ListsRepository implements IListRepository {

    private DbHelper dbHelper;

    public ListsRepository(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public long insert(ContentValues values) {
        Objects.requireNonNull(values);
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            return database.insertOrThrow("lists", null, values);
        }
    }

    @Override
    public int delete() {
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            return database.delete("lists", null, new String[]{});
        }
    }

    @Override
    public int update(ContentValues values) {
        Objects.requireNonNull(values);
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            return database.update("lists", values, "_id = ?", new String[]{(String) values.get("_id")});
        }
    }

    @Override
    public int delete(String id) {
        Objects.requireNonNull(id);
        try (SQLiteDatabase database = dbHelper.getWritableDatabase()) {
            return database.delete("lists", "_id = ?", new String[]{id});
        }
    }

    @Override
    public Cursor read() {
        return dbHelper.getWritableDatabase().rawQuery("SELECT _id, name FROM lists", new String[]{});
    }
}
