package com.hose.aureliano.project.done.activity;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.utils.ActivityUtils;

public class TaskDetailsActivity extends AppCompatActivity {

    private static int COLOR_BLACK_PRIMARY;
    private static int COLOR_BLACK_SECONDARY;
    private static Drawable DRAWABLE_DONE;
    private static Drawable DRAWABLE_CANCEL;

    private TaskService taskService = new TaskService(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_details_activity);
        initStaticResources();

        Bundle extras = getIntent().getExtras();
        Task task;
        if (null != extras) {
            task = buildTask(extras);
            task.setListId(extras.getInt("listId"));

            EditText taskNameView = findViewById(R.id.task_details_main_text);
            taskNameView.setText(task.getName());
            prepareTaskName(taskNameView, task.getDone());


            CheckBox checkBox = findViewById(R.id.task_details_main_checkbox);
            checkBox.setChecked(task.getDone());
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (task.getDone() != isChecked) {
                    task.setDone(buttonView.isChecked());
                    taskService.update(task);
                }
                prepareTaskName(taskNameView, isChecked);
            });

            View doneCancelButton = findViewById(R.id.task_detail_bottom_bar_done);
            ImageView doneCancelButtonIcon = findViewById(R.id.task_detail_bottom_bar_done_icon);
            doneCancelButtonIcon.setImageDrawable(task.getDone() ? DRAWABLE_CANCEL : DRAWABLE_DONE);
            doneCancelButton.setOnClickListener(view -> {
                checkBox.setChecked(!task.getDone());
                doneCancelButtonIcon.setImageDrawable(task.getDone() ? DRAWABLE_CANCEL : DRAWABLE_DONE);
            });

            View deleteButton = findViewById(R.id.task_detail_bottom_bar_delete);
            deleteButton.setOnClickListener(view -> {
                ActivityUtils.showConfirmationDialog(this, R.string.task_delete_confirmation,
                        (dialog, which) -> {
                            taskService.delete(task);
                            onBackPressed();
                        });
            });
        }
    }

    private Task buildTask(Bundle extras) {
        Task task = new Task();
        task.setId(extras.getInt("taskId"));
        task.setName(extras.getString("taskName"));
        task.setDone(extras.getBoolean("done", false));
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

    private void initStaticResources() {
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
