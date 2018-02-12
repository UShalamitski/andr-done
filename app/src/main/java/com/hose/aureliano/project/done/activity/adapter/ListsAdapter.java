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

import com.hose.aureliano.project.done.activity.dialog.AddListModal;
import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Created by evere on 05.02.2018.
 */

public class ListsAdapter extends BaseAdapter {

    private FragmentManager fragmentManager;
    private DoneListDao doneListDao;
    private List<DoneList> doneLists;
    private Context context;

    public ListsAdapter(Context context, FragmentManager fragmentManager) {
        doneListDao = DatabaseCreator.getDatabase(context).getDoneListDao();
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
        View view = LayoutInflater.from(context).inflate(R.layout.lists_item, parent, false);

        TextView name = view.findViewById(R.id.name);
        TextView id = view.findViewById(R.id.summary);
        ImageView more = view.findViewById(R.id.more);

        name.setText(getItem(position).getName());
        id.setText(getItem(position).getId());
        more.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.menu_list_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        doneListDao.delete(id.getText().toString());
                        refresh();
                        break;
                    case R.id.menu_edit:
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name.getText().toString());
                        bundle.putString("id", id.getText().toString());
                        DialogFragment dialog = new AddListModal();
                        dialog.setArguments(bundle);
                        dialog.show(fragmentManager, "list_edit");
                        break;
                }
                return true;
            });
            popupMenu.show();
        });

        return view;
    }
}
