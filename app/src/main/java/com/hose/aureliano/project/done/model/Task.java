package com.hose.aureliano.project.done.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Entity represents an item to do.
 * <p>
 * Date: 11.02.2018.
 *
 * @author evere
 */
@Entity(tableName = "tasks",
        foreignKeys = @ForeignKey(entity = DoneList.class, parentColumns = "id", childColumns = "listId"))
public class Task {

    @NonNull
    @PrimaryKey
    private String id;
    private String listId;
    private String name;
    private Long dueDateTime;
    private boolean dueTimeIsSet;
    private Long remindDateTime;
    private boolean remindTimeIsSet;
    private boolean done;

    public Task() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean getDueTimeIsSet() {
        return dueTimeIsSet;
    }

    public void setDueTimeIsSet(boolean dueTimeIsSet) {
        this.dueTimeIsSet = dueTimeIsSet;
    }

    public boolean getRemindTimeIsSet() {
        return remindTimeIsSet;
    }

    public void setRemindTimeIsSet(boolean remindTimeIsSet) {
        this.remindTimeIsSet = remindTimeIsSet;
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
                .append(id, that.id)
                .append(listId, that.listId)
                .append(name, that.name)
                .append(done, that.done)
                .append(dueDateTime, that.dueDateTime)
                .append(dueTimeIsSet, that.dueTimeIsSet)
                .append(remindDateTime, that.remindDateTime)
                .append(remindTimeIsSet, that.remindTimeIsSet)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(listId)
                .append(name)
                .append(done)
                .append(dueDateTime)
                .append(dueTimeIsSet)
                .append(remindDateTime)
                .append(remindTimeIsSet)
                .toHashCode();
    }

    public enum Fields {
        ID("id"),
        LIST_ID("listId"),
        NAME("name"),
        DONE("done"),
        DUE_DATE_TIME("dueDateTime"),
        DUE_TIME_IS_SET("dueTimeIsSet"),
        REMIND_DATE_TIME("remindDateTime"),
        REMIND_TIME_IS_SET("remindTimeIsSet");

        private String name;

        private Fields(String name) {
            this.name = name;
        }

        public String getFieldName() {
            return name;
        }
    }
}
