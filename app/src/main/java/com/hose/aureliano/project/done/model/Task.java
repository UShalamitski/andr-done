package com.hose.aureliano.project.done.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.TypeConverters;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Entity represents an item to do.
 * <p>
 * Date: 11.02.2018.
 *
 * @author Uladzislau Shalamitski
 */
@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = DoneList.class, parentColumns = "id", childColumns = "listId"))
public class Task extends BaseEntity {

    private Integer listId;
    private String listName;
    private String name;
    private String note;
    private Long dueDateTime;
    private Long remindDateTime;
    private boolean done;
    private Integer position;
    private Long createdDateTime;
    @TypeConverters(TaskRepeatEnum.class)
    private TaskRepeatEnum repeatType;

    public Task() {
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
        this.listId = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean getDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Long getDueDateTime() {
        return dueDateTime;
    }

    public void setDueDateTime(Long dueDateTime) {
        this.dueDateTime = dueDateTime;
    }

    public Long getRemindDateTime() {
        return remindDateTime;
    }

    public void setRemindDateTime(Long remindDateTime) {
        this.remindDateTime = remindDateTime;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Long getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Long createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public TaskRepeatEnum getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(TaskRepeatEnum repeatType) {
        this.repeatType = repeatType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Task that = (Task) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(that))
                .append(listId, that.listId)
                .append(listName, that.listName)
                .append(name, that.name)
                .append(done, that.done)
                .append(dueDateTime, that.dueDateTime)
                .append(remindDateTime, that.remindDateTime)
                .append(position, that.position)
                .append(createdDateTime, that.createdDateTime)
                .append(repeatType, that.repeatType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(listId)
                .append(listName)
                .append(name)
                .append(note)
                .append(done)
                .append(dueDateTime)
                .append(remindDateTime)
                .append(position)
                .append(createdDateTime)
                .append(repeatType)
                .toHashCode();
    }

    public enum Fields {
        ID("id"),
        LIST_ID("listId"),
        LIST_NAME("listName"),
        NOTE("note"),
        NAME("name"),
        DONE("done"),
        POSITION("position"),
        DUE_DATE_TIME("dueDateTime"),
        REMIND_DATE_TIME("remindDateTime"),
        CREATED_DATE_TIME("createdDateTime"),
        REPEAT_TYPE("repeatType");

        private String name;

        Fields(String name) {
            this.name = name;
        }

        public String fieldName() {
            return name;
        }
    }
}
