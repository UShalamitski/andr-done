package com.hose.aureliano.project.done.repository.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.hose.aureliano.project.done.model.DoneList;

import java.util.List;

/**
 * DAO for {@link DoneList}.
 * <p>
 * Date: 12.02.2018.
 *
 * @author evere
 */
@Dao
public interface DoneListDao {

    @Insert
    long insert(DoneList doneList);

    @Update
    int update(DoneList doneList);

    @Query("DELETE FROM lists")
    int delete();

    @Delete
    int delete(DoneList doneList);

    @Query("SELECT id, name, createdDateTime, position, (SELECT COUNT(*) FROM tasks t " +
            "WHERE t.listId = l.id) AS tasksCount, " +
            "(SELECT COUNT(*) FROM tasks t WHERE t.listId = l.id AND t.done) AS doneTasksCount " +
            "FROM lists l ORDER BY position")
    List<DoneList> read();
}
