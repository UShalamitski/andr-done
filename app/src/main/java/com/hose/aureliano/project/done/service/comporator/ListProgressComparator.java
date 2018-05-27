package com.hose.aureliano.project.done.service.comporator;

import com.hose.aureliano.project.done.model.DoneList;

import java.util.Comparator;

/**
 * Compares {@link DoneList}s by progress.
 * <p>
 * Date: 27.05.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class ListProgressComparator implements Comparator<DoneList> {

    @Override
    public int compare(DoneList list1, DoneList list2) {
        int result;
        if (0 == list1.getTasksCount() && 0 == list2.getTasksCount()) {
            result = 0;
        } else if (0 != list1.getTasksCount() && 0 == list2.getTasksCount()) {
            result = 1;
        } else if (0 == list1.getTasksCount() && 0 != list2.getTasksCount()) {
            result = -1;
        } else {
            result = Double.compare(list1.getDoneTasksCount() * 100d / list1.getTasksCount(),
                    list2.getDoneTasksCount() * 100d / list2.getTasksCount());
        }
        return result;
    }
}
