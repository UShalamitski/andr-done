package com.hose.aureliano.project.done.model;

import android.arch.persistence.room.Entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Entity represents a list of items to do.
 * <p>
 * Date: 11.02.2018.
 *
 * @author evere
 */
@Entity(tableName = "lists")
public class DoneList extends BaseEntity {

    private String name;
    private Integer position;
    private int tasksCount;
    private int doneTasksCount;
    private Long createdDateTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    public int getDoneTasksCount() {
        return doneTasksCount;
    }

    public void setDoneTasksCount(int doneTasksCount) {
        this.doneTasksCount = doneTasksCount;
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
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DoneList doneList = (DoneList) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(doneList))
                .append(name, doneList.name)
                .append(position, doneList.position)
                .append(createdDateTime, doneList.createdDateTime)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(name)
                .append(position)
                .append(createdDateTime)
                .toHashCode();
    }
}
