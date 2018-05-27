package com.hose.aureliano.project.done.activity.dialog;

import android.app.AlertDialog;
import android.content.Context;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.model.DoneList;
import com.hose.aureliano.project.done.service.ListService;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a new dialog to select list.
 * <p>
 * Date: 5/25/2018.
 *
 * @author Uladzislau_Shalamits
 */
public class SelectListModal {

    /**
     * Creates a new dialog to select list.
     *
     * @param context application context
     * @param handler instance of {link SelectListDialogHandler}
     */
    public SelectListModal(Context context, SelectListDialogHandler handler) {
        ListService listService = new ListService(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.modal_select_list));

        List<DoneList> lists = listService.getLists();
        List<String> listsNames = new ArrayList<>(CollectionUtils.size(lists));
        for (DoneList list : lists) {
            listsNames.add(list.getName());
        }
        builder.setItems(listsNames.toArray(new String[CollectionUtils.size(lists)]), (dialog, which) -> {
            handler.handle(lists.get(which).getId());
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    /**
     * Interface that handles action on on list selection.
     */
    public interface SelectListDialogHandler {

        /**
         * Handles action on list selection.
         *
         * @param listId identifier of selected list
         */
        void handle(Integer listId);
    }
}
