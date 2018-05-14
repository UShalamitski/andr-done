package com.hose.aureliano.project.done.activity.adapter.api;

import java.util.List;

/**
 * Base class for adapter.
 * <p>
 * Date: 05.02.2018.
 *
 * @author Uladzislau Shalamitslo
 */
public interface Adapter<T> {

    /**
     * @return list of adapter items.
     */
    List<T> getItems();

    /**
     * Remove adapter items.
     *
     * @param items list of {@link T}s to remove
     */
    void removeItems(List<T> items);
}
