package com.hose.aureliano.project.done.activity;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.dialog.DateTimePickerDialog;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.model.TaskRepeatEnum;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;
import com.hose.aureliano.project.done.utils.CalendarUtils;

import org.apache.commons.lang3.StringUtils;

public class TaskDetailsActivity extends AppCompatActivity {

    private static int COLOR_BLACK_PRIMARY;
    private static int COLOR_BLACK_SECONDARY;
    private static int COLOR_BLUE;
    private static int COLOR_RED;
    private static Drawable DRAWABLE_DONE;
    private static Drawable DRAWABLE_CANCEL;

    private TaskService taskService = new TaskService(this);

    private ImageView doneCancelButtonIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details_activity);
        initStaticResources();

        Bundle extras = getIntent().getExtras();
        Task task;
        if (null != extras) {
            task = buildTask(extras);
            setTitle(task.getListName());

            EditText taskNameView = findViewById(R.id.task_details_main_text);
            taskNameView.setText(task.getName());
            prepareTaskName(taskNameView, task.getDone());
            taskNameView.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    taskNameView.clearFocus();
                }
                return true;
            });
            taskNameView.setOnFocusChangeListener((View view, boolean hasFocus) -> {
                if (!hasFocus) {
                    task.setName(StringUtils.trimToEmpty(taskNameView.getText().toString()));
                    taskService.update(task);
                }
            });

            View repeatView = findViewById(R.id.task_details_repeat);
            ImageView repeatIcon = findViewById(R.id.task_details_repeat_icon);
            TextView repeatText = findViewById(R.id.task_details_repeat_text);
            View repeatClearView = findViewById(R.id.task_details_repeat_clear);
            changeRepeatFields(repeatText, repeatIcon, task);
            repeatClearView.setOnClickListener(view -> {
                task.setRepeatType(null);
                changeRepeatFieldsAndSave(repeatText, repeatIcon, task);
            });
            repeatView.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.inflate(R.menu.menu_repeat);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_task_repeat_every_day:
                            task.setRepeatType(TaskRepeatEnum.EVERY_DAY);
                            break;
                        case R.id.menu_task_repeat_every_week:
                            task.setRepeatType(TaskRepeatEnum.EVERY_WEEK);
                            break;
                        case R.id.menu_task_repeat_every_month:
                            task.setRepeatType(TaskRepeatEnum.EVERY_MONTH);
                            break;
                        case R.id.menu_task_repeat_every_year:
                            task.setRepeatType(TaskRepeatEnum.EVERY_YEAR);
                            break;
                        case R.id.menu_task_repeat_working_days:
                            task.setRepeatType(TaskRepeatEnum.WORKING_DAYS);
                            break;
                    }
                    changeRepeatFieldsAndSave(repeatText, repeatIcon, task);
                    return true;
                });
                popupMenu.show();
            });

            View dueDateView = findViewById(R.id.task_details_due_date);
            ImageView dueDateIcon = findViewById(R.id.task_details_due_date_icon);
            TextView dueDateText = findViewById(R.id.task_details_due_date_text);
            View dueDateClearView = findViewById(R.id.task_details_due_date_clear);
            changeDueDateFields(dueDateText, dueDateIcon, repeatView, task);
            dueDateClearView.setOnClickListener(view -> {
                task.setDueDateTime(null);
                task.setRepeatType(null);
                repeatView.setVisibility(View.GONE);
                changeDueDateFieldsAndSave(dueDateText, dueDateIcon, repeatView, task);
            });
            dueDateView.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.inflate(R.menu.menu_due_date);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_tasks_due_date_today:
                            task.setDueDateTime(CalendarUtils.getTodayDateTimeInMillis());
                            changeDueDateFieldsAndSave(dueDateText, dueDateIcon, repeatView, task);
                            break;
                        case R.id.menu_tasks_due_date_tomorrow:
                            task.setDueDateTime(CalendarUtils.getTomorrowDateTimeInMillis());
                            changeDueDateFieldsAndSave(dueDateText, dueDateIcon, repeatView, task);
                            break;
                        case R.id.menu_tasks_due_date_next_week:
                            task.setDueDateTime(CalendarUtils.getNextMondayDateTimeInMillis());
                            changeDueDateFieldsAndSave(dueDateText, dueDateIcon, repeatView, task);
                            break;
                        case R.id.menu_tasks_select_due_date:
                            ActivityUtils.showDatePickerDialog(this, (v, year, month, dayOfMonth) -> {
                                task.setDueDateTime(CalendarUtils.getTimeInMillis(year, month, dayOfMonth));
                                changeDueDateFieldsAndSave(dueDateText, dueDateIcon, repeatView, task);
                            }, System.currentTimeMillis());
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });

            View remindDateView = findViewById(R.id.task_details_remind_date);
            ImageView remindDateIcon = findViewById(R.id.task_details_remind_date_icon);
            TextView remindDateText = findViewById(R.id.task_details_remind_date_text);
            View remindDateClearView = findViewById(R.id.task_details_remind_date_clear);
            changeRemindDateFields(remindDateText, remindDateIcon, task);
            remindDateClearView.setOnClickListener(view -> {
                task.setRemindDateTime(null);
                AlarmService.cancelTaskReminder(this, task);
                changeRemindDateFieldsAndSave(remindDateText, remindDateIcon, task);
            });
            remindDateView.setOnClickListener(view -> {
                PopupMenu popupMenu = new PopupMenu(this, view);
                popupMenu.inflate(R.menu.menu_remind_date);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_tasks_remind_date_tomorrow:
                            task.setRemindDateTime(CalendarUtils.getTomorrowAtNineDateTimeInMillis());
                            AlarmService.setTaskReminder(this, task);
                            changeRemindDateFieldsAndSave(remindDateText, remindDateIcon, task);
                            break;
                        case R.id.menu_tasks_remind_date_next_week:
                            task.setRemindDateTime(CalendarUtils.getNextMondayAtNineDateTimeInMillis());
                            AlarmService.setTaskReminder(this, task);
                            changeRemindDateFieldsAndSave(remindDateText, remindDateIcon, task);
                            break;
                        case R.id.menu_tasks_select_remind_date:
                            DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog(this,
                                    task.getRemindDateTime(), dateTime -> {
                                task.setRemindDateTime(dateTime);
                                AlarmService.setTaskReminder(this, task);
                                changeRemindDateFieldsAndSave(remindDateText, remindDateIcon, task);
                            });
                            dateTimePickerDialog.show();
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });

            CheckBox checkBox = findViewById(R.id.task_details_main_checkbox);
            checkBox.setChecked(task.getDone());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (task.getDone() != isChecked) {
                    task.setDone(buttonView.isChecked());
                }
                if (isChecked) {
                    AlarmService.cancelTaskReminder(this, task);
                    if (null != task.getRepeatType()) {
                        taskService.createRepetitiveTask(task);
                        changeRepeatFields(repeatText, repeatIcon, task);
                    }
                } else {
                    AlarmService.setTaskReminder(this, task);
                }
                prepareTaskName(taskNameView, isChecked);
                doneCancelButtonIcon.setImageDrawable(isChecked ? DRAWABLE_CANCEL : DRAWABLE_DONE);
                changeRemindDateFields(remindDateText, remindDateIcon, task);
                changeDueDateFields(dueDateText, dueDateIcon, repeatView, task);
                taskService.update(task);
                ActivityUtils.vibrate(this);
            });

            EditText taskNoteView = findViewById(R.id.task_details_note_text);
            taskNoteView.setText(task.getNote());
            taskNoteView.setOnFocusChangeListener((View view, boolean hasFocus) -> {
                if (!hasFocus) {
                    task.setNote(taskNoteView.getText().toString());
                    taskService.update(task);
                }
            });

            View doneCancelButton = findViewById(R.id.task_detail_bottom_bar_done);
            doneCancelButtonIcon = findViewById(R.id.task_detail_bottom_bar_done_icon);
            doneCancelButtonIcon.setImageDrawable(task.getDone() ? DRAWABLE_CANCEL : DRAWABLE_DONE);
            doneCancelButton.setOnClickListener(view -> {
                checkBox.setChecked(!task.getDone());
            });

            View deleteButton = findViewById(R.id.task_detail_bottom_bar_delete);
            deleteButton.setOnClickListener(view -> {
                ActivityUtils.showConfirmationDialog(this, R.string.task_details_delete_confirmation,
                        (dialog, which) -> {
                            taskService.delete(task);
                            onBackPressed();
                        });
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Task buildTask(Bundle extras) {
        Task task = new Task();
        task.setId((Integer) extras.get(Task.Fields.ID.fieldName()));
        task.setListId((Integer) extras.get(Task.Fields.LIST_ID.fieldName()));
        task.setListName(extras.getString(Task.Fields.LIST_NAME.fieldName(), StringUtils.EMPTY));
        task.setName(extras.getString(Task.Fields.NAME.fieldName(), StringUtils.EMPTY));
        task.setNote(extras.getString(Task.Fields.NOTE.fieldName(), StringUtils.EMPTY));
        task.setDone(extras.getBoolean(Task.Fields.DONE.fieldName(), false));
        task.setDueDateTime((Long) extras.get(Task.Fields.DUE_DATE_TIME.fieldName()));
        task.setRemindDateTime((Long) extras.get(Task.Fields.REMIND_DATE_TIME.fieldName()));
        task.setCreatedDateTime((Long) extras.get(Task.Fields.CREATED_DATE_TIME.fieldName()));
        task.setPosition((Integer) extras.get(Task.Fields.POSITION.fieldName()));
        String repeatType = extras.getString(Task.Fields.REPEAT_TYPE.fieldName());
        task.setRepeatType(null != repeatType ? TaskRepeatEnum.valueOf(repeatType) : null);
        return task;
    }

    private void crossOutText(EditText view, boolean checked) {
        view.setPaintFlags(checked
                ? view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : view.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private void prepareTaskName(EditText taskNameView, boolean isChecked) {
        crossOutText(taskNameView, isChecked);
        taskNameView.setTextColor(isChecked ? COLOR_BLACK_SECONDARY : COLOR_BLACK_PRIMARY);
    }

    private void changeRepeatFields(TextView repeatDateText, ImageView repeatDateIcon, Task task) {
        if (null == task.getRepeatType()) {
            repeatDateText.setTextColor(COLOR_BLACK_SECONDARY);
            repeatDateIcon.setColorFilter(COLOR_BLACK_SECONDARY);
            repeatDateText.setText(getString(R.string.task_repeat));
            task.setRepeatType(null);
        } else {
            repeatDateText.setTextColor(COLOR_BLUE);
            repeatDateIcon.setColorFilter(COLOR_BLUE);
            repeatDateText.setText(getRepeatFieldString(task.getRepeatType()));
        }
    }

    private void changeRepeatFieldsAndSave(TextView remindDateText, ImageView remindDateIcon, Task task) {
        changeRepeatFields(remindDateText, remindDateIcon, task);
        taskService.update(task);
    }

    private String getRepeatFieldString(TaskRepeatEnum repeatEnum) {
        String remindText;
        switch (repeatEnum) {
            case EVERY_DAY:
                remindText = getString(R.string.menu_repeat_every_day);
                break;
            case EVERY_WEEK:
                remindText = getString(R.string.menu_repeat_every_week);
                break;
            case EVERY_MONTH:
                remindText = getString(R.string.menu_repeat_every_month);
                break;
            case EVERY_YEAR:
                remindText = getString(R.string.menu_repeat_every_year);
                break;
            case WORKING_DAYS:
                remindText = getString(R.string.menu_repeat_working_days);
                break;
            default:
                remindText = getString(R.string.task_repeat);
        }
        return remindText;
    }

    private void changeRemindDateFields(TextView remindDateText, ImageView remindDateIcon, Task task) {
        if (null != task.getRemindDateTime() && System.currentTimeMillis() < task.getRemindDateTime()) {
            if (task.getDone()) {
                remindDateText.setTextColor(COLOR_BLACK_SECONDARY);
                remindDateIcon.setColorFilter(COLOR_BLACK_SECONDARY);
            } else {
                remindDateText.setTextColor(COLOR_BLUE);
                remindDateIcon.setColorFilter(COLOR_BLUE);
            }
            remindDateText.setText(ActivityUtils.getStringDate(this, task.getRemindDateTime(), true));
        } else {
            remindDateText.setTextColor(COLOR_BLACK_SECONDARY);
            remindDateIcon.setColorFilter(COLOR_BLACK_SECONDARY);
            remindDateText.setText(getString(R.string.task_remind_me));
            task.setRemindDateTime(null);
        }
    }

    private void changeRemindDateFieldsAndSave(TextView remindDateText, ImageView remindDateIcon, Task task) {
        changeRemindDateFields(remindDateText, remindDateIcon, task);
        taskService.update(task);
    }

    private void changeDueDateFields(TextView dueDateText, ImageView dueDateIcon, View repeatView, Task task) {
        if (null != task.getDueDateTime()) {
            if (System.currentTimeMillis() > task.getDueDateTime() && !task.getDone()) {
                dueDateText.setTextColor(COLOR_RED);
                dueDateIcon.setColorFilter(COLOR_RED);
            } else {
                dueDateText.setTextColor(COLOR_BLUE);
                dueDateIcon.setColorFilter(COLOR_BLUE);
            }
            repeatView.setVisibility(View.VISIBLE);
            dueDateText.setText(ActivityUtils.getStringDate(this, task.getDueDateTime(), false));
        } else {
            repeatView.setVisibility(View.GONE);
            dueDateText.setTextColor(COLOR_BLACK_SECONDARY);
            dueDateIcon.setColorFilter(COLOR_BLACK_SECONDARY);
            dueDateText.setText(getString(R.string.task_due_date));
        }
    }

    private void changeDueDateFieldsAndSave(TextView dueDateText, ImageView dueDateIcon, View repeatView, Task task) {
        changeDueDateFields(dueDateText, dueDateIcon, repeatView, task);
        taskService.update(task);
    }

    private void initStaticResources() {
        if (0 == COLOR_BLUE) {
            COLOR_BLUE = ContextCompat.getColor(this, R.color.blue);
        }
        if (0 == COLOR_RED) {
            COLOR_RED = ContextCompat.getColor(this, R.color.red);
        }
        if (0 == COLOR_BLACK_PRIMARY) {
            COLOR_BLACK_PRIMARY = ContextCompat.getColor(this, R.color.black_primary);
        }
        if (0 == COLOR_BLACK_SECONDARY) {
            COLOR_BLACK_SECONDARY = ContextCompat.getColor(this, R.color.black_secondary);
        }
        if (null == DRAWABLE_CANCEL) {
            DRAWABLE_CANCEL = ContextCompat.getDrawable(this, R.drawable.icon_cancel_white_24dp);
        }
        if (null == DRAWABLE_DONE) {
            DRAWABLE_DONE = ContextCompat.getDrawable(this, R.drawable.icon_done_white_24dp);
        }
    }
}
