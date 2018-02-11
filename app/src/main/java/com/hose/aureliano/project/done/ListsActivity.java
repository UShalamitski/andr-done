package com.hose.aureliano.project.done;

import android.content.ContentValues;
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

import com.hose.aureliano.project.done.repository.DbHelper;
import com.hose.aureliano.project.done.repository.impl.ListsRepository;

import java.util.UUID;

public class ListsActivity extends AppCompatActivity implements AddListModal.NoticeDialogListener {

    private ListsAdapter listsAdapter;
    private ListsRepository listsRepository = new ListsRepository(new DbHelper(this));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);
        //setSupportActionBar(findViewById(R.id.toolbar));

        listsAdapter = new ListsAdapter(this, listsRepository, getSupportFragmentManager());
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
        int id = item.getItemId();
        if (id == R.id.delete_all) {
            int deletedCount = listsRepository.delete();
            listsAdapter.swapCursor(listsRepository.read());
            Toast.makeText(this, "Removed " + deletedCount, Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment fragment) {
        long result = 0;
        EditText name = fragment.getDialog().findViewById(R.id.list_name);

        ContentValues values = new ContentValues(2);
        values.put("name", name.getText().toString());

        Bundle bundle = fragment.getArguments();
        if (null != bundle) {
            values.put("_id", bundle.getString("_id"));
            result = listsRepository.update(values);
        } else {
            values.put("_id", UUID.randomUUID().toString());
            result = listsRepository.insert(values);
        }

        if (result != -1) {
            listsAdapter.swapCursor(listsRepository.read());
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
