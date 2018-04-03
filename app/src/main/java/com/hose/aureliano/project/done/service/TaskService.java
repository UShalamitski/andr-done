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

    public TaskService(Context context) {
        this.context = context;
        taskDao = DatabaseCreator.getDatabase(context).getTaskDao();
    }

    /**
     * Updates instance of {@link Task}.
     *
     * @param task instance of {@link Task} to update
     */
    public void update(Task task) {
        taskDao.update(task);
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
     * Removes reminder date from {@link Task} by given identifier.
     *
     * @param taskId identifier of {@link Task}
     */
    public void deleteReminderDate(int taskId) {
        taskDao.deleteReminderDate(taskId);
    }

    /**
     * @return list of {@link Task}s which have a reminder.
     */
    public List<Task> getTasksWithReminder() {
        return taskDao.readAllWithReminder();
    }
}
