package com.hose.aureliano.project.done.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;
import com.hose.aureliano.project.done.repository.dao.TaskDao;

/**
 * Database.
 * <p>
 * Date: 12.02.2018.
 *
 * @author evere
 */
@Database(entities = {DoneList.class}, version = 1)
public abstract class DoneDatabase extends RoomDatabase {

    public abstract DoneListDao getDoneListDao();

    public abstract TaskDao getTaskDao();
}
