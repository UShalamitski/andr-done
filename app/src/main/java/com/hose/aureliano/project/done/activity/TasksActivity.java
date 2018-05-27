package com.hose.aureliano.project.done.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.TaskAdapter;
import com.hose.aureliano.project.done.activity.callback.DiffUtilCallback;
import com.hose.aureliano.project.done.activity.component.CustomEditText;
import com.hose.aureliano.project.done.activity.component.RecyclerViewEmptySupport;
import com.hose.aureliano.project.done.activity.dialog.SelectListModal;
import com.hose.aureliano.project.done.activity.dialog.TaskModal;
import com.hose.aureliano.project.done.activity.helper.TaskItemTouchHelper;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.model.TasksViewEnum;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.service.comporator.TaskCreateDateComparator;
import com.hose.aureliano.project.done.service.comporator.TaskDueDateComparator;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;
import com.hose.aureliano.project.done.utils.AnimationUtil;
import com.hose.aureliano.project.done.utils.CalendarUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Activity fot displaying all TODOs for selected list.
 * <p>
 * Date: 12.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class TasksActivity extends AppCompatActivity implements TaskModal.TaskDialogListener {

    private Integer listId;
    private View coordinator;
    private View backgroundView;
    private View bottomView;
    private ViewGroup decorView;
    private TaskAdapter taskAdapter;
    private FloatingActionButton floatingActionButton;
    private TaskService taskService = new TaskService(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Toolbar toolbar = findViewById(R.id.tasks_toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white_24);
        setSupportActionBar(toolbar);

        TasksViewEnum viewEnum = null != getIntent().getExtras().get("view")
                ? (TasksViewEnum) getIntent().getExtras().get("view") : null;
        if (null != viewEnum) {
            setTitle(getString(R.string.view));
            toolbar.setSubtitle(getIntent().getStringExtra("title"));
        } else {
            setTitle(getIntent().getStringExtra("title"));
        }

        listId = getIntent().getIntExtra("listId", 0);
        coordinator = findViewById(R.id.tasks_coordinator_layout);
        taskAdapter = new TaskAdapter(this, getSupportFragmentManager(), listId, viewEnum);

        RecyclerViewEmptySupport listView = findViewById(R.id.activity_tasks_list_view);
        listView.setEmptyView(findViewById(R.id.activity_tasks_empty_view));
        listView.setAdapter(taskAdapter);
        listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        floatingActionButton = findViewById(R.id.activity_tasks_fab);

        bottomView = findViewById(R.id.activity_task_new);
        bottomView.setVisibility(View.GONE);
        bottomView.setOnClickListener(view -> {
        });

        if (TasksViewEnum.OVERDUE != viewEnum) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            CustomEditText newTaskEditText = findViewById(R.id.activity_task_new_edit_text);

            decorView = getWindow().getDecorView().findViewById(R.id.activity_task_to_decor);
            ImageView newIcon = findViewById(R.id.activity_task_new_icon);
            backgroundView = LayoutInflater.from(this).inflate(R.layout.decor_view_layout, null);

            backgroundView.setOnClickListener(view -> {
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
                newTaskEditText.onKeyPreIme(1, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            });

            newTaskEditText.setListener(() -> {
                bottomView.setVisibility(View.GONE);
                AnimationUtil.animateAlphaFadeOut(backgroundView, () -> {
                    decorView.removeView(backgroundView);
                    floatingActionButton.show();
                });
            });

            newTaskEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (StringUtils.isNoneBlank(s)) {
                        newIcon.setColorFilter(getResources().getColor(R.color.green));
                    } else if (StringUtils.isBlank(s)) {
                        newIcon.setColorFilter(getResources().getColor(R.color.darker_gray));
                    }
                }
            });

            newTaskEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE && StringUtils.isNotBlank(newTaskEditText.getText().toString())) {
                    taskService.getAvailablePosition(listId);
                    Task task = new Task();
                    task.setListId(listId);
                    task.setName(newTaskEditText.getText().toString());
                    task.setCreatedDateTime(new Date().getTime());
                    task.setPosition(taskService.getAvailablePosition(listId));
                    if (null != viewEnum) {
                        task.setDueDateTime(CalendarUtils.getTodayDateTimeInMillis());
                    }
                    long id = taskService.insert(task);
                    ActivityUtils.handleDbInteractionResult(id, coordinator, () -> {
                        task.setId((int) id);
                        taskAdapter.getItems().add(task);
                        int adapterPos = taskAdapter.getItems().indexOf(task);
                        taskAdapter.notifyItemInserted(adapterPos);
                        listView.scrollToPosition(adapterPos);
                        newTaskEditText.setText(StringUtils.EMPTY);
                    });
                }
                return true;
            });

            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(view -> {
                if (null != viewEnum) {
                    new SelectListModal(this, listId -> {
                        this.listId = listId;
                        showAddNewTaskView(inputManager, newTaskEditText);
                    });
                } else {
                    showAddNewTaskView(inputManager, newTaskEditText);
                }
                newTaskEditText.requestFocus();
            });
        } else {
            floatingActionButton.setVisibility(View.GONE);
        }

        new ItemTouchHelper(new TaskItemTouchHelper(this, taskAdapter, coordinator)).attachToRecyclerView(listView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onSupportActionModeStarted(@NonNull ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        if (null != floatingActionButton) {
            floatingActionButton.hide();
        }
    }

    @Override
    public void onSupportActionModeFinished(@NonNull ActionMode mode) {
        super.onSupportActionModeFinished(mode);
        if (null != floatingActionButton) {
            floatingActionButton.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_tasks_sort_name || id == R.id.menu_tasks_sort_due_date || id == R.id.menu_tasks_sort_create_date) {
            List<Task> oldTasks = new ArrayList<>(taskAdapter.getItems());
            if (id == R.id.menu_tasks_sort_name) {
                Collections.sort(taskAdapter.getItems(), (t1, t2) -> StringUtils.compare(t1.getName(), t2.getName()));
            } else if (id == R.id.menu_tasks_sort_due_date) {
                Collections.sort(taskAdapter.getItems(), new TaskDueDateComparator());
            } else {
                Collections.sort(taskAdapter.getItems(), new TaskCreateDateComparator());
            }
            applySortChanges(oldTasks);
        }
        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment fragment, Task task) {
        TextView name = fragment.getDialog().findViewById(R.id.tasks_modal_name);
        task.setName(name.getText().toString());
        task.setListId(listId);
        ActivityUtils.handleDbInteractionResult(taskService.update(task), coordinator, () -> {
            taskAdapter.refresh();
            if (task.getRemindTimeIsSet()) {
                AlarmService.setTaskReminder(this, task);
            }
        });
    }

    private void showAddNewTaskView(InputMethodManager inputManager, CustomEditText newTaskEditText) {
        bottomView.setVisibility(View.VISIBLE);
        newTaskEditText.requestFocus();
        if (null != inputManager) {
            floatingActionButton.hide();
            inputManager.showSoftInput(newTaskEditText, InputMethodManager.SHOW_IMPLICIT);
            AnimationUtil.animateAlphaFadeIn(backgroundView);
            decorView.addView(backgroundView);
        }
    }

    private void applySortChanges(List<Task> oldTasks) {
        DiffUtilCallback<Task> utilCallback = new DiffUtilCallback<>(oldTasks, taskAdapter.getItems());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(utilCallback);
        diffResult.dispatchUpdatesTo(taskAdapter);
        taskAdapter.updatePositions();
        taskService.update(taskAdapter.getItems());
    }
}
