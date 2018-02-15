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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
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
public class ListAdapter extends BaseAdapter {

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

    public void refresh() {
        this.doneLists = doneListDao.read();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return CollectionUtils.size(doneLists);
    }

    @Override
    public DoneList getItem(int position) {
        return doneLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_layout, parent, false);
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView id = convertView.findViewById(R.id.summary);
        ImageView itemMenu = convertView.findViewById(R.id.more);

        name.setText(getItem(position).getName());
        id.setText(getItem(position).getId());
        itemMenu.setOnClickListener(v -> {
            String idString = id.getText().toString();
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.menu_list_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        DatabaseCreator.getDatabase(context).runInTransaction(() -> {
                            taskDao.deleteByListId(idString);
                            doneListDao.delete(idString);
                        });
                        refresh();
                        break;
                    case R.id.menu_edit:
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name.getText().toString());
                        bundle.putString("id", idString);
                        DialogFragment dialog = new ListModal();
                        dialog.setArguments(bundle);
                        dialog.show(fragmentManager, "list_edit");
                        break;
                }
                return true;
            });
            popupMenu.show();
        });

        return convertView;
    }
}
