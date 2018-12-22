package com.hose.aureliano.project.done.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.NavListAdapter;
import com.hose.aureliano.project.done.activity.adapter.TaskAdapter;
import com.hose.aureliano.project.done.activity.callback.DiffUtilCallback;
import com.hose.aureliano.project.done.activity.component.CustomEditText;
import com.hose.aureliano.project.done.activity.component.CustomTextWatcher;
import com.hose.aureliano.project.done.activity.component.RecyclerViewEmptySupport;
import com.hose.aureliano.project.done.activity.dialog.ListModal;
import com.hose.aureliano.project.done.activity.dialog.SelectListModal;
import com.hose.aureliano.project.done.activity.helper.TaskItemTouchHelper;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.model.TasksViewEnum;
import com.hose.aureliano.project.done.service.ListService;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.service.comporator.TaskCreateDateComparator;
import com.hose.aureliano.project.done.service.comporator.TaskDueDateComparator;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;
import com.hose.aureliano.project.done.utils.AnimationUtil;
import com.hose.aureliano.project.done.utils.CalendarUtils;
import com.hose.aureliano.project.done.utils.PreferencesUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity fot displaying all TODOs for selected list.
 * <p>
 * Date: 12.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class TasksActivity extends AppCompatActivity {

    private static int COLOR_LIGHT_GRAY;
    private static int COLOR_BACKGROUND;

    private Integer listId;
    private String listName;
    private View coordinator;
    private View backgroundView;
    private View bottomView;
    private ViewGroup decorView;
    private TaskAdapter taskAdapter;
    private NavListAdapter navListAdapter;
    private TasksViewEnum viewEnum;
    private RecyclerViewEmptySupport recyclerView;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private DrawerLayout drawer;

    private TaskService taskService;
    private ListService listService;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private SparseBooleanArray sortMap = new SparseBooleanArray();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists_drawer);
        taskService = new TaskService(this);
        listService = new ListService(this);

        toolbar = findViewById(R.id.tasks_toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white_24);
        setSupportActionBar(toolbar);

        initStaticResources();
        configureApplication();
        initNavBar();

        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            viewEnum = null != extras.get("view") ? (TasksViewEnum) extras.get("view") : null;
            if (null != viewEnum) {
                setTitle(getString(R.string.view));
                toolbar.setSubtitle(extras.getString("title"));
            } else {
                listName = extras.getString("title");
                setTitle(listName);
            }
            listId = (Integer) extras.get(Task.Fields.LIST_ID.fieldName());
        }

        coordinator = findViewById(R.id.tasks_coordinator_layout);
        taskAdapter = new TaskAdapter(this, listId, viewEnum);

        recyclerView = findViewById(R.id.activity_tasks_list_view);
        recyclerView.setEmptyView(findViewById(R.id.activity_tasks_empty_view));
        recyclerView.setAdapter(taskAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

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

            newTaskEditText.addTextChangedListener(new CustomTextWatcher(text -> {
                newIcon.setColorFilter(getResources().getColor(
                        StringUtils.isBlank(text) ? R.color.darker_gray : R.color.green));
            }));

            newTaskEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        && StringUtils.isNotBlank(newTaskEditText.getText().toString())) {
                    Task task = new Task();
                    task.setListId(listId);
                    task.setListName(listName);
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
                        recyclerView.scrollToPosition(adapterPos);
                        newTaskEditText.setText(StringUtils.EMPTY);
                    });
                }
                return true;
            });

            floatingActionButton.setVisibility(View.VISIBLE);
            floatingActionButton.setOnClickListener(view -> {
                if (null != viewEnum) {
                    new SelectListModal(this, (listId, listName) -> {
                        this.listName = listName;
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

        sortMap.append(R.id.menu_tasks_sort_name, true);
        sortMap.append(R.id.menu_tasks_sort_due_date, true);
        sortMap.append(R.id.menu_tasks_sort_completed, true);
        sortMap.append(R.id.menu_tasks_sort_create_date, true);

        if (null != extras && null != extras.get(Task.Fields.ID.fieldName())) {
            Intent intent = new Intent(this, TaskDetailsActivity.class);
            intent.putExtras(extras);
            startActivity(intent);
        }

        new ItemTouchHelper(new TaskItemTouchHelper(this, taskAdapter, coordinator)).attachToRecyclerView(recyclerView);
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
        MenuItem item = menu.findItem(R.id.menu_list_delete);
        item.setVisible(null == viewEnum);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean sortDirection;
        switch (item.getItemId()) {
            case R.id.menu_tasks_sort_name:
                sortDirection = getAndRevertSortDirection(R.id.menu_tasks_sort_name);
                executor.submit(() -> this.runOnUiThread(() -> applySortChanges((task1, task2) -> sortDirection
                        ? StringUtils.compare(task1.getName(), task2.getName())
                        : StringUtils.compare(task2.getName(), task1.getName()))));
                break;
            case R.id.menu_tasks_sort_completed:
                sortDirection = getAndRevertSortDirection(R.id.menu_tasks_sort_completed);
                executor.submit(() -> this.runOnUiThread(() -> applySortChanges((task1, task2) -> sortDirection
                        ? Boolean.compare(task1.getDone(), task2.getDone())
                        : Boolean.compare(task2.getDone(), task1.getDone()))));
                break;
            case R.id.menu_tasks_sort_due_date:
                sortDirection = getAndRevertSortDirection(R.id.menu_tasks_sort_due_date);
                executor.submit(() -> this.runOnUiThread(
                        () -> applySortChanges(new TaskDueDateComparator(sortDirection))));
                break;
            case R.id.menu_tasks_sort_create_date:
                sortDirection = getAndRevertSortDirection(R.id.menu_tasks_sort_create_date);
                executor.submit(() -> this.runOnUiThread(
                        () -> applySortChanges(new TaskCreateDateComparator(sortDirection))));
                break;
            case R.id.menu_list_delete:
                ActivityUtils.showConfirmationDialog(this, R.string.list_delete_confirmation, (dialogInterface, i) -> {
                    listService.delete(listId);
                    drawer.openDrawer(Gravity.START);
                    navListAdapter.refresh();
                    openView(findViewById(R.id.nav_menu_today), TasksViewEnum.TODAY, R.string.navbar_today);
                });
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        taskAdapter.updatesAdapterItems();
    }

    private boolean getAndRevertSortDirection(int key) {
        boolean sortDirection = sortMap.get(key);
        sortMap.put(key, !sortDirection);
        return sortDirection;
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

    private void applySortChanges(Comparator<Task> comparator) {
        List<Task> newTasks = taskAdapter.getItems();
        List<Task> oldTasks = new ArrayList<>(newTasks);
        Collections.sort(newTasks, comparator);
        if (50 < CollectionUtils.size(newTasks)) {
            taskAdapter.refresh(newTasks);
        } else {
            Parcelable recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
            DiffUtil.DiffResult diffResult =
                    DiffUtil.calculateDiff(new DiffUtilCallback<>(oldTasks, newTasks), true);
            diffResult.dispatchUpdatesTo(taskAdapter);
            recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
        }
        if (null == viewEnum) {
            taskService.updatePositions(newTasks);
        }
        ActivityUtils.showSnackBar(coordinator, this.getString(R.string.tasks_sorted));
    }

    private void initNavBar() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle
                = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        View newTaskButton = findViewById(R.id.nav_add_new_list);
        newTaskButton.setOnClickListener(view -> {
            ListModal dialog = new ListModal();
            dialog.setListener((dialogFragment) -> {
                EditText name = dialogFragment.getDialog().findViewById(R.id.list_name);
                DoneList list = new DoneList();
                String listName = name.getText().toString();
                list.setName(listName);
                list.setCreatedDateTime(new Date().getTime());
                listId = (int) listService.insert(list);
                navListAdapter.setSelectedItem(null);
                navListAdapter.setCurrentListId(listId);
                navListAdapter.refresh();
                selectList(listId, listName);
            });
            dialog.show(getSupportFragmentManager(), "tag");
        });

        View navMenuToday = findViewById(R.id.nav_menu_today);
        View navMenuWeek = findViewById(R.id.nav_menu_week);
        View navMenuOverdue = findViewById(R.id.nav_menu_overdue);

        if (null != viewEnum) {
            if (TasksViewEnum.TODAY == viewEnum) {
                navMenuToday.setBackgroundColor(COLOR_LIGHT_GRAY);
            } else if (TasksViewEnum.WEEK == viewEnum) {
                navMenuWeek.setBackgroundColor(COLOR_LIGHT_GRAY);
            } else if (TasksViewEnum.OVERDUE == viewEnum) {
                navMenuOverdue.setBackgroundColor(COLOR_LIGHT_GRAY);
            }
        }

        RecyclerView recycler = findViewById(R.id.nav_lists);
        navListAdapter = new NavListAdapter(this, getSupportFragmentManager(), (listItem) -> {
            navMenuToday.setBackgroundColor(COLOR_BACKGROUND);
            navMenuWeek.setBackgroundColor(COLOR_BACKGROUND);
            navMenuOverdue.setBackgroundColor(COLOR_BACKGROUND);
            selectList(listItem.getId(), listItem.getName());
        }, listId);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(navListAdapter);

        navMenuToday.setOnClickListener(view -> {
            navMenuWeek.setBackgroundColor(COLOR_BACKGROUND);
            navMenuOverdue.setBackgroundColor(COLOR_BACKGROUND);
            openView(view, TasksViewEnum.TODAY, R.string.navbar_today);
            new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START, true), 50);
        });
        navMenuWeek.setOnClickListener(view -> {
            navMenuToday.setBackgroundColor(COLOR_BACKGROUND);
            navMenuOverdue.setBackgroundColor(COLOR_BACKGROUND);
            openView(view, TasksViewEnum.WEEK, R.string.navbar_week);
            new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START, true), 50);
        });
        navMenuOverdue.setOnClickListener(view -> {
            navMenuToday.setBackgroundColor(COLOR_BACKGROUND);
            navMenuWeek.setBackgroundColor(COLOR_BACKGROUND);
            openView(view, TasksViewEnum.OVERDUE, R.string.navbar_overdue);
            new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START, true), 50);
        });
    }

    private void selectList(Integer id, String listName) {
        listId = id;
        viewEnum = null;
        setTitle(listName);
        toolbar.setSubtitle(null);
        taskAdapter.refresh(listId);
        new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START, true), 25);
        PreferencesUtil.removePreference(this, getString(R.string.preference_lastOpenedView));
        PreferencesUtil.removePreference(this, getString(R.string.preference_lastOpenedViewName));
        PreferencesUtil.addIntPreference(this, getString(R.string.preference_lastOpenedListId), listId);
        PreferencesUtil.addPreference(this, getString(R.string.preference_lastOpenedListName), listName);
        toolbar.getMenu().getItem(1).setVisible(true);
    }

    private void configureApplication() {
        String viewName = PreferencesUtil.getPreference(this, getString(R.string.preference_lastOpenedView));
        if (StringUtils.isNoneBlank(viewName)) {
            viewEnum = TasksViewEnum.valueOf(viewName);
            setTitle(getString(R.string.view));
            toolbar.setSubtitle(PreferencesUtil.getPreference(this, getString(R.string.preference_lastOpenedViewName)));
        } else {
            this.listId = PreferencesUtil.getIntPreference(this, getString(R.string.preference_lastOpenedListId));
            setTitle(PreferencesUtil.getPreference(this, getString(R.string.preference_lastOpenedListName)));
        }
        String isFirstRunConfiguredPreferenceName = getString(R.string.preference_isFirstRunConfigured);
        if (!PreferencesUtil.getBooleanPreference(this, isFirstRunConfiguredPreferenceName)) {
            AlarmService.setOverdueTasksReminder(this);
            PreferencesUtil.addPreference(this, isFirstRunConfiguredPreferenceName, true);
        }
    }

    private void openView(View view, TasksViewEnum viewEnum, int subtitle) {
        this.viewEnum = viewEnum;
        view.setBackgroundColor(COLOR_LIGHT_GRAY);
        View selectedItem = navListAdapter.getSelectedItem();
        if (null != selectedItem) {
            selectedItem.setBackgroundColor(COLOR_BACKGROUND);
            //navListAdapter.setSelectedItem(null);
            // navListAdapter.setCurrentListId(null);
        }
        setTitle(getString(R.string.view));
        String subtitleString = getString(subtitle);
        toolbar.setSubtitle(subtitleString);
        taskAdapter.refresh(viewEnum);
        PreferencesUtil.addPreference(this, getString(R.string.preference_lastOpenedView), viewEnum.name());
        PreferencesUtil.addPreference(this, getString(R.string.preference_lastOpenedViewName), subtitleString);
        PreferencesUtil.removePreference(this, getString(R.string.preference_lastOpenedListId));
        PreferencesUtil.removePreference(this, getString(R.string.preference_lastOpenedListName));
        new Handler().postDelayed(() -> drawer.closeDrawer(GravityCompat.START, true), 50);
    }

    private void initStaticResources() {
        if (0 == COLOR_LIGHT_GRAY) {
            COLOR_LIGHT_GRAY = ContextCompat.getColor(this, R.color.lightestGray);
        }
        if (0 == COLOR_BACKGROUND) {
            COLOR_BACKGROUND = ContextCompat.getColor(this, R.color.background);
        }
    }
}
