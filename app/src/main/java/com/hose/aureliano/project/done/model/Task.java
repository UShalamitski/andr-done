package com.hose.aureliano.project.done.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
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
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String listId;
    private String name;
    private Long dueDateTime;
    private boolean dueTimeIsSet;
    private Long remindDateTime;
    private boolean remindTimeIsSet;
    private boolean done;
    private Integer position;
    private Long createdDateTime;

    public Task() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
                .append(position, that.position)
                .append(createdDateTime, that.createdDateTime)
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
                .append(position)
                .append(createdDateTime)
                .toHashCode();
    }

    public enum Fields {
        ID("id"),
        LIST_ID("listId"),
        NAME("name"),
        DONE("done"),
        POSITION("position"),
        DUE_DATE_TIME("dueDateTime"),
        DUE_TIME_IS_SET("dueTimeIsSet"),
        REMIND_DATE_TIME("remindDateTime"),
        REMIND_TIME_IS_SET("remindTimeIsSet"),
        CREATED_DATE_TIME("createdDateTime");

        private String name;

        private Fields(String name) {
            this.name = name;
        }

        public String getFieldName() {
            return name;
        }
    }
}
