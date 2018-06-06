package com.hose.aureliano.project.done.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

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
    private String name;
    private Long dueDateTime;
    private Long remindDateTime;
    private boolean done;
    private Integer position;
    private Long createdDateTime;

    public Task() {
    }

    public Integer getListId() {
        return listId;
    }

    public void setListId(Integer listId) {
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
                .appendSuper(super.equals(that))
                .append(listId, that.listId)
                .append(name, that.name)
                .append(done, that.done)
                .append(dueDateTime, that.dueDateTime)
                .append(remindDateTime, that.remindDateTime)
                .append(position, that.position)
                .append(createdDateTime, that.createdDateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(listId)
                .append(name)
                .append(done)
                .append(dueDateTime)
                .append(remindDateTime)
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
        REMIND_DATE_TIME("remindDateTime"),
        CREATED_DATE_TIME("createdDateTime");

        private String name;

        Fields(String name) {
            this.name = name;
        }

        public String fieldName() {
            return name;
        }
    }
}
