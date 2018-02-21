package com.hose.aureliano.project.done.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.TaskAdapter;
import com.hose.aureliano.project.done.activity.dialog.ListModal;
import com.hose.aureliano.project.done.activity.dialog.TaskModal;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.TaskDao;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import java.util.UUID;

/**
 * Activity fot displaying all TODOs for selected list.
 * <p>
 * Date: 12.02.2018.
 *
 * @author evere
 */
public class TasksActivity extends AppCompatActivity implements ListModal.NoticeDialogListener {

    private String listId;
    private View coordinator;
    private TaskAdapter taskAdapter;
    private TaskDao taskDao = DatabaseCreator.getDatabase(this).getTaskDao();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        listId = getIntent().getExtras().get("listId").toString();
        coordinator = findViewById(R.id.tasks_coordinator_layout);
        taskAdapter = new TaskAdapter(this, getSupportFragmentManager(), listId);
        RecyclerView listView = findViewById(R.id.activity_tasks_list_view);
        listView.setAdapter(taskAdapter);
        FloatingActionButton fab = findViewById(R.id.activity_tasks_fab);
        fab.setOnClickListener(view -> {
            DialogFragment dialogFragment = new TaskModal();
            dialogFragment.show(getSupportFragmentManager(), "tag");
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment fragment) {
        long result = 0;
        TextView name = fragment.getDialog().findViewById(R.id.tasks_modal_name);

        Task task = new Task();
        task.setListId(listId);
        task.setName(name.getText().toString());

        Bundle bundle = fragment.getArguments();
        if (null != bundle) {
            task.setId(bundle.getString("id"));
            task.setDone(Boolean.valueOf(bundle.getString("done")));
            result = taskDao.update(task);
        } else {
            task.setId(UUID.randomUUID().toString());
            result = taskDao.insert(task);
        }

        if (result != -1) {
            taskAdapter.refresh();
            ActivityUtils.showSnackBar(coordinator, String.format("done: %s", name.getText()));
        } else {
            ActivityUtils.showSnackBar(coordinator, "Oops! Something went wrong!");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}
