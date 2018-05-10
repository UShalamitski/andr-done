package com.hose.aureliano.project.done.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;

/**
 * Modal window for task item.
 * <p/>
 * Date: 05.02.2018
 *
 * @author Uladzislau Shalamitski
 */
public class TaskModal extends DialogFragment {

    private int blueColor;
    private int defaultTextColor;
    private int blackColor;
    private TaskDialogListener listener;
    private TextView dueDateText;
    private ImageView dueDateIcon;
    private ImageView clearDueDateIcon;
    private TextView remindDateText;
    private ImageView remindDateIcon;
    private ImageView clearRemindDateIcon;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Title");
        View view = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.modal_task, (ViewGroup) getView(), false);

        Task task = buildTaskFromBundle();
        blueColor = getContext().getResources().getColor(R.color.blue);
        blackColor = getContext().getResources().getColor(R.color.black_secondary);
        defaultTextColor = getContext().getResources().getColor(R.color.text_color);
        dueDateText = view.findViewById(R.id.task_modal_calendar_text);
        dueDateIcon = view.findViewById(R.id.task_modal_calendar_icon);
        clearDueDateIcon = view.findViewById(R.id.task_modal_calendar_clear_icon);
        remindDateText = view.findViewById(R.id.task_modal_alarm_text);
        remindDateIcon = view.findViewById(R.id.task_modal_alarm_icon);
        clearRemindDateIcon = view.findViewById(R.id.task_modal_alert_clear_icon);

        EditText name = view.findViewById(R.id.tasks_modal_name);
        name.setText(task.getName());

        clearDueDateIcon.setOnClickListener(iconView -> {
            changeDueDate(defaultTextColor, blackColor, getString(R.string.task_due_date));
            task.setDueDateTime(null);
            task.setDueTimeIsSet(false);
        });
        clearRemindDateIcon.setOnClickListener(iconView -> {
            changeRemindDate(defaultTextColor, blackColor, getString(R.string.task_remind_me));
            task.setRemindDateTime(null);
            task.setRemindTimeIsSet(false);
            AlarmService.cancelAlarm(getContext(), task);
        });

        if (task.getDueDateTime() != null) {
            changeDueDate(blueColor, blueColor,
                    ActivityUtils.getStringDate(getContext(), task.getDueDateTime(), task.getDueTimeIsSet()));
        }
        if (task.getRemindDateTime() != null) {
            changeRemindDate(blueColor, blueColor,
                    ActivityUtils.getStringDate(getContext(), task.getRemindDateTime(), task.getRemindTimeIsSet()));
        }

        RelativeLayout relativeLayout = view.findViewById(R.id.task_modal_layout_calendar);
        relativeLayout.setOnClickListener(v -> {
            DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getContext(), task.getDueDateTime(),
                    task.getDueTimeIsSet(), (dateTime, isTimeSet) -> {
                changeDueDate(blueColor, blueColor, ActivityUtils.getStringDate(getContext(), dateTime, isTimeSet));
                task.setDueDateTime(dateTime);
                task.setDueTimeIsSet(isTimeSet);
            });
            pickerDialog.show();
        });
        RelativeLayout layoutAlert = view.findViewById(R.id.task_modal_layout_alert);
        layoutAlert.setOnClickListener(v -> {
            DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(getContext(), task.getRemindDateTime(),
                    task.getRemindTimeIsSet(), (dateTime, isTimeSet) -> {
                changeRemindDate(blueColor, blueColor, ActivityUtils.getStringDate(getContext(), dateTime, isTimeSet));
                task.setRemindDateTime(dateTime);
                task.setRemindTimeIsSet(isTimeSet);
            });
            dateTimePickerDialog.show();
        });

        builder.setView(view);
        builder.setNegativeButton(R.string.no, null);
        builder.setPositiveButton(R.string.yes, (dialog, i) -> listener.onDialogPositiveClick(this, task));
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (TaskDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement TaskDialogListener");
        }
    }

    private Task buildTaskFromBundle() {
        Task task = new Task();
        Bundle bundle = getArguments();
        if (bundle != null) {
            task.setId((Integer) bundle.get(Task.Fields.ID.getFieldName()));
            task.setName(bundle.getString(Task.Fields.NAME.getFieldName()));
            task.setDone(bundle.getBoolean(Task.Fields.DONE.getFieldName(), false));
            task.setDueDateTime((Long) bundle.get(Task.Fields.DUE_DATE_TIME.getFieldName()));
            task.setRemindDateTime((Long) bundle.get(Task.Fields.REMIND_DATE_TIME.getFieldName()));
            task.setDueTimeIsSet(bundle.getBoolean(Task.Fields.DUE_TIME_IS_SET.getFieldName(), false));
            task.setRemindTimeIsSet(bundle.getBoolean(Task.Fields.REMIND_TIME_IS_SET.getFieldName(), false));
            task.setPosition((Integer) bundle.get(Task.Fields.POSITION.getFieldName()));
            task.setCreatedDateTime((Long) bundle.get(Task.Fields.CREATED_DATE_TIME.getFieldName()));
        }
        return task;
    }

    private void changeDueDate(int textColor, int iconColor, String text) {
        dueDateText.setText(text);
        dueDateText.setTextColor(textColor);
        dueDateIcon.setColorFilter(iconColor);
    }

    private void changeRemindDate(int color, int iconColor, String text) {
        remindDateText.setText(text);
        remindDateText.setTextColor(color);
        remindDateIcon.setColorFilter(iconColor);
    }

    public interface TaskDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, Task task);
    }
}
