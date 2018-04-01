package com.hose.aureliano.project.done.service.schedule;

import android.content.Context;

import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;

/**
 * Service logic for lists.
 * <p/>
 * Date: 11.03.2018
 *
 * @author Uladzislau Shalamitski
 */
public class ListService {

    private DoneListDao listDao;

    public ListService(Context context) {
        listDao = DatabaseCreator.getDatabase(context).getDoneListDao();
    }

    /**
     * Updates instance of {@link DoneList}.
     *
     * @param list instance of {@link DoneList} to update
     */
    public void update(DoneList list) {
        listDao.update(list);
    }
}
