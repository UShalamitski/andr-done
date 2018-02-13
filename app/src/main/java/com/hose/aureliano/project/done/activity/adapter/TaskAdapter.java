package com.hose.aureliano.project.done.activity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
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

    public TaskAdapter(Context context, FragmentManager fragmentManager) {
        taskDao = DatabaseCreator.getDatabase(context).getTaskDao();
        taskList = taskDao.read();
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    public void refresh() {
        this.taskList = taskDao.read();
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
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.done_list_item_layout, parent, false);
        }

        TextView name = convertView.findViewById(R.id.task_name);
        TextView id = convertView.findViewById(R.id.task_summary);
        ImageView itemMenu = convertView.findViewById(R.id.task_more);

        name.setText(getItem(position).getName());
        id.setText(getItem(position).getId());
        itemMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.menu_list_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        taskDao.delete(id.getText().toString());
                        refresh();
                        break;
                    case R.id.menu_edit:
/*                        Bundle bundle = new Bundle();
                        bundle.putString("name", name.getText().toString());
                        bundle.putString("id", id.getText().toString());
                        DialogFragment dialog = new ListModal();
                        dialog.setArguments(bundle);
                        dialog.show(fragmentManager, "list_edit");*/
                        break;
                }
                return true;
            });
            popupMenu.show();
        });

        return convertView;
    }
}
