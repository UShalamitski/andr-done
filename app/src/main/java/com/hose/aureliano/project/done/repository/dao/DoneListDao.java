package com.hose.aureliano.project.done.repository.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.hose.aureliano.project.done.model.DoneList;

import java.util.List;

/**
 * Created by evere on 12.02.2018.
 */

@Dao
public interface DoneListDao {

    @Insert
    long insert(DoneList doneList);

    @Update
    int update(DoneList doneList);

    @Query("DELETE FROM lists")
    int delete();

    @Query("DELETE FROM lists WHERE id = :id")
    int delete(String id);

    @Query("SELECT id, name FROM lists")
    List<DoneList> read();
}
