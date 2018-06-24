package com.hose.aureliano.project.done.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hose.aureliano.project.done.R;
import com.hose.aureliano.project.done.activity.TaskDetailsActivity;
import com.hose.aureliano.project.done.activity.adapter.api.Adapter;
import com.hose.aureliano.project.done.activity.callback.DiffUtilCallback;
import com.hose.aureliano.project.done.model.Task;
import com.hose.aureliano.project.done.model.TasksViewEnum;
import com.hose.aureliano.project.done.service.TaskService;
import com.hose.aureliano.project.done.service.schedule.alarm.AlarmService;
import com.hose.aureliano.project.done.utils.ActivityUtils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Adapter for {@link RecyclerView} of the {@link Task}s.
 * <p>
 * Date: 05.02.2018.
 *
 * @author evere
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> implements Adapter<Task> {

    private static int COLOR_RED_SECONDARY;
    private static int COLOR_BLACK_PRIMARY;
    private static int COLOR_BLACK_SECONDARY;
    private static int COLOR_WHITE;
    private static int COLOR_GRAY_LIGHT;

    private Set<Integer> selectedIdsSet;
    private TaskService taskService;
    private List<Task> taskList;
    private Context context;
    private Integer listId;
    private TasksViewEnum viewEnum;
    private ActionMode actionMode;

    private ExecutorService executor = Executors.newFixedThreadPool(2);

    /**
     * Controller.
     *
     * @param context context
     * @param listId  identifier of the list
     */
    public TaskAdapter(Context context, Integer listId,
                       TasksViewEnum viewEnum) {
        taskService = new TaskService(context);
        this.context = context;
        this.listId = listId;
        this.viewEnum = viewEnum;
        selectedIdsSet = new HashSet<>();
        initStaticResources();
        refresh();
    }

    /**
     * @return {@link TasksViewEnum}.
     */
    public TasksViewEnum getTasksView() {
        return viewEnum;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.checkBox.setOnClickListener(v -> ActivityUtils.vibrate(context));

        view.setOnClickListener(itemView -> {
            if (null != actionMode) {
                toggleSelection(viewHolder, actionMode);
                ActivityUtils.vibrate(context);
            } else {
                Intent intent = new Intent(context, TaskDetailsActivity.class);
                Task task = getItem(viewHolder.getAdapterPosition());
                intent.putExtra(Task.Fields.LIST_ID.fieldName(), task.getListId());
                intent.putExtra(Task.Fields.LIST_NAME.fieldName(), task.getListName());
                intent.putExtra(Task.Fields.NAME.fieldName(), task.getName());
                intent.putExtra(Task.Fields.NOTE.fieldName(), task.getNote());
                intent.putExtra(Task.Fields.ID.fieldName(), task.getId());
                intent.putExtra(Task.Fields.DONE.fieldName(), task.getDone());
                intent.putExtra(Task.Fields.DUE_DATE_TIME.fieldName(), task.getDueDateTime());
                intent.putExtra(Task.Fields.REMIND_DATE_TIME.fieldName(), task.getRemindDateTime());
                intent.putExtra(Task.Fields.CREATED_DATE_TIME.fieldName(), task.getCreatedDateTime());
                intent.putExtra(Task.Fields.POSITION.fieldName(), task.getPosition());
                intent.putExtra(Task.Fields.REPEAT_TYPE.fieldName(),
                        null != task.getRepeatType() ? task.getRepeatType().name() : null);
                context.startActivity(intent,
                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context).toBundle());
            }
        });

        view.setOnLongClickListener(longClickView -> {
            if (null != actionMode) {
                if (1 == CollectionUtils.size(selectedIdsSet) && selectedIdsSet.contains(viewHolder.getAdapterPosition())) {
                    return false;
                } else {
                    toggleSelection(viewHolder, actionMode);
                }
            }
            return true;
        });
        view.setTag(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = getItem(position);
        holder.checkBox.setTag(task.getId());
        holder.name.setText(task.getName());
        holder.name.setTextColor(task.getDone() ? COLOR_BLACK_SECONDARY : COLOR_BLACK_PRIMARY);
        crossOutText(holder, task.getDone());

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.getDone());

        if (null != task.getDueDateTime()) {
            holder.dueDateText.setText(ActivityUtils.getStringDate(context, task.getDueDateTime(), false));
        }
        if (null != task.getRemindDateTime()) {
            holder.reminderText.setText(ActivityUtils.getStringDate(context, task.getRemindDateTime(), true));
        }
        if (null != viewEnum) {
            setVisibility(holder.listNameDelimiter, true);
            setVisibility(holder.listName, true);
            holder.listName.setText(task.getListName());
        }

        if (null != task.getDueDateTime() || null != task.getRemindDateTime() && !task.getDone()) {
            setVisibility(holder.taskInfoLayout, true);
            setVisibility(holder.dueDateIcon, null != task.getDueDateTime());
            setVisibility(holder.dueDateText, null != task.getDueDateTime());
            if (!task.getDone() && null != task.getRemindDateTime()
                    && System.currentTimeMillis() < task.getRemindDateTime()) {
                setVisibility(holder.dueDateAndReminderDelimiter, null != task.getDueDateTime());
                setVisibility(holder.reminderIcon, true);
                setVisibility(holder.reminderText, null == task.getDueDateTime());
            } else {
                setVisibility(holder.dueDateAndReminderDelimiter, false);
                setVisibility(holder.reminderIcon, false);
                setVisibility(holder.reminderText, false);
            }
            setVisibility(holder.repeatDelimiter, null != task.getRepeatType());
            setVisibility(holder.repeatIcon, null != task.getRepeatType());
            colorDueDate(task, holder, task.getDone());
        } else {
            setVisibility(holder.taskInfoLayout, false);
        }

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            crossOutText(holder, isChecked);
            Task currentTask = getItem(buttonView);
            Task newTask = null;
            currentTask.setDone(isChecked);

            if (isChecked) {
                if (null != currentTask.getRemindDateTime()) {
                    setVisibility(holder.reminderIcon, false);
                    setVisibility(holder.reminderText, false);
                    setVisibility(holder.dueDateAndReminderDelimiter, false);
                    AlarmService.cancelTaskReminder(context, currentTask);
                }
                if (null != currentTask.getRepeatType()) {
                    setVisibility(holder.repeatDelimiter, false);
                    setVisibility(holder.repeatIcon, false);
                    newTask = taskService.createRepetitiveTask(currentTask);
                    currentTask.setRepeatType(null);
                }
            } else if (null != currentTask.getRemindDateTime()) {
                if (currentTask.getRemindDateTime() > System.currentTimeMillis()) {
                    AlarmService.setTaskReminder(context, currentTask);
                    setVisibility(holder.taskInfoLayout, true);
                    setVisibility(holder.reminderIcon, true);
                    setVisibility(holder.reminderText, null == currentTask.getDueDateTime());
                    setVisibility(holder.dueDateAndReminderDelimiter, null != currentTask.getDueDateTime());
                } else {
                    taskService.deleteReminderDate(currentTask.getId());
                    currentTask.setRemindDateTime(null);
                    setVisibility(holder.reminderIcon, false);
                    setVisibility(holder.reminderText, false);
                    setVisibility(holder.dueDateAndReminderDelimiter, false);
                }
            }
            taskService.update(currentTask);
            notifyItemChanged(holder.getAdapterPosition());

            if (null != newTask) {
                getItems().add(holder.getAdapterPosition() + 1, newTask);
                notifyItemInserted(holder.getAdapterPosition() + 1);
            }

            colorDueDate(currentTask, holder, isChecked);
            holder.name.setTextColor(isChecked ? COLOR_BLACK_SECONDARY : COLOR_BLACK_PRIMARY);
        });
        holder.viewForeground.setBackgroundColor(
                selectedIdsSet.contains(holder.getAdapterPosition()) ? COLOR_GRAY_LIGHT : COLOR_WHITE);
    }

    /**
     * Updates adapter items showing in UI with animation.
     */
    public void updatesAdapterItems() {
        executor.submit(() -> {
            List<Task> oldTasks = new ArrayList<>(getItems());
            if (null != listId) {
                setItems(taskService.getTasks(listId));
            } else {
                setItems(taskService.getTasksForView(viewEnum));
            }
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtilCallback<>(oldTasks, getItems()));
            ((Activity) context).runOnUiThread(() -> {
                result.dispatchUpdatesTo(this);
            });
        });
    }

    /**
     * Sets action mode.
     *
     * @param actionMode instance of {@link ActionMode}
     */
    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }

    /**
     * @return set of selected {@link Task}s.
     */
    public List<Task> getSelectedTasks() {
        List<Task> selectedTasks = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(selectedIdsSet)) {
            for (Integer position : selectedIdsSet) {
                selectedTasks.add(taskList.get(position));
            }
        }
        return selectedTasks;
    }

    /**
     * Clear set of selected items.
     */
    public void clearSelection() {
        setActionMode(null);
        for (Integer id : selectedIdsSet) {
            notifyItemChanged(id);
        }
        selectedIdsSet.clear();
    }

    /**
     * Selects all items.
     */
    public void selectAll() {
        for (int i = 0; i < CollectionUtils.size(taskList); i++) {
            selectedIdsSet.add(i);
        }
        notifyDataSetChanged();
        if (null != actionMode) {
            actionMode.setTitle(context.getString(R.string.task_selected, CollectionUtils.size(selectedIdsSet)));
        }
    }

    public void toggleSelection(TaskAdapter.ViewHolder viewHolder, ActionMode actionMode) {
        int pos = viewHolder.getAdapterPosition();
        if (selectedIdsSet.contains(pos)) {
            selectedIdsSet.remove(viewHolder.getAdapterPosition());
            viewHolder.viewForeground.setBackgroundColor(COLOR_WHITE);
            if (CollectionUtils.isEmpty(selectedIdsSet)) {
                notifyItemChanged(pos);
                actionMode.finish();
            }
        } else {
            selectedIdsSet.add(viewHolder.getAdapterPosition());
            viewHolder.viewForeground.setBackgroundColor(COLOR_GRAY_LIGHT);
        }
        actionMode.setTitle(context.getString(R.string.task_selected, CollectionUtils.size(selectedIdsSet)));
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(taskList);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @return available position for new task.
     */
    public int getAvailablePosition() {
        int position = 0;
        for (Task task : getItems()) {
            if (task.getPosition() > position) {
                position = task.getPosition();
            }
        }
        return ++position;
    }

    /**
     * Sets position for tasks as their index.
     */
    public void updatePositions() {
        int position = 0;
        for (Task task : getItems()) {
            task.setPosition(position++);
        }
    }

    @Override
    public List<Task> getItems() {
        return taskList;
    }

    @Override
    public void setItems(List<Task> items) {
        taskList.clear();
        taskList.addAll(items);
    }

    /**
     * Refreshes data on UI.
     */
    public void refresh() {
        if (null != listId) {
            taskList = taskService.getTasks(listId);
        } else if (null != viewEnum) {
            taskList = taskService.getTasksForView(viewEnum);
        }
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

    @Override
    public void removeItems(List<Task> tasks) {
        taskList.removeAll(new HashSet<>(tasks));
        notifyDataSetChanged();
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
        if (0 == COLOR_WHITE) {
            COLOR_WHITE = ContextCompat.getColor(context, R.color.white);
        }
        if (0 == COLOR_GRAY_LIGHT) {
            COLOR_GRAY_LIGHT = ContextCompat.getColor(context, R.color.lightGray);
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
        private CheckBox checkBox;
        private RelativeLayout taskInfoLayout;
        private ImageView dueDateIcon;
        private TextView dueDateText;
        private TextView dueDateAndReminderDelimiter;
        private ImageView reminderIcon;
        private TextView reminderText;
        private TextView repeatDelimiter;
        private ImageView repeatIcon;
        private TextView listNameDelimiter;
        private TextView listName;

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
            this.repeatDelimiter = view.findViewById(R.id.task_info_delimiter_before_repeat);
            this.repeatIcon = view.findViewById(R.id.task_info_repeat_icon);
            this.reminderIcon = view.findViewById(R.id.task_info_reminder_icon);
            this.reminderText = view.findViewById(R.id.task_info_reminder_text);
            this.listNameDelimiter = view.findViewById(R.id.task_info_delimiter_before_list_name);
            this.listName = view.findViewById(R.id.task_info_list_name);
            this.name = view.findViewById(R.id.task_name);
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
