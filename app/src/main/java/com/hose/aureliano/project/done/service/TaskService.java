package com.hose.aureliano.project.done.service;

import android.content.Context;

import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.model.TasksViewEnum;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.TaskDao;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.CalendarUtils;

import java.util.ArrayList;
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
     * Retrieves {@link Task}s by list id.
     *
     * @param listId identifier of list
     * @return list of {@link Task}s by list id
     */
    public List<Task> getTasks(Integer listId) {
        return taskDao.readByListId(listId);
    }

    /**
     * Retrieves {@link Task}s
     *
     * @param view view name
     * @return list of {@link Task}s
     */
    public List<Task> getTasksForView(TasksViewEnum view) {
        List<Task> tasks;
        switch (view) {
            case TODAY: {
                tasks = taskDao.readByDueDate(CalendarUtils.getTodayAtZeroDateTimeInMillis(),
                        CalendarUtils.getTodayDateTimeInMillis());
                break;
            }
            case WEEK: {
                tasks = taskDao.readByDueDate(CalendarUtils.getTodayAtZeroDateTimeInMillis(),
                        CalendarUtils.getNextWeekDaysDateTimeInMillis());
                break;
            }
            case OVERDUE: {
                tasks = taskDao.readOverdueTasks();
                break;
            }
            default:
                tasks = new ArrayList<>();
        }
        return tasks;
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
     * Creates new {@link Task} based on {@link Task#getRepeatType()}.
     * Inserts it into database.
     *
     * @param task to repeat
     */
    public void createRepetitiveTask(Task task) {
        if (null != task.getRepeatType()) {
            Task newTask = new Task();
            newTask.setListId(task.getListId());
            newTask.setName(task.getName());
            newTask.setRepeatType(task.getRepeatType());
            newTask.setCreatedDateTime(System.currentTimeMillis());
            newTask.setPosition(task.getPosition());
            newTask.setDueDateTime(CalendarUtils.getTime(task.getDueDateTime(), task.getRepeatType()));
            if (null != task.getRemindDateTime()) {
                newTask.setRemindDateTime(
                        CalendarUtils.getTime(task.getRemindDateTime(), task.getRepeatType()));
            }
            task.setRepeatType(null);
            newTask.setId((int) insert(newTask));
            AlarmService.setTaskReminder(context, newTask);
        }
    }

    /**
     * Inserts {@link Task}s.
     *
     * @param tasks    list of {@link Task}s to insert
     * @param position next available position to insert
     */
    public void duplicate(List<Task> tasks, int position) {
        for (Task task : tasks) {
            task.setId(null);
            task.setPosition(position++);
            task.setDone(false);
            Integer taskId = (int) taskDao.insert(task);
            if (null != task.getRemindDateTime() && System.currentTimeMillis() < task.getRemindDateTime()) {
                task.setId(taskId);
                AlarmService.setTaskReminder(context, task);
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
    public void deleteByListId(Integer listId) {
        List<Task> tasks = taskDao.readByListId(listId);
        for (Task task : tasks) {
            AlarmService.cancelTaskReminder(context, task);
        }
        taskDao.deleteByListId(listId);
    }

    /**
     * Removes specified {@link Task} and reminder for it.
     *
     * @param task instance of {@link Task} to remove
     */
    public void delete(Task task) {
        AlarmService.cancelTaskReminder(context, task);
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
    public int getAvailablePosition(Integer listId) {
        return taskDao.getMaxPosition(listId) + 1;
    }

    /**
     * @return overdue tasks.
     */
    public List<Task> getOverdueTasks() {
        return taskDao.readOverdueTasks();
    }
}
