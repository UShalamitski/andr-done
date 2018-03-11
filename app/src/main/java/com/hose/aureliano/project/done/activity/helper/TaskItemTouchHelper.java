package com.hose.aureliano.project.done.activity.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.TaskAdapter;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.TaskDao;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import java.util.HashMap;
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

    private static int COLOR_RED;
    private static int COLOR_GREEN;
    private static int COLOR_ORANGE;
    private static Drawable DRAWABLE_CANCEL;
    private static Drawable DRAWABLE_DONE;

    private TaskAdapter adapter;
    private TaskDao taskDao;
    private Context context;
    private View coordinator;

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(5);
    private Map<Task, ScheduledFuture> itemsToRemoveMap = new HashMap<>();

    public TaskItemTouchHelper(Context context, TaskAdapter adapter, View coordinator, int swipeDirs, int dragDirs) {
        super(dragDirs, swipeDirs);
        this.context = context;
        this.adapter = adapter;
        this.coordinator = coordinator;
        taskDao = DatabaseCreator.getDatabase(context).getTaskDao();
        COLOR_RED = ContextCompat.getColor(context, R.color.red);
        COLOR_GREEN = ContextCompat.getColor(context, R.color.green);
        COLOR_ORANGE = ContextCompat.getColor(context, R.color.orange);
        DRAWABLE_CANCEL = ContextCompat.getDrawable(context, R.drawable.icon_cancel_white);
        DRAWABLE_DONE = ContextCompat.getDrawable(context, R.drawable.icon_done_white);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null) {
            View foregroundView = ((TaskAdapter.ViewHolder) viewHolder).getViewForeground();
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((TaskAdapter.ViewHolder) viewHolder).getViewForeground();
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final View foregroundView = ((TaskAdapter.ViewHolder) viewHolder).getViewForeground();
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        TaskAdapter.ViewHolder taskViewHolder = (TaskAdapter.ViewHolder) viewHolder;
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX > 0) {
                if (taskViewHolder.getCheckBox().isChecked()) {
                    taskViewHolder.getViewBackground().setBackgroundColor(COLOR_ORANGE);
                    taskViewHolder.getBackgroundLeftIcon().setImageDrawable(DRAWABLE_CANCEL);
                    taskViewHolder.getBackgroundLeftText().setText(R.string.cancel);
                } else {
                    taskViewHolder.getViewBackground().setBackgroundColor(COLOR_GREEN);
                    taskViewHolder.getBackgroundLeftIcon().setImageDrawable(DRAWABLE_DONE);
                    taskViewHolder.getBackgroundLeftText().setText(R.string.done);
                }
                taskViewHolder.getBackgroundLeftIcon().setVisibility(View.VISIBLE);
                taskViewHolder.getBackgroundLeftText().setVisibility(View.VISIBLE);
                taskViewHolder.getBackgroundRightIcon().setVisibility(View.GONE);
                taskViewHolder.getBackgroundRightText().setVisibility(View.GONE);
            } else if (dX < 0) {
                taskViewHolder.getViewBackground().setBackgroundColor(COLOR_RED);
                taskViewHolder.getBackgroundLeftIcon().setVisibility(View.GONE);
                taskViewHolder.getBackgroundLeftText().setVisibility(View.GONE);
                taskViewHolder.getBackgroundRightIcon().setVisibility(View.VISIBLE);
                taskViewHolder.getBackgroundRightText().setVisibility(View.VISIBLE);
            }
        }
        getDefaultUIUtil().onDraw(c, recyclerView, taskViewHolder.getViewForeground(), dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.LEFT) {
            int position = viewHolder.getAdapterPosition();
            Task task = adapter.getItem(position);
            adapter.removeItem(position);

            itemsToRemoveMap.put(task, executorService.schedule(() -> {
                taskDao.delete(task.getId());
                itemsToRemoveMap.remove(task);
                AlarmService.cancelAlarm(context, task);
            }, 3, TimeUnit.SECONDS));

            ActivityUtils.showSnackBar(coordinator, "Item removed " + task.getName(), R.string.undo,
                    snackBarView -> {
                        adapter.restoreItem(position, task);
                        itemsToRemoveMap.remove(task).cancel(false);
                    });
            ActivityUtils.vibrate(context);
        } else if (direction == ItemTouchHelper.RIGHT) {
            TaskAdapter.ViewHolder holder = ((TaskAdapter.ViewHolder) viewHolder);
            holder.getCheckBox().setChecked(!holder.getCheckBox().isChecked());
            adapter.notifyDataSetChanged();
            ActivityUtils.vibrate(context);
        }
    }
}
