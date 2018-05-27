package com.hose.aureliano.project.done.repository.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.hose.aureliano.project.done.model.Task;

import java.util.List;

/**
 * DAO for {@link Task}.
 * <p>
 * Date: 12.02.2018.
 *
 * @author evere
 */
@Dao
public interface TaskDao {

    @Insert
    long insert(Task item);

    @Update
    int update(Task item);

    @Query("UPDATE tasks SET remindTimeIsSet = 'false', remindDateTime = null WHERE id = :taskId")
    int deleteReminderDate(int taskId);

    @Query("DELETE FROM tasks WHERE id = :id")
    int delete(int id);

    @Query("DELETE FROM tasks WHERE listId = :listId")
    int deleteByListId(Integer listId);

    @Query("SELECT * FROM tasks WHERE listId = :listId ORDER BY CASE WHEN position IS NULL THEN 1 ELSE 0 END, position")
    List<Task> readByListId(Integer listId);

    @Query("SELECT * FROM tasks WHERE NOT done AND dueDateTime BETWEEN :beginDate AND :endDate ORDER BY dueDateTime")
    List<Task> readByDueDate(long beginDate, long endDate);

    @Query("SELECT * FROM tasks WHERE remindDateTime IS NOT null AND NOT done")
    List<Task> readNotCompletedWithReminder();

    @Query("SELECT MAX(position) FROM tasks WHERE listId = :listId")
    int getMaxPosition(Integer listId);

    @Query("SELECT * FROM tasks WHERE NOT done AND dueDateTime < CAST(strftime('%s', 'now')  AS  integer) * 1000")
    List<Task> readOverdueTasks();
}
