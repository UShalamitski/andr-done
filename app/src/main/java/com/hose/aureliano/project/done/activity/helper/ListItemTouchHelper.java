package com.hose.aureliano.project.done.activity.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.hose.aureliano.project.done.activity.adapter.ListAdapter;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.service.ListService;

import java.util.List;

/**
 * Touch helper for tasks.
 * <p>
 * Date: 12.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class ListItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ListAdapter adapter;
    private ListService taskService;

    private Integer fromPosition = null;
    private Integer toPosition = null;

    public ListItemTouchHelper(Context context, ListAdapter adapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
        this.adapter = adapter;
        taskService = new ListService(context);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        int oldPosition = viewHolder.getAdapterPosition();
        int newPosition = target.getAdapterPosition();
        List<DoneList> lists = adapter.getLists();
        lists.add(newPosition, lists.remove(oldPosition));
        adapter.notifyItemMoved(oldPosition, newPosition);
        return true;
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (fromPosition != null && toPosition != null) {
            int i = 0;
            for (DoneList list : adapter.getLists()) {
                list.setPosition(i++);
                taskService.update(list);
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
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
