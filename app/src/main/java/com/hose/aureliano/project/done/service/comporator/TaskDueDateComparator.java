package com.hose.aureliano.project.done.service.comporator;

import com.hose.aureliano.project.done.model.Task;

import java.util.Comparator;

/**
 * Compares {@link Task}s by {@link Task#getDueDateTime()}.
 * <p>
 * Date: 27.05.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class TaskDueDateComparator implements Comparator<Task> {

    @Override
    public int compare(Task task1, Task task2) {
        int result;
        if (null == task1.getDueDateTime() && null != task2.getDueDateTime()) {
            result = 1;
        } else if (null != task1.getDueDateTime() && null == task2.getDueDateTime()) {
            result = -1;
        } else if (null == task1.getDueDateTime() && null == task2.getDueDateTime()) {
            result = 0;
        } else {
            result = task1.getDueDateTime().compareTo(task2.getDueDateTime());
        }
        return result;
    }
}
