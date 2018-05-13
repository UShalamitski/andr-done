package com.hose.aureliano.project.done.service;

import android.content.Context;

import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.DoneListDao;

import java.util.List;

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
     * Inserts instance of {@link DoneList}.
     *
     * @param list instance of {@link DoneList} to insert
     */
    public long insert(DoneList list) {
        return listDao.insert(list);
    }

    /**
     * Updates instance of {@link DoneList}.
     *
     * @param list instance of {@link DoneList} to update
     * @return number affected rows
     */
    public int update(DoneList list) {
        return listDao.update(list);
    }

    /**
     * Updates {@link DoneList}s.
     *
     * @param lists list of {@link DoneList} to update
     */
    public void update(List<DoneList> lists) {
        for (DoneList list : lists) {
            update(list);
        }
    }

    /**
     * Return list with all {@link DoneList}.
     */
    public List<DoneList> getLists() {
        return listDao.read();
    }
}
