package com.hose.aureliano.project.done.service;

import android.content.Context;

import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.TaskDao;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;

import java.util.List;

/**
 * Service logic for tasks.
 * <p/>
 * Date: 11.03.2018
 *
 * @author Uladzislau Shalamitski
 */
public class TaskService {

    private TaskDao taskDao;
    private Context context;

    /**
     * Constructor.
     *
     * @param context application context
     */
    public TaskService(Context context) {
        this.context = context;
        taskDao = DatabaseCreator.getDatabase(context).getTaskDao();
    }

    /**
     * Inserts instance of {@link Task}.
     *
     * @param task instance of {@link Task} to insert
     */
    public long insert(Task task) {
        return taskDao.insert(task);
    }

    /**
     * Inserts {@link Task}s.
     *
     * @param tasks list of {@link Task}s to insert
     */
    public void duplicate(List<Task> tasks) {
        for (Task task : tasks) {
            task.setId(null);
            task.setPosition(null);
            task.setDone(false);
            Integer taskId = (int) taskDao.insert(task);
            if (null != task.getRemindDateTime() && System.currentTimeMillis() < task.getRemindDateTime()) {
                task.setId(taskId);
                AlarmService.setAlarm(context, task);
            }
        }
    }

    /**
     * Updates instance of {@link Task}.
     *
     * @param task instance of {@link Task} to update
     * @return number of affected rows or -1
     */
    public int update(Task task) {
        return taskDao.update(task);
    }

    /**
     * Updates {@link Task}s.
     *
     * @param tasks list of {@link Task} to update
     */
    public void update(List<Task> tasks) {
        for (Task task : tasks) {
            update(task);
        }
    }

    /**
     * Removes all tasks for specified list and reminders for that tasks.
     *
     * @param listId identifier of list
     */
    public void deleteByListId(String listId) {
        List<Task> tasks = taskDao.read(listId);
        for (Task task : tasks) {
            AlarmService.cancelAlarm(context, task);
        }
        taskDao.deleteByListId(listId);
    }

    /**
     * Removes specified {@link Task} and reminder for it.
     *
     * @param task instance of {@link Task} to remove
     */
    public void delete(Task task) {
        AlarmService.cancelAlarm(context, task);
        taskDao.delete(task.getId());
    }

    /**
     * Removes specified {@link Task}s and reminder for them.
     *
     * @param tasks list of {@link Task}s to remove
     */
    public void delete(List<Task> tasks) {
        for (Task task : tasks) {
            delete(task);
        }
    }

    /**
     * Removes reminder date from {@link Task} by given identifier.
     *
     * @param taskId identifier of {@link Task}
     */
    public void deleteReminderDate(int taskId) {
        taskDao.deleteReminderDate(taskId);
    }

    /**
     * @return list of incompleted {@link Task}s which have a reminder.
     */
    public List<Task> getNotCompletedTasksWithReminder() {
        return taskDao.readNotCompletedWithReminder();
    }

    /**
     * Returns next available position for new task in particular list.
     *
     * @param listId identifier of list
     * @return next available position for new task in particular list.
     */
    public int getAvailablePosition(String listId) {
        return taskDao.getMaxPosition(listId);
    }
}
