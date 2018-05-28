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

    private boolean isAsc;

    public ListProgressComparator(boolean isAsc) {
        this.isAsc = isAsc;
    }

    @Override
    public int compare(DoneList list1, DoneList list2) {
        int result;
        if (0 == list1.getTasksCount() && 0 != list2.getTasksCount()) {
            result = isAsc ? -1 : 1;
        } else if (0 != list1.getTasksCount() && 0 == list2.getTasksCount()) {
            result = isAsc ? 1 : -1;
        } else if (0 == list1.getTasksCount() && 0 == list2.getTasksCount()) {
            result = 0;
        } else {
            result = isAsc
                    ? Double.compare(list1.getDoneTasksCount() * 100d / list1.getTasksCount(),
                    list2.getDoneTasksCount() * 100d / list2.getTasksCount())
                    : Double.compare(list2.getDoneTasksCount() * 100d / list2.getTasksCount(),
                    list1.getDoneTasksCount() * 100d / list1.getTasksCount());
        }
        return result;
    }
}
