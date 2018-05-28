package com.hose.aureliano.project.done.service.comporator;

import com.hose.aureliano.project.done.model.DoneList;

import java.util.Comparator;

/**
 * Compares {@link DoneList}s by {@link DoneList#getCreatedDateTime()}.
 * <p>
 * Date: 27.05.2018.
 *
 * @author Uladzislau Shalamitski
 */
public class ListCreateDateComparator implements Comparator<DoneList> {

    private boolean isAsc;

    /**
     * Constructor.
     *
     * @param isAsc shows whether items should be sorted by ascending or not
     */
    public ListCreateDateComparator(boolean isAsc) {
        this.isAsc = isAsc;
    }

    @Override
    public int compare(DoneList list1, DoneList list2) {
        int result;
        if (null == list1.getCreatedDateTime() && null != list2.getCreatedDateTime()) {
            result = isAsc ? 1 : -1;
        } else if (null != list1.getCreatedDateTime() && null == list2.getCreatedDateTime()) {
            result = isAsc ? -1 : 1;
        } else if (null == list1.getCreatedDateTime() && null == list2.getCreatedDateTime()) {
            result = 0;
        } else {
            result = isAsc
                    ? list1.getCreatedDateTime().compareTo(list2.getCreatedDateTime())
                    : list2.getCreatedDateTime().compareTo(list1.getCreatedDateTime());
        }
        return result;
    }
}
