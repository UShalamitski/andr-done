package com.hose.aureliano.project.done.activity.callback;

import android.support.v7.util.DiffUtil;

import com.hose.aureliano.project.done.model.BaseEntity;

import java.util.List;

/**
 * A Callback class used by DiffUtil while calculating the diff between two lists.
 */
public class DiffUtilCallback<T extends BaseEntity> extends DiffUtil.Callback {

    private final List<T> oldList;
    private final List<T> newList;

    /**
     * Constructor.
     *
     * @param oldList list with old elements
     * @param newList list with new elements
     */
    public DiffUtilCallback(List<T> oldList, List<T> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T oldItem = oldList.get(oldItemPosition);
        T newItem = newList.get(newItemPosition);
        return oldItem.getId().equals(newItem.getId());
    }
}
