package com.hose.aureliano.project.done.service.schedule.receiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.TasksActivity;
import com.hose.aureliano.project.done.model.TasksViewEnum;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;

import org.apache.commons.collections4.CollectionUtils;

/**
 * Class that creates and shows a notification about overdue tasks.
 * <p/>
 * Date: 20.05.2018
 *
 * @author Uladzislau Shalamitski
 */
public class OverdueTasksReceiver extends BroadcastReceiver {

    public static final int PENDING_INTENT_ID = 2000017000;

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent) {
        int overdueTasksCount = CollectionUtils.size(new TaskService(context).getOverdueTasks());
        if (0 < overdueTasksCount) {
            Intent contentIntent = new Intent().setClass(context, TasksActivity.class);
            contentIntent.putExtra("view", TasksViewEnum.OVERDUE);
            contentIntent.putExtra("title", context.getString(R.string.navbar_overdue));
            contentIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(TasksActivity.class);
            stackBuilder.addNextIntent(contentIntent);

            PendingIntent pendingIntent = stackBuilder.getPendingIntent(PENDING_INTENT_ID, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "channelId")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.tasks_overdue_notification_title, overdueTasksCount))
                    .setContentText(context.getString(R.string.tasks_overdue_notification_text))
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(PENDING_INTENT_ID, notificationBuilder.build());
            }
        }
        AlarmService.setOverdueTasksReminder(context);
    }
}
