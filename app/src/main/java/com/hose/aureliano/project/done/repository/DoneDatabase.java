package com.hose.aureliano.project.done.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;

/**
 * Created by evere on 12.02.2018.
 */

@Database(entities = {DoneList.class}, version = 1)
public abstract class DoneDatabase extends RoomDatabase {

    public abstract DoneListDao getDoneListDao();
}
