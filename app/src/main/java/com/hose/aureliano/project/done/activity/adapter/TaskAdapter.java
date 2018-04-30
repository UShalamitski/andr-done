package com.hose.aureliano.project.done.activity.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.dialog.TaskModal;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.repository.DatabaseCreator;
import com.hose.aureliano.project.done.repository.dao.TaskDao;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Adapter for {@link RecyclerView} of the {@link Task}s.
 * <p>
 * Date: 05.02.2018.
 *
 * @author evere
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private static int COLOR_RED_SECONDARY;
    private static int COLOR_BLACK_PRIMARY;
    private static int COLOR_BLACK_SECONDARY;

    private FragmentManager fragmentManager;
    private TaskService taskService;
    private TaskDao taskDao;
    private List<Task> taskList;
    private Context context;
    private String listId;

    /**
     * Controller.
     *
     * @param context         context
     * @param fragmentManager fragment manager
     * @param listId          identifier of the list
     */
    public TaskAdapter(Context context, FragmentManager fragmentManager, String listId) {
        taskDao = DatabaseCreator.getDatabase(context).getTaskDao();
        taskService = new TaskService(context);
        taskList = taskDao.read(listId);
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.listId = listId;
        initStaticResources();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.checkBox.setOnClickListener(v -> ActivityUtils.vibrate(context));
        viewHolder.menu.setOnClickListener(menuView -> {
            Task currentTask = getItem(menuView);
            PopupMenu popupMenu = new PopupMenu(context, menuView);
            popupMenu.inflate(R.menu.menu_list_more);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_delete:
                        ActivityUtils.showConfirmationDialog(context, R.string.task_delete_confirmation,
                                (dialog, which) -> {
                                    taskService.delete(currentTask);
                                    removeItem(getPosition(menuView));
                                });
                        break;
                    case R.id.menu_edit:
                        DialogFragment dialog = new TaskModal();
                        dialog.setArguments(buildBundle(currentTask));
                        dialog.show(fragmentManager, "task_modal");
                        break;
                }
                return true;
            });
            popupMenu.show();
        });
        view.setOnClickListener(itemView -> {
            viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
            ActivityUtils.vibrate(context);
        });
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = getItem(position);
        holder.checkBox.setTag(task.getId());
        holder.menu.setTag(task.getId());
        holder.name.setText(task.getName());
        holder.name.setTextColor(task.getDone() ? COLOR_BLACK_SECONDARY : COLOR_BLACK_PRIMARY);
        crossOutText(holder, task.getDone());
        holder.checkBox.setChecked(task.getDone());

        if (null != task.getDueDateTime()) {
            holder.dueDateText
                    .setText(ActivityUtils.getStringDate(context, task.getDueDateTime(), task.getDueTimeIsSet()));
        }
        if (null != task.getRemindDateTime()) {
            holder.reminderText
                    .setText(ActivityUtils.getStringDate(context, task.getRemindDateTime(), task.getRemindTimeIsSet()));
        }

        if (null != task.getDueDateTime() || null != task.getRemindDateTime() && !task.getDone()) {
            setVisibility(holder.taskInfoLayout, true);
            setVisibility(holder.dueDateIcon, null != task.getDueDateTime());
            setVisibility(holder.dueDateText, null != task.getDueDateTime());
            setVisibility(holder.dueDateAndReminderDelimiter, null != task.getDueDateTime()
                    && null != task.getRemindDateTime() && !task.getDone());
            setVisibility(holder.reminderIcon, null != task.getRemindDateTime() && !task.getDone());
            setVisibility(holder.reminderText, null == task.getDueDateTime());
            colorDueDate(task, holder, task.getDone());
        } else {
            setVisibility(holder.taskInfoLayout, false);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            crossOutText(holder, isChecked);
            Task currentTask = getItem(buttonView);
            if (currentTask.getDone() != buttonView.isChecked()) {
                currentTask.setDone(buttonView.isChecked());
                taskDao.update(currentTask);
            }
            if (isChecked && currentTask.getRemindTimeIsSet()) {
                setVisibility(holder.reminderIcon, false);
                setVisibility(holder.reminderText, false);
                setVisibility(holder.dueDateAndReminderDelimiter, false);
                AlarmService.cancelAlarm(context, currentTask);
            } else if (!isChecked && currentTask.getRemindTimeIsSet()) {
                if (currentTask.getRemindDateTime() > System.currentTimeMillis()) {
                    AlarmService.setAlarm(context, currentTask);
                    setVisibility(holder.taskInfoLayout, true);
                    setVisibility(holder.reminderIcon, true);
                    setVisibility(holder.reminderText, null == currentTask.getDueDateTime());
                    setVisibility(holder.dueDateAndReminderDelimiter, null != currentTask.getDueDateTime());
                } else {
                    taskService.deleteReminderDate(currentTask.getId());
                    currentTask.setRemindDateTime(null);
                    currentTask.setRemindTimeIsSet(false);
                    setVisibility(holder.reminderIcon, false);
                    setVisibility(holder.reminderText, false);
                    setVisibility(holder.dueDateAndReminderDelimiter, false);
                }
            }
            colorDueDate(currentTask, holder, isChecked);
            holder.name.setTextColor(isChecked ? COLOR_BLACK_SECONDARY : COLOR_BLACK_PRIMARY);
        });
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(taskList);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Task> getTasks() {
        return taskList;
    }

    /**
     * Refreshes data on UI.
     */
    public void refresh() {
        this.taskList = taskDao.read(listId);
        notifyDataSetChanged();
    }

    /**
     * Remove {@link Task} from the list.
     *
     * @param position position of the {@link Task} to remove
     */
    public void removeItem(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Restore task information the list.
     *
     * @param position position of the {@link Task} to restore
     * @param task     instance of {@link Task} to restore
     */
    public void restoreItem(int position, Task task) {
        taskList.add(position, task);
        notifyItemInserted(position);
    }

    /**
     * Retrieves {@link Task} information specified position.
     *
     * @param position position of the {@link Task} to retrieve
     * @return instance of {@link Task} information specified position
     */
    public Task getItem(int position) {
        return taskList.get(position);
    }

    private Task getItem(View view) {
        return getItemById((int) view.getTag());
    }

    private Task getItemById(int taskId) {
        for (Task task : taskList) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }
        throw new NoSuchElementException();
    }

    private int getPosition(View view) {
        int position = 0;
        for (Task task : taskList) {
            if (task.getId().equals(view.getTag())) {
                return position;
            }
            position++;
        }
        throw new NoSuchElementException();
    }

    private void setVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void colorDueDate(Task task, ViewHolder holder, boolean isChecked) {
        if (null != task.getDueDateTime()) {
            long currentTime = System.currentTimeMillis();
            holder.dueDateIcon.setColorFilter(currentTime > task.getDueDateTime() && !isChecked
                    ? COLOR_RED_SECONDARY : COLOR_BLACK_SECONDARY);
            holder.dueDateText.setTextColor(currentTime > task.getDueDateTime() && !isChecked
                    ? COLOR_RED_SECONDARY : COLOR_BLACK_SECONDARY);
        }
    }

    private void crossOutText(ViewHolder holder, boolean checked) {
        holder.name.setPaintFlags(checked
                ? holder.name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                : holder.name.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    private Bundle buildBundle(Task task) {
        Bundle bundle = new Bundle();
        bundle.putInt(Task.Fields.ID.getFieldName(), task.getId());
        bundle.putString(Task.Fields.NAME.getFieldName(), task.getName());
        bundle.putBoolean(Task.Fields.DONE.getFieldName(), task.getDone());
        bundle.putInt(Task.Fields.POSITION.getFieldName(), task.getPosition());
        if (null != task.getDueDateTime()) {
            bundle.putLong(Task.Fields.DUE_DATE_TIME.getFieldName(), task.getDueDateTime());
            bundle.putBoolean(Task.Fields.DUE_TIME_IS_SET.getFieldName(), task.getDueTimeIsSet());
        }
        if (null != task.getRemindDateTime()) {
            bundle.putLong(Task.Fields.REMIND_DATE_TIME.getFieldName(), task.getRemindDateTime());
            bundle.putBoolean(Task.Fields.REMIND_TIME_IS_SET.getFieldName(), task.getRemindTimeIsSet());
        }
        return bundle;
    }

    private void initStaticResources() {
        if (0 == COLOR_RED_SECONDARY) {
            COLOR_RED_SECONDARY = ContextCompat.getColor(context, R.color.red);
        }
        if (0 == COLOR_BLACK_PRIMARY) {
            COLOR_BLACK_PRIMARY = ContextCompat.getColor(context, R.color.black_primary);
        }
        if (0 == COLOR_BLACK_SECONDARY) {
            COLOR_BLACK_SECONDARY = ContextCompat.getColor(context, R.color.black_secondary);
        }
    }

    /**
     * Provides a reference to the views for each task item.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout view;
        private RelativeLayout viewForeground;
        private RelativeLayout viewBackground;
        private TextView backgroundRightText;
        private TextView backgroundLeftText;
        private ImageView backgroundRightIcon;
        private ImageView backgroundLeftIcon;
        private TextView name;
        private ImageView menu;
        private CheckBox checkBox;
        private RelativeLayout taskInfoLayout;
        private ImageView dueDateIcon;
        private TextView dueDateText;
        private TextView dueDateAndReminderDelimiter;
        private ImageView reminderIcon;
        private TextView reminderText;

        /**
         * Controller.
         *
         * @param view task view
         */
        ViewHolder(View view) {
            super(view);
            this.taskInfoLayout = view.findViewById(R.id.task_info_layout);
            this.dueDateIcon = view.findViewById(R.id.task_info_due_icon);
            this.dueDateText = view.findViewById(R.id.task_info_due_text);
            this.dueDateAndReminderDelimiter = view.findViewById(R.id.task_info_delimiter_before_reminder);
            this.reminderIcon = view.findViewById(R.id.task_info_reminder_icon);
            this.reminderText = view.findViewById(R.id.task_info_reminder_text);
            this.name = view.findViewById(R.id.task_name);
            this.menu = view.findViewById(R.id.task_menu);
            this.checkBox = view.findViewById(R.id.task_checkbox);
            this.view = view.findViewById(R.id.task_item_layout);
            this.viewForeground = view.findViewById(R.id.item_view_foreground);
            this.viewBackground = view.findViewById(R.id.item_view_background);
            this.backgroundRightIcon = view.findViewById(R.id.task_background_delete_icon);
            this.backgroundLeftIcon = view.findViewById(R.id.task_background_done_icon);
            this.backgroundRightText = view.findViewById(R.id.task_background_delete_text);
            this.backgroundLeftText = view.findViewById(R.id.task_background_done_text);

            this.dueDateIcon.setVisibility(View.GONE);
            this.dueDateText.setVisibility(View.GONE);
        }

        public FrameLayout getView() {
            return view;
        }

        public RelativeLayout getViewForeground() {
            return viewForeground;
        }

        public RelativeLayout getViewBackground() {
            return viewBackground;
        }

        public TextView getBackgroundRightText() {
            return backgroundRightText;
        }

        public TextView getBackgroundLeftText() {
            return backgroundLeftText;
        }

        public ImageView getBackgroundRightIcon() {
            return backgroundRightIcon;
        }

        public ImageView getBackgroundLeftIcon() {
            return backgroundLeftIcon;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }
}
