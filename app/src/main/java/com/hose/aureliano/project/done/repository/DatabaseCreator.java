package com.hose.aureliano.project.done.repository;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Database creator.
 * <p>
 * Date: 12.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class DatabaseCreator {

    private static final Object LOCK = new Object();
    private static DoneDatabase DONE_DATABASE;

    private DatabaseCreator() {
        throw new AssertionError();
    }

    public static DoneDatabase getDatabase(Context context) {
        if (null == DONE_DATABASE) {
            synchronized (LOCK) {
                DONE_DATABASE = Room.databaseBuilder(context, DoneDatabase.class, "done.room.db")
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return DONE_DATABASE;
    }
}
