package com.hose.aureliano.project.done.activity.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.adapter.api.Adapter;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Adapter for ListView.
 * <p>
 * Date: 05.02.2018
 *
 * @author Uladzislau Shalamitski
 */
public class NavListAdapter extends RecyclerView.Adapter<NavListAdapter.ViewHolder> implements Adapter<DoneList> {

    private FragmentManager fragmentManager;
    private DoneListDao doneListDao;
    private List<DoneList> doneLists;
    private Context context;
    private Integer currentListId;
    private View selectedItem;
    private ListItemClickListener itemClickListener;

    /**
     * Controller.
     *
     * @param context         application context
     * @param fragmentManager fragment manager
     */
    public NavListAdapter(Context context, FragmentManager fragmentManager, ListItemClickListener itemClickListener, Integer currentListId) {
        doneListDao = DatabaseCreator.getDatabase(context).getDoneListDao();
        doneLists = doneListDao.read();
        this.currentListId = currentListId;
        this.itemClickListener = itemClickListener;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    @Override
    public NavListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nav_list_item_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(itemView -> {
            itemClickListener.click(getItems().get(viewHolder.getAdapterPosition()));
            if (null != selectedItem) {
                selectedItem.setBackgroundColor(context.getResources().getColor(R.color.background));
            }
            itemView.setBackgroundColor(context.getResources().getColor(R.color.lightestGray));
            selectedItem = itemView;
        });
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NavListAdapter.ViewHolder holder, int position) {
        DoneList doneList = getItem(position);
        holder.id = doneList.getId();
        holder.name.setText(doneList.getName());
        if (null != currentListId && currentListId.equals(holder.id)) {
            selectedItem = holder.itemView;
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.lightestGray));
        }
    }

    public void setSelectedItem(View selectedItem) {
        this.selectedItem = selectedItem;
    }

    public View getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(doneLists);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public List<DoneList> getItems() {
        return doneLists;
    }

    @Override
    public void setItems(List<DoneList> items) {
        doneLists.clear();
        doneLists.addAll(items);
    }

    @Override
    public void removeItems(List<DoneList> lists) {
        for (DoneList list : lists) {
            doneLists.remove(list);
        }
        notifyDataSetChanged();
    }


    private DoneList getItem(int position) {
        return doneLists.get(position);
    }

    /**
     * Provides a reference to the views for each data item.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private Integer id;
        private TextView name;
        private View itemView;

        ViewHolder(View view) {
            super(view);
            this.itemView = view;
            this.name = view.findViewById(R.id.nav_list_name);
        }
    }

    public interface ListItemClickListener {
        void click(DoneList list);
    }
}
