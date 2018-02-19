package com.hose.aureliano.project.done.activity.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
public class TaskAdapter extends BaseAdapter {

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

    public void refresh() {
        this.taskList = taskDao.read(listId);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return CollectionUtils.size(taskList);
    }

    @Override
    public Task getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Task task = getItem(position);
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_task_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.id = convertView.findViewById(R.id.task_summary);
            viewHolder.name = convertView.findViewById(R.id.task_name);
            viewHolder.itemMenu = convertView.findViewById(R.id.task_more);
            viewHolder.done = convertView.findViewById(R.id.task_checkbox);
            viewHolder.done.setTag(position);

            viewHolder.itemMenu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.inflate(R.menu.menu_list_more);
                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.menu_delete:
                            taskDao.delete(task.getId());
                            refresh();
                            break;
                        case R.id.menu_edit:
                            Bundle bundle = new Bundle();
                            bundle.putString("name", task.getName());
                            bundle.putString("id", task.getId());
                            bundle.putString("done", String.valueOf(task.getDone()));
                            DialogFragment dialog = new TaskModal();
                            dialog.setArguments(bundle);
                            dialog.show(fragmentManager, "task_modal");
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.id.setText(task.getId());
        viewHolder.name.setText(task.getName());
        viewHolder.done.setChecked(task.getDone());
        viewHolder.done.setOnCheckedChangeListener((buttonView, isChecked) -> {
            taskList.get((int) buttonView.getTag()).setDone(isChecked);
            taskDao.update(task);
            Toast.makeText(context, viewHolder.name.getText(), Toast.LENGTH_SHORT).show();
        });
        viewHolder.done.setTag(position);
        return convertView;
    }

    private static class ViewHolder {
        private TextView id;
        private TextView name;
        private ImageView itemMenu;
        private CheckBox done;
    }
}
