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
    private Long dueDateAndTime;
    private boolean dueTimeIsSet;
    private Long remindDate;
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

    public Long getDueDateAndTime() {
        return dueDateAndTime;
    }

    public void setDueDateAndTime(Long dueDateAndTime) {
        this.dueDateAndTime = dueDateAndTime;
    }

    public Long getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Long remindDate) {
        this.remindDate = remindDate;
    }

    public boolean getDueTimeIsSet() {
        return dueTimeIsSet;
    }

    public void setDueTimeIsSet(boolean dueTimeIsSet) {
        this.dueTimeIsSet = dueTimeIsSet;
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
                .append(dueDateAndTime, that.dueDateAndTime)
                .append(dueTimeIsSet, that.dueTimeIsSet)
                .append(remindDate, that.remindDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(listId)
                .append(name)
                .append(done)
                .append(dueDateAndTime)
                .append(dueTimeIsSet)
                .append(remindDate)
                .toHashCode();
    }
}
