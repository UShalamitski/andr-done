package com.hose.aureliano.project.done.service.schedule.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.TasksActivity;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.service.TaskService;

/**
 * Class that receives and handles broadcast intents sent by
 * {@link com.hose.aureliano.project.done.service.schedule.alarm.AlarmService}
 * <p/>
 * Date: 10.03.2018
 *
 * @author Uladzislau Shalamitski
 */
public class ReminderReceiver extends BroadcastReceiver {

    TaskService taskService;

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            taskService = new TaskService(context);
            int taskId = extras.getInt(Task.Fields.ID.fieldName());

            Intent contentIntent = new Intent().setClass(context, TasksActivity.class);;
            contentIntent.putExtra("listId", extras.getString(Task.Fields.LIST_ID.fieldName()));
            contentIntent.putExtra("name", extras.getString(Task.Fields.NAME.fieldName()));
            contentIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(TasksActivity.class);
            stackBuilder.addNextIntent(contentIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(taskId, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "channelId")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.task_notification_title))
                    .setContentText(extras.getString(Task.Fields.NAME.fieldName()))
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(taskId, notificationBuilder.build());
            }
            taskService.deleteReminderDate(taskId);
        }
    }
}
