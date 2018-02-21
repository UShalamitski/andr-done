package com.hose.aureliano.project.done.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.ListAdapter;
import com.hose.aureliano.project.done.activity.dialog.ListModal;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import java.util.UUID;

/**
 * Activity fot displaying all users lists of TODOs.
 * <p>
 * Date: 12.02.2018.
 *
 * @author evere
 */
public class ListsActivity extends AppCompatActivity implements ListModal.NoticeDialogListener {

    private ListAdapter listsAdapter;
    private DoneListDao doneListDao = DatabaseCreator.getDatabase(this).getDoneListDao();
    View coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        coordinator = findViewById(R.id.coordinator_layout);
        listsAdapter = new ListAdapter(this, getSupportFragmentManager());
        RecyclerView recyclerView = findViewById(R.id.activity_lists_list_view);
        recyclerView.setAdapter(listsAdapter);
/*        petListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, TasksActivity.class);
            intent.putExtra("listId", ((TextView) view.findViewById(R.id.summary)).getText().toString());
            startActivity(intent);
        });*/

        FloatingActionButton fab = findViewById(R.id.activity_lists_fab);
        fab.setOnClickListener(view -> {
            DialogFragment dialogFragment = new ListModal();
            dialogFragment.show(getSupportFragmentManager(), "tag");
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int deletedCount = 0;
        int id = item.getItemId();
        if (id == R.id.delete_all) {
            deletedCount = doneListDao.delete();
            listsAdapter.refresh();
            ActivityUtils.showSnackBar(coordinator, String.format("Deleted: %s", deletedCount));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment fragment) {
        long result = 0;
        EditText name = fragment.getDialog().findViewById(R.id.list_name);

        DoneList doneList = new DoneList();
        doneList.setName(name.getText().toString());

        Bundle bundle = fragment.getArguments();
        if (null != bundle) {
            doneList.setId(bundle.getString("id"));
            result = doneListDao.update(doneList);
        } else {
            doneList.setId(UUID.randomUUID().toString());
            result = doneListDao.insert(doneList);
        }

        if (result != -1) {
            listsAdapter.refresh();
            ActivityUtils.showSnackBar(coordinator, String.format("done: %s", name.getText()));
        } else {
            ActivityUtils.showSnackBar(coordinator, "Oops! Something went wrong!");
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }
}
