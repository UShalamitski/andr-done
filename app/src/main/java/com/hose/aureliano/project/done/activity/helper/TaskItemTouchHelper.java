package com.hose.aureliano.project.done.activity.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.TaskAdapter;
import com.hose.aureliano.project.done.activity.dialog.SelectListModal;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.utils.ActivityUtils;
import com.hose.aureliano.project.done.utils.CalendarUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Touch helper for tasks.
 * <p>
 * Date: 12.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class TaskItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private static ColorDrawable DRAWABLE_COLOR_PRIMARY;
    private static ColorDrawable DRAWABLE_COLOR_RED;
    private static ColorDrawable DRAWABLE_COLOR_GREEN;
    private static ColorDrawable DRAWABLE_COLOR_GRAY;
    private static Drawable DRAWABLE_DRAWABLE_CANCEL;
    private static Drawable DRAWABLE_DRAWABLE_DONE;

    private TaskService taskService;
    private TaskAdapter adapter;
    private Context context;
    private View coordinator;

    private Integer fromPosition = null;
    private Integer toPosition = null;
    private boolean isDragged;
    private boolean isMoved;
    private ActionMode actionMode;
    private TaskAdapter.ViewHolder holder;

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(10);
    private Map<Task, ScheduledFuture> itemsToRemoveMap = new HashMap<>();
    private Map<Integer, Float> previousDxMap = new HashMap<>();

    public TaskItemTouchHelper(Context context, TaskAdapter adapter, View coordinator) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.context = context;
        this.adapter = adapter;
        this.coordinator = coordinator;
        taskService = new TaskService(context);
        initStaticVariables();
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return null == actionMode;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return null == actionMode;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        if (null == actionMode) {
            isMoved = true;
            int oldPosition = viewHolder.getAdapterPosition();
            int newPosition = target.getAdapterPosition();
            List<Task> tasks = adapter.getItems();
            tasks.add(newPosition, tasks.remove(oldPosition));
            adapter.notifyItemMoved(oldPosition, newPosition);
        }
        return null == actionMode;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (fromPosition != null && toPosition != null) {
            int i = 0;
            for (Task task : adapter.getItems()) {
                task.setPosition(i++);
            }
            executorService.submit(() -> taskService.update(adapter.getItems()));
        }
        fromPosition = null;
        toPosition = null;
        final View foregroundView = ((TaskAdapter.ViewHolder) viewHolder).getViewForeground();
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        TaskAdapter.ViewHolder taskViewHolder = (TaskAdapter.ViewHolder) viewHolder;
        View view = null;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            view = ((TaskAdapter.ViewHolder) viewHolder).getViewForeground();
            Float previousDx = previousDxMap.get(viewHolder.getAdapterPosition());
            if (dX > 0 && (null == previousDx || previousDx < dX)) {
                if (taskViewHolder.getCheckBox().isChecked()) {
                    if (!taskViewHolder.getViewBackground().getBackground().equals(DRAWABLE_COLOR_GRAY)) {
                        taskViewHolder.getViewBackground().setBackground(DRAWABLE_COLOR_GRAY);
                        taskViewHolder.getBackgroundLeftIcon().setImageDrawable(DRAWABLE_DRAWABLE_CANCEL);
                        taskViewHolder.getBackgroundLeftText().setText(R.string.cancel);
                    }
                } else if (!taskViewHolder.getViewBackground().getBackground().equals(DRAWABLE_COLOR_GREEN)) {
                    taskViewHolder.getViewBackground().setBackground(DRAWABLE_COLOR_GREEN);
                    taskViewHolder.getBackgroundLeftIcon().setImageDrawable(DRAWABLE_DRAWABLE_DONE);
                    taskViewHolder.getBackgroundLeftText().setText(R.string.done);
                }
                taskViewHolder.getBackgroundLeftIcon().setVisibility(View.VISIBLE);
                taskViewHolder.getBackgroundLeftText().setVisibility(View.VISIBLE);
                taskViewHolder.getBackgroundRightIcon().setVisibility(View.GONE);
                taskViewHolder.getBackgroundRightText().setVisibility(View.GONE);
            } else if (dX < 0 && !taskViewHolder.getViewBackground().getBackground().equals(DRAWABLE_COLOR_RED)) {
                taskViewHolder.getViewBackground().setBackground(DRAWABLE_COLOR_RED);
                taskViewHolder.getBackgroundLeftIcon().setVisibility(View.GONE);
                taskViewHolder.getBackgroundLeftText().setVisibility(View.GONE);
                taskViewHolder.getBackgroundRightIcon().setVisibility(View.VISIBLE);
                taskViewHolder.getBackgroundRightText().setVisibility(View.VISIBLE);
            }
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            view = ((TaskAdapter.ViewHolder) viewHolder).getView();
        }
        previousDxMap.put(viewHolder.getAdapterPosition(), dX);
        getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            isDragged = true;
            holder = (TaskAdapter.ViewHolder) viewHolder;
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (isDragged && !isMoved) {
                actionMode = ((Activity) context).startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        if (0 <= holder.getAdapterPosition()) {
                            actionMode = mode;
                            mode.getMenuInflater().inflate(R.menu.menu_tasks_selected, menu);
                            adapter.toggleSelection(holder, mode);
                            ((Activity) context)
                                    .getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.gray));
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        adapter.setActionMode(actionMode);
                        if (null != adapter.getTasksView()) {
                            menu.findItem(R.id.menu_tasks_selected_duplicate).setVisible(false);
                            menu.findItem(R.id.menu_tasks_selected_move).setVisible(false);
                        }
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_tasks_selected_select:
                                adapter.selectAll();
                                break;
                            case R.id.menu_tasks_selected_duplicate:
                                taskService.duplicate(adapter.getSelectedTasks(), adapter.getAvailablePosition());
                                refreshDataAndFinishActionMode();
                                ActivityUtils.showSnackBar(coordinator, context.getString(R.string.task_duplicate));
                                break;
                            case R.id.menu_tasks_selected_move:
                                new SelectListModal(context, (listId, listName) -> {
                                    int position = taskService.getAvailablePosition(listId);
                                    for (Task task : adapter.getSelectedTasks()) {
                                        task.setListId(listId);
                                        task.setPosition(position++);
                                        taskService.update(task);
                                    }
                                    adapter.refresh();
                                    actionMode.finish();
                                    ActivityUtils.showSnackBar(coordinator, context.getString(R.string.task_move));
                                });
                                break;
                            case R.id.menu_tasks_selected_delete:
                                ActivityUtils.showConfirmationDialog(context, R.string.task_delete_selected_confirmation,
                                        (dialog, which) -> {
                                            List<Task> selectedTasks = adapter.getSelectedTasks();
                                            executorService.submit(() -> taskService.delete(selectedTasks));
                                            adapter.removeItems(selectedTasks);
                                            actionMode.finish();
                                        });
                                break;
                            case R.id.menu_tasks_due_date_today:
                                updateDueDate(CalendarUtils.getTodayDateTimeInMillis());
                                break;
                            case R.id.menu_tasks_due_date_tomorrow:
                                updateDueDate(CalendarUtils.getTomorrowDateTimeInMillis());
                                break;
                            case R.id.menu_tasks_due_date_next_week:
                                updateDueDate(CalendarUtils.getNextMondayDateTimeInMillis());
                                break;
                            case R.id.menu_tasks_without_due_date:
                                updateDueDate(null);
                                break;
                            case R.id.menu_tasks_select_due_date:
                                ActivityUtils.showDatePickerDialog(context, (view, year, month, dayOfMonth) -> {
                                    updateDueDate(CalendarUtils.getTimeInMillis(year, month, dayOfMonth));
                                }, System.currentTimeMillis());
                                break;
                        }
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        actionMode = null;
                        adapter.clearSelection();
                        ((Activity) context)
                                .getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    }
                });
            }
            isDragged = false;
            isMoved = false;
            holder = null;
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void onMoved(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int fromPos,
                        RecyclerView.ViewHolder target, int toPos, int x, int y) {
        if (fromPosition == null) {
            fromPosition = fromPos;
        }
        toPosition = toPos;
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            int position = viewHolder.getAdapterPosition();
            Task task = adapter.getItem(position);
            adapter.removeItem(position);

            itemsToRemoveMap.put(task, executorService.schedule(() -> {
                taskService.delete(task);
                itemsToRemoveMap.remove(task);
            }, 3, TimeUnit.SECONDS));

            ActivityUtils.showSnackBar(coordinator, context.getString(R.string.task_removed), R.string.undo,
                    snackBarView -> {
                        adapter.restoreItem(position, task);
                        itemsToRemoveMap.remove(task).cancel(false);
                    });
        } else if (direction == ItemTouchHelper.RIGHT) {
            if (null != adapter.getTasksView()) {
                int position = viewHolder.getAdapterPosition();
                Task task = adapter.getItem(position);
                task.setDone(!task.getDone());
                taskService.update(task);
                adapter.removeItem(position);
            } else {
                TaskAdapter.ViewHolder holder = ((TaskAdapter.ViewHolder) viewHolder);
                holder.getCheckBox().setChecked(!holder.getCheckBox().isChecked());
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        }
        ActivityUtils.vibrate(context);
    }

    private void updateDueDate(Long timeInMillis) {
        for (Task task : adapter.getSelectedTasks()) {
            task.setDueDateTime(timeInMillis);
            taskService.update(task);
        }
        refreshDataAndFinishActionMode();
        ActivityUtils.showSnackBar(coordinator, context.getString(R.string.task_due_date_changed));
    }

    private void refreshDataAndFinishActionMode() {
        adapter.refresh();
        actionMode.finish();
    }

    private void initStaticVariables() {
        if (null == DRAWABLE_COLOR_PRIMARY) {
            DRAWABLE_COLOR_PRIMARY = new ColorDrawable(ContextCompat.getColor(context, R.color.colorPrimary));
        }
        if (null == DRAWABLE_COLOR_RED) {
            DRAWABLE_COLOR_RED = new ColorDrawable(ContextCompat.getColor(context, R.color.red));
        }
        if (null == DRAWABLE_COLOR_GREEN) {
            DRAWABLE_COLOR_GREEN = new ColorDrawable(ContextCompat.getColor(context, R.color.green));
        }
        if (null == DRAWABLE_COLOR_GRAY) {
            DRAWABLE_COLOR_GRAY = new ColorDrawable(ContextCompat.getColor(context, R.color.gray));
        }
        if (null == DRAWABLE_DRAWABLE_CANCEL) {
            DRAWABLE_DRAWABLE_CANCEL = ContextCompat.getDrawable(context, R.drawable.icon_cancel_white_24dp);
        }
        if (null == DRAWABLE_DRAWABLE_DONE) {
            DRAWABLE_DRAWABLE_DONE = ContextCompat.getDrawable(context, R.drawable.icon_done_white_24dp);
        }
    }
}
