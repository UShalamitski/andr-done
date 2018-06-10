package com.hose.aureliano.project.done.repository.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
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
 * @author Uladzislau Shalamatski
 */
@Dao
public interface TaskDao {

    @Insert
    long insert(Task item);

    @Insert
    List<Long> insert(List<Task> items);

    @Update
    int update(Task item);

    @Update
    int update(List<Task> items);

    @Query("UPDATE tasks SET remindDateTime = null WHERE id = :taskId")
    int deleteReminderDate(int taskId);

    @Query("DELETE FROM tasks WHERE id = :id")
    int delete(int id);

    @Delete
    int delete(List<Task> items);

    @Query("DELETE FROM tasks WHERE listId = :listId")
    int deleteByListId(Integer listId);

    @Query("SELECT t.id, t.listId, l.name AS listName, t.name, t.note, t.dueDateTime, t.remindDateTime, t.done, t.position, t.createdDateTime, t.repeatType " +
            "FROM tasks t " +
            "INNER JOIN lists l ON l.id = t.listId " +
            "WHERE t.listId = :listId ORDER BY CASE WHEN t.position IS NULL THEN 1 ELSE 0 END, t.position, t.createdDateTime")
    List<Task> readByListId(Integer listId);

    @Query("SELECT t.id, t.listId, l.name AS listName, t.name, t.note, t.dueDateTime, t.remindDateTime, t.done, t.position, t.createdDateTime, t.repeatType " +
            "FROM tasks t " +
            "INNER JOIN lists l ON l.id = t.listId " +
            "WHERE NOT t.done AND t.dueDateTime BETWEEN :beginDate AND :endDate ORDER BY t.dueDateTime")
    List<Task> readByDueDate(long beginDate, long endDate);

    @Query("SELECT t.id, t.listId, l.name AS listName, t.name, t.note, t.dueDateTime, t.remindDateTime, t.done, t.position, t.createdDateTime, t.repeatType " +
            "FROM tasks t " +
            "INNER JOIN lists l ON l.id = t.listId " +
            "WHERE t.remindDateTime IS NOT null AND NOT t.done")
    List<Task> readNotCompletedWithReminder();

    @Query("SELECT t.id, t.listId, l.name AS listName, t.name, t.note, t.dueDateTime, t.remindDateTime, t.done, t.position, t.createdDateTime, t.repeatType " +
            "FROM tasks t " +
            "INNER JOIN lists l ON l.id = t.listId " +
            "WHERE t.Id IN (:taskIds) AND t.remindDateTime IS NOT null AND NOT t.done")
    List<Task> readNotCompletedWithReminderByIds(List<Long> taskIds);

    @Query("SELECT MAX(position) FROM tasks WHERE listId = :listId")
    int getMaxPosition(Integer listId);

    @Query("SELECT * FROM tasks WHERE NOT done AND dueDateTime < CAST(strftime('%s', 'now')  AS  integer) * 1000")
    List<Task> readOverdueTasks();
}
