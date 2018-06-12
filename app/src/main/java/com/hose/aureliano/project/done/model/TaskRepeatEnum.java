package com.hose.aureliano.project.done.model;

import android.arch.persistence.room.TypeConverter;

/**
 * Enum that contains all task repeat types.
 * <p>
 * Date: 6/6/2018.
 *
 * @author Uladzislau_Shalamits
 */
public enum TaskRepeatEnum {

    /**
     * Task for every day.
     */
    EVERY_DAY,

    /**
     * Task for every month.
     */
    EVERY_MONTH,

    /**
     * Task for every week.
     */
    EVERY_WEEK,

    /**
     * Task for every year.
     */
    EVERY_YEAR,

    /**
     * Task for working days.
     */
    WORKING_DAYS,

    /**
     * Task for weekends.
     */
    WEEKENDS;

    @TypeConverter
    public static TaskRepeatEnum getTaskRepeatEnumFromString(String value) {
        return null != value ? TaskRepeatEnum.valueOf(value) : null;
    }

    @TypeConverter
    public static String getStringFromTaskRepeatEnum(TaskRepeatEnum repeatEnum) {
        return null != repeatEnum ? repeatEnum.name() : null;
    }
}
