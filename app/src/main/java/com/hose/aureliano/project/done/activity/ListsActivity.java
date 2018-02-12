package com.hose.aureliano.project.done.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.hose.aureliano.project.done.activity.dialog.AddListModal;
import com.hose.aureliano.project.done.activity.adapter.ListsAdapter;
import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;

import java.util.UUID;

public class ListsActivity extends AppCompatActivity implements AddListModal.NoticeDialogListener {

    private ListsAdapter listsAdapter;
    private DoneListDao doneListDao = DatabaseCreator.getDatabase(this).getDoneListDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        listsAdapter = new ListsAdapter(this, getSupportFragmentManager());
        ListView petListView = findViewById(R.id.lists);
        petListView.setAdapter(listsAdapter);
        petListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, ItemsActivity.class);
            startActivity(intent);
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            DialogFragment dialogFragment = new AddListModal();
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
            Toast.makeText(this, "Removed " + deletedCount, Toast.LENGTH_LONG).show();
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
            Toast.makeText(fragment.getContext(), "yeap : " + name.getText(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(fragment.getContext(), "Oops! Something went wrong!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        EditText name = dialog.getDialog().findViewById(R.id.list_name);
        Toast.makeText(dialog.getContext(), "nope : " + name.getText(), Toast.LENGTH_SHORT).show();
    }
}
