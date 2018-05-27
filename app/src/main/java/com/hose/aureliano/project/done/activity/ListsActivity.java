package com.hose.aureliano.project.done.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.ListAdapter;
import com.hose.aureliano.project.done.activity.callback.DiffUtilCallback;
import com.hose.aureliano.project.done.activity.component.CustomEditText;
import com.hose.aureliano.project.done.activity.component.RecyclerViewEmptySupport;
import com.hose.aureliano.project.done.activity.dialog.ListModal;
import com.hose.aureliano.project.done.activity.helper.ListItemTouchHelper;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.model.TasksViewEnum;
import com.hose.aureliano.project.done.service.ListService;
import com.hose.aureliano.project.done.service.comporator.ListCreateDateComparator;
import com.hose.aureliano.project.done.service.comporator.ListProgressComparator;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;
import com.hose.aureliano.project.done.utils.AnimationUtil;
import com.hose.aureliano.project.done.utils.PreferencesUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Activity fot displaying all users lists of TODOs.
 * <p>
 * Date: 12.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class ListsActivity extends AppCompatActivity implements ListModal.NoticeDialogListener,
        NavigationView.OnNavigationItemSelectedListener {

    private View coordinator;
    private DrawerLayout drawer;
    private ListAdapter listsAdapter;
    private ListService listService = new ListService(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_drawer);

        Toolbar toolbar = findViewById(R.id.lists_toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle =
                new ActionBarDrawerToggle(this, drawer, toolbar, R.string.action_settings, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        coordinator = findViewById(R.id.coordinator_layout);
        listsAdapter = new ListAdapter(this, getSupportFragmentManager());
        RecyclerViewEmptySupport recyclerView = findViewById(R.id.activity_lists_list_view);
        recyclerView.setEmptyView(findViewById(R.id.activity_lists_empty_view));
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
            if (actionId == EditorInfo.IME_ACTION_DONE && StringUtils.isNotBlank(editText.getText().toString())) {
                DoneList list = new DoneList();
                list.setName(editText.getText().toString());
                list.setCreatedDateTime(new Date().getTime());
                list.setPosition(listsAdapter.getAvailablePosition());
                int id = (int) listService.insert(list);
                ActivityUtils.handleDbInteractionResult(id, coordinator, () -> {
                    editText.setText(StringUtils.EMPTY);
                    editText.onKeyPreIme(1, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                    Intent intent = new Intent(this, TasksActivity.class);
                    intent.putExtra("listId", id);
                    intent.putExtra("position", list.getPosition());
                    intent.putExtra("title", list.getName());
                    this.startActivity(intent);
                });
            }
            return true;
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

        new ItemTouchHelper(new ListItemTouchHelper(this, listsAdapter)).attachToRecyclerView(recyclerView);
        configureApplication();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        listsAdapter.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lists, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_lists_sort_name || id == R.id.menu_lists_sort_progress || id == R.id.menu_lists_sort_creation_date) {
            List<DoneList> oldTasks = new ArrayList<>(listsAdapter.getItems());
            if (id == R.id.menu_lists_sort_name) {
                Collections.sort(listsAdapter.getItems(),
                        (list1, list2) -> StringUtils.compare(list1.getName(), list2.getName()));
            } else if (id == R.id.menu_lists_sort_progress) {
                Collections.sort(listsAdapter.getItems(), new ListProgressComparator());
            } else {
                Collections.sort(listsAdapter.getItems(), new ListCreateDateComparator());
            }
            applySortChanges(oldTasks);
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;
        if (id == R.id.navbar_view_today) {
            intent = new Intent(this, TasksActivity.class);
            intent.putExtra("view", TasksViewEnum.TODAY);
            intent.putExtra("title", getString(R.string.navbar_today));
            this.startActivity(intent);
        } else if (id == R.id.navbar_view_week) {
            intent = new Intent(this, TasksActivity.class);
            intent.putExtra("view", TasksViewEnum.WEEK);
            intent.putExtra("title", getString(R.string.navbar_week));
            this.startActivity(intent);
        } else if (id == R.id.navbar_view_overdue) {
            intent = new Intent(this, TasksActivity.class);
            intent.putExtra("view", TasksViewEnum.OVERDUE);
            intent.putExtra("title", getString(R.string.navbar_overdue));
            this.startActivity(intent);
        }

        if (null != intent) {
            new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START), 300);
        } else {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment fragment) {
        long result = 0;
        EditText name = fragment.getDialog().findViewById(R.id.list_name);

        DoneList doneList = new DoneList();
        doneList.setName(name.getText().toString());

        Bundle bundle = fragment.getArguments();
        if (null != bundle) {
            doneList.setId(bundle.getInt("id"));
            doneList.setPosition(bundle.getInt("position"));
            doneList.setCreatedDateTime(bundle.getLong("createdDateTime"));
            result = listService.update(doneList);
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

    private void applySortChanges(List<DoneList> oldTasks) {
        DiffUtilCallback<DoneList> utilCallback = new DiffUtilCallback<>(oldTasks, listsAdapter.getItems());
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(utilCallback);
        diffResult.dispatchUpdatesTo(listsAdapter);
        listsAdapter.updatePositions();
        listService.update(listsAdapter.getItems());
    }

    private void configureApplication() {
        String isFirstRunConfiguredPreferenceName = getString(R.string.preference_isFirstRunConfigured);
        if (!PreferencesUtil.getPreference(this, isFirstRunConfiguredPreferenceName)) {
            AlarmService.setOverdueTasksReminder(this);
            PreferencesUtil.addPreference(this, isFirstRunConfiguredPreferenceName, true);
        }
    }
}
