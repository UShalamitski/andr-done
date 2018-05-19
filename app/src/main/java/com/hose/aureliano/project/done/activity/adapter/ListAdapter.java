package com.hose.aureliano.project.done.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.TasksActivity;
import com.hose.aureliano.project.done.activity.adapter.api.Adapter;
import com.hose.aureliano.project.done.activity.dialog.ListModal;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Adapter for ListView.
 * <p>
 * Date: 05.02.2018.
 *
 * @author evere
 */
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> implements Adapter<DoneList> {

    private static int COLOR_WHITE;
    private static int COLOR_GRAY_LIGHT;

    private FragmentManager fragmentManager;
    private DoneListDao doneListDao;
    private TaskService taskService;
    private List<DoneList> doneLists;
    private Set<Integer> selectedItemsSet;
    private Context context;
    private ActionMode actionMode;

    /**
     * Controller.
     *
     * @param context         application context
     * @param fragmentManager fragment manager
     */
    public ListAdapter(Context context, FragmentManager fragmentManager) {
        doneListDao = DatabaseCreator.getDatabase(context).getDoneListDao();
        taskService = new TaskService(context);
        doneLists = doneListDao.read();
        this.fragmentManager = fragmentManager;
        this.context = context;
        selectedItemsSet = new HashSet<>();
        initStaticResources();
    }

    /**
     * Sets action mode.
     *
     * @param actionMode action mode
     */
    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }

    /**
     * Refreshes data on UI.
     */
    public void refresh() {
        this.doneLists = doneListDao.read();
        notifyDataSetChanged();
    }

    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.menu.setOnClickListener(itemView -> {
            DoneList doneList = getItem(itemView);
            PopupMenu popupMenu = new PopupMenu(context, itemView);
            popupMenu.inflate(R.menu.menu_list_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        ActivityUtils.showConfirmationDialog(context, R.string.list_delete_confirmation,
                                (dialog, which) -> {
                                    DatabaseCreator.getDatabase(context).runInTransaction(() -> {
                                        taskService.deleteByListId(doneList.getId());
                                        doneListDao.delete(doneList);
                                    });
                                    int position = getPosition(itemView);

                                });
                        break;
                    case R.id.menu_edit:
                        Bundle bundle = new Bundle();
                        bundle.putString("name", doneList.getName());
                        bundle.putString("id", doneList.getId());
                        bundle.putLong("createdDateTime", doneList.getCreatedDateTime());
                        DialogFragment dialog = new ListModal();
                        dialog.setArguments(bundle);
                        dialog.show(fragmentManager, "list_edit");
                        break;
                }
                return true;
            });
            popupMenu.show();
        });
        view.setOnClickListener(itemView -> {
            if (null != actionMode) {
                toggleSelection(viewHolder, actionMode);
                ActivityUtils.vibrate(context);
            } else {
                Intent intent = new Intent(context, TasksActivity.class);
                intent.putExtra("listId", viewHolder.id);
                intent.putExtra("name", viewHolder.name.getText().toString());
                context.startActivity(intent);
            }
        });
        view.setOnLongClickListener(longClickView -> {
            if (null != actionMode) {
                if (1 == CollectionUtils.size(selectedItemsSet)
                        && selectedItemsSet.contains(viewHolder.getAdapterPosition())) {
                    return false;
                } else {
                    toggleSelection(viewHolder, actionMode);
                }
            }
            return true;
        });
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        DoneList doneList = getItem(position);
        holder.id = doneList.getId();
        holder.taskCounts.setText(String.format("%s/%s", doneList.getDoneTasksCount(), doneList.getTasksCount()));
        holder.name.setText(doneList.getName());
        holder.menu.setTag(doneList.getId());
        holder.progressBar.setMax(doneList.getTasksCount());
        holder.progressBar.setProgress(doneList.getDoneTasksCount());
        holder.view.setBackgroundColor(
                selectedItemsSet.contains(holder.getAdapterPosition()) ? COLOR_GRAY_LIGHT : COLOR_WHITE);
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(doneLists);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Sets position for tasks as their index.
     */
    public void updatePositions() {
        int position = 0;
        for (DoneList list : getItems()) {
            list.setPosition(position++);
        }
    }

    @Override
    public List<DoneList> getItems() {
        return doneLists;
    }

    @Override
    public void removeItems(List<DoneList> lists) {
        for (DoneList list : lists) {
            doneLists.remove(list);
        }
        notifyDataSetChanged();
    }

    public void toggleSelection(ListAdapter.ViewHolder viewHolder, ActionMode actionMode) {
        if (selectedItemsSet.contains(viewHolder.getAdapterPosition())) {
            selectedItemsSet.remove(viewHolder.getAdapterPosition());
            viewHolder.getView().setBackgroundColor(COLOR_WHITE);
            if (CollectionUtils.isEmpty(selectedItemsSet)) {
                actionMode.finish();
            }
        } else {
            selectedItemsSet.add(viewHolder.getAdapterPosition());
            viewHolder.getView().setBackgroundColor(COLOR_GRAY_LIGHT);
        }
        actionMode.setTitle(context.getString(R.string.task_selected, CollectionUtils.size(selectedItemsSet)));
    }

    /**
     * Clear set of selected items.
     */
    public void clearSelection() {
        setActionMode(null);
        selectedItemsSet.clear();
        notifyDataSetChanged();
    }

    /**
     * Selects all items.
     */
    public void selectAll() {
        for (int i = 0; i < CollectionUtils.size(doneLists); i++) {
            selectedItemsSet.add(i);
        }
        notifyDataSetChanged();
        if (null != actionMode) {
            actionMode.setTitle(context.getString(R.string.task_selected, CollectionUtils.size(selectedItemsSet)));
        }
    }

    /**
     * @return set of selected {@link Task}s.
     */
    public List<DoneList> getSelectedItems() {
        List<DoneList> selectedTasks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(selectedItemsSet)) {
            for (Integer position : selectedItemsSet) {
                selectedTasks.add(doneLists.get(position));
            }
        }
        return selectedTasks;
    }

    private DoneList getItem(int position) {
        return doneLists.get(position);
    }

    private DoneList getItem(View view) {
        return getItem((String) view.getTag());
    }

    private DoneList getItem(String listId) {
        for (DoneList list : doneLists) {
            if (list.getId().equals(listId)) {
                return list;
            }
        }
        throw new NoSuchElementException();
    }

    private int getPosition(View view) {
        int pos = 0;
        for (DoneList list : doneLists) {
            if (list.getId().equals(view.getTag())) {
                return pos;
            }
            pos++;
        }
        throw new NoSuchElementException();
    }

    private void initStaticResources() {
        if (0 == COLOR_WHITE) {
            COLOR_WHITE = ContextCompat.getColor(context, R.color.white);
        }
        if (0 == COLOR_GRAY_LIGHT) {
            COLOR_GRAY_LIGHT = ContextCompat.getColor(context, R.color.lightGray);
        }
    }

    /**
     * Provides a reference to the views for each data item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private String id;
        private TextView taskCounts;
        private TextView name;
        private View menu;
        private ProgressBar progressBar;
        private View view;

        ViewHolder(View view) {
            super(view);
            this.name = view.findViewById(R.id.name);
            this.menu = view.findViewById(R.id.list_menu);
            this.taskCounts = view.findViewById(R.id.list_item_count);
            this.progressBar = view.findViewById(R.id.list_progress_bar);
            this.view = view;
        }

        public View getView() {
            return view;
        }
    }
}
