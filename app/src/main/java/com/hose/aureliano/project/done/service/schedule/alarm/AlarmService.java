package com.hose.aureliano.project.done.service.schedule.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.service.schedule.receiver.OverdueTasksReceiver;
import com.hose.aureliano.project.done.service.schedule.receiver.ReminderReceiver;
import com.hose.aureliano.project.done.utils.CalendarUtils;

/**
 * Service that manages an alarms.
 * <p/>
 * Date: 10.03.2018.
 *
 * @author Uladzislau_Shalamitski
 */
public class AlarmService {

    private AlarmService() {
        throw new AssertionError();
    }

    /**
     * Schedule an reminder to be delivered precisely at the specified time.
     *
     * @param context context
     * @param task    instance of {@link Task}
     */
    public static void setTaskReminder(Context context, Task task) {
        if (!task.getDone() && null != task.getRemindDateTime()
                && System.currentTimeMillis() <= task.getRemindDateTime()) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            intent.putExtra(Task.Fields.LIST_ID.fieldName(), task.getListId());
            intent.putExtra(Task.Fields.LIST_NAME.fieldName(), task.getListName());
            intent.putExtra(Task.Fields.NAME.fieldName(), task.getName());
            intent.putExtra(Task.Fields.NOTE.fieldName(), task.getNote());
            intent.putExtra(Task.Fields.ID.fieldName(), task.getId());
            intent.putExtra(Task.Fields.DONE.fieldName(), task.getDone());
            intent.putExtra(Task.Fields.DUE_DATE_TIME.fieldName(), task.getDueDateTime());
            intent.putExtra(Task.Fields.REMIND_DATE_TIME.fieldName(), task.getRemindDateTime());
            intent.putExtra(Task.Fields.CREATED_DATE_TIME.fieldName(), task.getCreatedDateTime());
            intent.putExtra(Task.Fields.POSITION.fieldName(), task.getPosition());
            intent.putExtra(Task.Fields.REPEAT_TYPE.fieldName(),
                    null != task.getRepeatType() ? task.getRepeatType().name() : null);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            setAlarm(context, pendingIntent, task.getRemindDateTime());
        }
    }

    /**
     * Cancel an reminder for specified {@link Task}.
     *
     * @param context context
     * @param task    instance of {@link Task}
     */
    public static void cancelTaskReminder(Context context, Task task) {
        if (null != task.getRemindDateTime()) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent pendingIntent =
                    PendingIntent.getBroadcast(context, task.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (manager != null) {
                manager.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    /**
     * Sets alarm to show reminder about overdue tasks.
     *
     * @param context application context
     */
    public static void setOverdueTasksReminder(Context context) {
        Intent intent = new Intent(context, OverdueTasksReceiver.class);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, 2000017004, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        setAlarm(context, pendingIntent, CalendarUtils.getTimeToShowOverdueTasksReminder());
    }

    private static void setAlarm(Context context, PendingIntent pendingIntent, long timeInMilliseconds) {
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.setExact(AlarmManager.RTC_WAKEUP, timeInMilliseconds, pendingIntent);
        }
    }
}
