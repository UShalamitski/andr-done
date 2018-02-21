package com.hose.aureliano.project.done.activity.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.dialog.TaskModal;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.TaskDao;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Adapter for ListView.
 * <p>
 * Date: 05.02.2018.
 *
 * @author evere
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private FragmentManager fragmentManager;
    private TaskDao taskDao;
    private List<Task> taskList;
    private Context context;
    private String listId;

    public TaskAdapter(Context context, FragmentManager fragmentManager, String listId) {
        taskDao = DatabaseCreator.getDatabase(context).getTaskDao();
        taskList = taskDao.read(listId);
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.listId = listId;
    }

    /**
     * Refreshes data on UI.
     */
    public void refresh() {
        this.taskList = taskDao.read(listId);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.menu.setOnClickListener(v -> {
            Task currentTask = getItem(v);
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.menu_list_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        taskDao.delete(currentTask.getId());
                        refresh();
                        break;
                    case R.id.menu_edit:
                        DialogFragment dialog = new TaskModal();
                        dialog.setArguments(buildBundle(currentTask));
                        dialog.show(fragmentManager, "task_modal");
                        break;
                }
                return true;
            });
            popupMenu.show();
        });
        view.setOnClickListener(itemView -> {
            viewHolder.done.setChecked(!viewHolder.done.isChecked());
        });
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = getItem(position);
        holder.done.setTag(position);
        holder.menu.setTag(position);
        holder.id.setText(task.getId());
        holder.name.setText(task.getName());
        holder.done.setChecked(task.getDone());

        holder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Task currentTask = getItem(buttonView);
            if (currentTask.getDone() != buttonView.isChecked()) {
                currentTask.setDone(buttonView.isChecked());
                taskDao.update(currentTask);
                Toast.makeText(context, holder.name.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(taskList);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Bundle buildBundle(Task task) {
        Bundle bundle = new Bundle();
        bundle.putString("name", task.getName());
        bundle.putString("id", task.getId());
        bundle.putString("done", String.valueOf(task.getDone()));
        return bundle;
    }

    private Task getItem(int position) {
        return taskList.get(position);
    }

    private Task getItem(View view) {
        return getItem((int) view.getTag());
    }

    /**
     * Provides a reference to the views for each data item.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView id;
        private TextView name;
        private ImageView menu;
        private CheckBox done;

        ViewHolder(View view) {
            super(view);
            this.id = view.findViewById(R.id.task_id);
            this.name = view.findViewById(R.id.task_name);
            this.menu = view.findViewById(R.id.task_menu);
            this.done = view.findViewById(R.id.task_checkbox);
        }
    }
}
