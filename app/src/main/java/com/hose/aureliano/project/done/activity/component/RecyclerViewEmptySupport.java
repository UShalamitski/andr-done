package com.hose.aureliano.project.done.activity.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Extends {@link RecyclerView} to provide ability to show empty view if the adapter is empty.
 * <p/>
 * Date: 12.05.2018
 *
 * @author Uladzislau Shalamitski
 */
public class RecyclerViewEmptySupport extends RecyclerView {

    private View emptyView;
    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            setEmptyViewVisibility();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            setEmptyViewVisibility();
        }

        @Override
        public void onChanged() {
            super.onChanged();
            setEmptyViewVisibility();
        }
    };

    /**
     * Controller.
     *
     * @param context application context
     */
    public RecyclerViewEmptySupport(Context context) {
        super(context);
    }

    /**
     * Controller.
     *
     * @param context application context
     * @param attrs   attributes
     */
    public RecyclerViewEmptySupport(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Controller.
     *
     * @param context  application context
     * @param attrs    attributes
     * @param defStyle style
     */
    public RecyclerViewEmptySupport(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }
        emptyObserver.onChanged();
    }

    /**
     * Sets the view to show if the adapter is empty.
     *
     * @param emptyView view to if the adapter is empty
     */
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    private void setEmptyViewVisibility() {
        Adapter<?> adapter = getAdapter();
        if (adapter != null && emptyView != null) {
            if (adapter.getItemCount() == 0) {
                emptyView.setVisibility(View.VISIBLE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        }
    }
}
