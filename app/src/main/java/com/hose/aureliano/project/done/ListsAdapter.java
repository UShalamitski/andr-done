package com.hose.aureliano.project.done;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.hose.aureliano.project.done.repository.impl.ListsRepository;

/**
 * Created by evere on 05.02.2018.
 */

public class ListsAdapter extends CursorAdapter {

    private ListsRepository listsRepository;
    private FragmentManager fragmentManager;

    public ListsAdapter(Context context, ListsRepository listsRepository, FragmentManager manager) {
        super(context, listsRepository.read(), 0);
        this.listsRepository = listsRepository;
        this.fragmentManager = manager;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.lists_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = view.findViewById(R.id.name);
        TextView id = view.findViewById(R.id.summary);
        ImageView more = view.findViewById(R.id.more);

        name.setText(cursor.getString(1));
        id.setText(cursor.getString(0));
        more.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.menu_list_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        listsRepository.delete(id.getText().toString());
                        swapCursor(listsRepository.read());
                        break;
                    case R.id.menu_edit:
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name.getText().toString());
                        bundle.putString("_id", id.getText().toString());
                        DialogFragment dialog = new AddListModal();
                        dialog.setArguments(bundle);
                        dialog.show(fragmentManager, "list_edit");
                        break;
                }
                return true;
            });
            popupMenu.show();
        });
    }
}
