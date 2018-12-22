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
    private TaskService taskService;
    private Context context;

    public ListService(Context context) {
        this.context = context;
        this.listDao = DatabaseCreator.getDatabase(context).getDoneListDao();
        this.taskService = new TaskService(context);
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
     * Deletes specified {@link DoneList}.
     *
     * @param list list to delete
     * @return number affected rows or -1
     */
    public int delete(DoneList list) {
        return listDao.delete(list);
    }

    /**
     * Deletes specified {@link DoneList} by its identifier.
     *
     * @param listId list identifier
     */
    public void delete(Integer listId) {
        DatabaseCreator.getDatabase(context).runInTransaction(() -> {
            taskService.deleteByListId(listId);
            listDao.delete(listId);
        });
    }

    /**
     * Return list with all {@link DoneList}.
     */
    public List<DoneList> getLists() {
        return listDao.read();
    }
}
