package com.hose.aureliano.project.done.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.ListAdapter;
import com.hose.aureliano.project.done.activity.component.CustomEditText;
import com.hose.aureliano.project.done.activity.dialog.ListModal;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;
import com.hose.aureliano.project.done.utils.ActivityUtils;
import com.hose.aureliano.project.done.utils.AnimationUtil;

import org.apache.commons.lang3.StringUtils;

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

        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        FloatingActionButton floatingActionButton = findViewById(R.id.activity_lists_fab);
        CustomEditText editText = findViewById(R.id.activity_lists_new_edit_text);
        View bottomView = findViewById(R.id.activity_lists_new);
        ViewGroup decorView = getWindow().getDecorView().findViewById(R.id.qwer);
        ImageView newIcon = findViewById(R.id.activity_list_new_icon);
        View background = LayoutInflater.from(this).inflate(R.layout.decor_view_layout, null);

        bottomView.setOnClickListener(view -> {
        });
        background.setOnClickListener(view -> {
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            editText.onKeyPreIme(1, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
        });
        bottomView.setVisibility(View.GONE);
        editText.setListener(() -> {
            bottomView.setVisibility(View.GONE);
            AnimationUtil.animateAlphaFadeOut(background, () -> {
                decorView.removeView(background);
                floatingActionButton.show();
            });
        });
        editText.addTextChangedListener(new TextWatcher() {
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
        editText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                DoneList list = new DoneList();
                list.setName(editText.getText().toString());
                list.setId(UUID.randomUUID().toString());
                ActivityUtils.handleDbInteractionResult(doneListDao.insert(list), coordinator, () -> {
                    listsAdapter.refresh();
                    editText.setText("");
                    Intent intent = new Intent(this, TasksActivity.class);
                    intent.putExtra("listId", list.getId());
                    intent.putExtra("name", list.getName());
                    this.startActivity(intent);
                });
                return true;
            }
            return false;
        });
        floatingActionButton.setOnClickListener(view -> {
            bottomView.setVisibility(View.VISIBLE);
            editText.requestFocus();
            if (null != inputManager) {
                floatingActionButton.hide();
                inputManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
                AnimationUtil.animateAlphaFadeIn(background);
                decorView.addView(background);
            }
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
