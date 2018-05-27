package com.hose.aureliano.project.done.service.comporator;

import com.hose.aureliano.project.done.model.Task;

import java.util.Comparator;

/**
 * Compares {@link Task}s by {@link Task#getCreatedDateTime()}.
 * <p>
 * Date: 27.05.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class TaskCreateDateComparator implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2) {
        int result;
        if (null == task1.getCreatedDateTime() && null != task2.getCreatedDateTime()) {
            result = 1;
        } else if (null != task1.getCreatedDateTime() && null == task2.getCreatedDateTime()) {
            result = -1;
        } else if (null == task1.getCreatedDateTime() && null == task2.getCreatedDateTime()) {
            result = 0;
        } else {
            result = task1.getCreatedDateTime().compareTo(task2.getCreatedDateTime());
        }
        return result;
    }
}
