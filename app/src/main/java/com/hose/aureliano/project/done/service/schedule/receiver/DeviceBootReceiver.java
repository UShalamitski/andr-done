package com.hose.aureliano.project.done.service.schedule.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;

import org.apache.commons.lang3.StringUtils;

/**
 * Broadcast receiver, starts when the device gets starts.
 * Resets the reminders for tasks.
 * <p/>
 * Date: 10.03.2018
 *
 * @author Uladzislau Shalamitski
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    private static final String[] ACTIONS = {
            "android.intent.action.BOOT_COMPLETED",
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            "android.intent.action.MY_PACKAGE_REPLACED"};

    @Override
    public void onReceive(Context context, Intent intent) {
        if (StringUtils.equalsAny(intent.getAction(), ACTIONS)) {
            TaskService taskService = new TaskService(context);
            long currentTime = System.currentTimeMillis();
            for (Task task : taskService.getNotCompletedTasksWithReminder()) {
                if (task.getRemindDateTime() > currentTime) {
                    AlarmService.setAlarm(context, task);
                } else {
                    taskService.deleteReminderDate(task.getId());
                }
            }
        }
    }
}
