package com.hose.aureliano.project.done.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.TasksActivity;
import com.hose.aureliano.project.done.activity.dialog.ListModal;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;
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
public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private FragmentManager fragmentManager;
    private DoneListDao doneListDao;
    private TaskDao taskDao;
    private List<DoneList> doneLists;
    private Context context;

    public ListAdapter(Context context, FragmentManager fragmentManager) {
        doneListDao = DatabaseCreator.getDatabase(context).getDoneListDao();
        taskDao = DatabaseCreator.getDatabase(context).getTaskDao();
        doneLists = doneListDao.read();
        this.fragmentManager = fragmentManager;
        this.context = context;
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
                        DatabaseCreator.getDatabase(context).runInTransaction(() -> {
                            taskDao.deleteByListId(doneList.getId());
                            doneListDao.delete(doneList.getId());
                        });
                        refresh();
                        break;
                    case R.id.menu_edit:
                        Bundle bundle = new Bundle();
                        bundle.putString("name", doneList.getName());
                        bundle.putString("id", doneList.getId());
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
            Intent intent = new Intent(context, TasksActivity.class);
            intent.putExtra("listId", viewHolder.id);
            context.startActivity(intent);
        });
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ListAdapter.ViewHolder holder, int position) {
        DoneList doneList = getItem(position);
        holder.id = (doneList.getId());
        holder.name.setText(doneList.getName());
        holder.menu.setTag(position);
        holder.progressBar.setMax(doneList.getTasksCount());
        holder.progressBar.setProgress(doneList.getDoneTasksCount());
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(doneLists);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private DoneList getItem(int position) {
        return doneLists.get(position);
    }

    private DoneList getItem(View view) {
        return getItem((int) view.getTag());
    }

    /**
     * Provides a reference to the views for each data item.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private String id;
        private TextView name;
        private ImageView menu;
        private ProgressBar progressBar;

        ViewHolder(View view) {
            super(view);
            //this.id = view.findViewById(R.id.list_id);
            this.name = view.findViewById(R.id.name);
            this.menu = view.findViewById(R.id.list_menu);
            this.progressBar = view.findViewById(R.id.list_progress_bar);
        }
    }
}
