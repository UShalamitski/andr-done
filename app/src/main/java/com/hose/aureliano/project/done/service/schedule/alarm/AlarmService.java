package com.hose.aureliano.project.done.service.schedule.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.service.schedule.receiver.AlarmReceiver;

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
     * Schedule an alarm to be delivered precisely at the specified time.
     *
     * @param context context
     * @param task    instance of {@link Task}
     */
    public static void setAlarm(Context context, Task task) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Task.Fields.NAME.getFieldName(), task.getName());
        intent.putExtra(Task.Fields.ID.getFieldName(), task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.setExact(AlarmManager.RTC_WAKEUP, task.getRemindDateTime(), pendingIntent);
        }
    }

    /**
     * Cancel an alarm for specified {@link Task}.
     *
     * @param context context
     * @param task    instance of {@link Task}
     */
    public static void cancelAlarm(Context context, Task task) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Task.Fields.NAME.getFieldName(), task.getName());
        intent.putExtra(Task.Fields.ID.getFieldName(), task.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getId(), intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (manager != null) {
            manager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
