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
import com.hose.aureliano.project.done.utils.ActivityUtils;

/**
 * Created by everest on 05.02.2018.
 */
public class TaskModal extends DialogFragment {

    private int blueColor;
    private TaskDialogListener listener;
    private TextView dueDateText;
    private ImageView dueDateIcon;
    private TextView remindDateText;
    private ImageView remindDateIcon;

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
        dueDateText = view.findViewById(R.id.task_modal_calendar_text);
        dueDateIcon = view.findViewById(R.id.task_modal_calendar_icon);
        remindDateText = view.findViewById(R.id.task_modal_alarm_text);
        remindDateIcon = view.findViewById(R.id.task_modal_alarm_icon);

        EditText name = view.findViewById(R.id.tasks_modal_name);
        name.setText(task.getName());

        if (task.getDueDateTime() != null) {
            changeColorDueDate(blueColor, task.getDueDateTime(), task.getDueTimeIsSet());
        }
        if (task.getRemindDateTime() != null) {
            changeColorRemindDate(blueColor, task.getRemindDateTime(), task.getRemindTimeIsSet());
        }

        RelativeLayout relativeLayout = view.findViewById(R.id.task_modal_layout_calendar);
        relativeLayout.setOnClickListener(v -> {
            DateTimePickerDialog pickerDialog = new DateTimePickerDialog(getContext(), task.getDueDateTime(),
                    task.getDueTimeIsSet(), (dateTime, isTimeSet) -> {
                changeColorDueDate(blueColor, dateTime, isTimeSet);
                task.setDueDateTime(dateTime);
                task.setDueTimeIsSet(isTimeSet);
            });
            pickerDialog.show();
        });
        RelativeLayout layoutAlert = view.findViewById(R.id.task_modal_layout_alert);
        layoutAlert.setOnClickListener(v -> {
            DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(getContext(), task.getRemindDateTime(),
                    task.getRemindTimeIsSet(), (dateTime, isTimeSet) -> {
                changeColorRemindDate(blueColor, dateTime, isTimeSet);
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
            task.setId(bundle.getString(Task.Fields.ID.getFieldName()));
            task.setName(bundle.getString(Task.Fields.NAME.getFieldName()));
            task.setDone(bundle.getBoolean(Task.Fields.DONE.getFieldName(), false));
            task.setDueDateTime((Long) bundle.get(Task.Fields.DUE_DATE_TIME.getFieldName()));
            task.setRemindDateTime((Long) bundle.get(Task.Fields.REMIND_DATE_TIME.getFieldName()));
            task.setDueTimeIsSet(bundle.getBoolean(Task.Fields.DUE_TIME_IS_SET.getFieldName(), false));
            task.setRemindTimeIsSet(bundle.getBoolean(Task.Fields.REMIND_TIME_IS_SET.getFieldName(), false));
        }
        return task;
    }

    private void changeColorDueDate(int color, long dateAndTimeInMilliseconds, boolean isTimeSet) {
        dueDateText.setText(ActivityUtils.getStringDate(getContext(), dateAndTimeInMilliseconds, isTimeSet));
        dueDateText.setTextColor(color);
        dueDateIcon.setColorFilter(color);
    }

    private void changeColorRemindDate(int color, long dateAndTimeInMilliseconds, boolean isTimeSet) {
        remindDateText.setText(ActivityUtils.getStringDate(getContext(), dateAndTimeInMilliseconds, isTimeSet));
        remindDateText.setTextColor(color);
        remindDateIcon.setColorFilter(color);
    }

    public interface TaskDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, Task task);
    }
}
