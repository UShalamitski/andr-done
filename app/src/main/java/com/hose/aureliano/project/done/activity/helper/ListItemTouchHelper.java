package com.hose.aureliano.project.done.activity.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.ListAdapter;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.service.ListService;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import java.util.List;

/**
 * Touch helper for tasks.
 * <p>
 * Date: 12.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class ListItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private static int COLOR_PRIMARY;
    private static int COLOR_GRAY;

    private ListAdapter adapter;
    private ListService listService;
    private TaskService taskService;
    private ActionMode actionMode;
    private ListAdapter.ViewHolder holder;
    private Context context;

    private Integer fromPosition = null;
    private Integer toPosition = null;
    private boolean isDragged;
    private boolean isMoved;

    public ListItemTouchHelper(Context context, ListAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        this.adapter = adapter;
        this.context = context;
        listService = new ListService(context);
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
            List<DoneList> lists = adapter.getItems();
            lists.add(newPosition, lists.remove(oldPosition));
            adapter.notifyItemMoved(oldPosition, newPosition);
        }
        return null == actionMode;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (fromPosition != null && toPosition != null) {
            int i = 0;
            for (DoneList list : adapter.getItems()) {
                list.setPosition(i++);
                listService.update(list);
            }
        }
        fromPosition = null;
        toPosition = null;
        getDefaultUIUtil().clearView(((ListAdapter.ViewHolder) viewHolder).getView());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View view = null;
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            view = ((ListAdapter.ViewHolder) viewHolder).getView();
        }
        getDefaultUIUtil().onDraw(c, recyclerView, view, dX, dY, actionState, isCurrentlyActive);
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
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            holder = (ListAdapter.ViewHolder) viewHolder;
            isDragged = true;
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (isDragged && !isMoved) {
                actionMode = ((Activity) context).startActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        if (0 <= holder.getAdapterPosition()) {
                            actionMode = mode;
                            mode.getMenuInflater().inflate(R.menu.menu_lists_selected, menu);
                            adapter.toggleSelection(holder, mode);
                            ((Activity) context).getWindow().setStatusBarColor(COLOR_GRAY);
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        adapter.setActionMode(actionMode);
                        return true;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_lists_selected_select:
                                adapter.selectAll();
                                break;
                            case R.id.menu_lists_selected_delete:
                                ActivityUtils.showConfirmationDialog(context, R.string.lists_delete_confirmation,
                                        (dialog, which) -> {
                                            List<DoneList> selectedLists = adapter.getSelectedItems();
                                            DatabaseCreator.getDatabase(context).runInTransaction(() -> {
                                                for (DoneList doneList : selectedLists) {
                                                    taskService.deleteByListId(doneList.getId());
                                                    listService.delete(doneList);
                                                }
                                            });
                                            adapter.removeItems(selectedLists);
                                            actionMode.finish();
                                        });
                                break;
                        }
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        actionMode = null;
                        adapter.clearSelection();
                        ((Activity) context).getWindow().setStatusBarColor(COLOR_PRIMARY);
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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }

    private void initStaticVariables() {
        if (0 == COLOR_PRIMARY) {
            COLOR_PRIMARY = ContextCompat.getColor(context, R.color.colorPrimary);
        }
        if (0 == COLOR_GRAY) {
            COLOR_GRAY = ContextCompat.getColor(context, R.color.gray);
        }
    }
}
