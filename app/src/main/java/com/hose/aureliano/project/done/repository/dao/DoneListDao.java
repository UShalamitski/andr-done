package com.hose.aureliano.project.done.repository.dao;

import android.arch.persistence.room.Dao;
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

    @Query("DELETE FROM lists WHERE id = :id")
    int delete(String id);

    @Query("SELECT id, name, createdDateTime, (SELECT COUNT(*) FROM tasks t WHERE t.listId = l.id) AS tasksCount, " +
            "(SELECT COUNT(*) FROM tasks t WHERE t.listId = l.id AND t.done) AS doneTasksCount " +
            "FROM lists l ORDER BY CASE WHEN position IS NULL THEN 1 ELSE 0 END, position")
    List<DoneList> read();
}
