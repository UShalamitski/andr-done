package com.hose.aureliano.project.done.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A suite of utilities surrounding the use of the {@link java.util.Calendar} object.
 * <p>
 * Date: 06/09/2018.
 *
 * @author Uladzislau_Shalamits
 */
public class CalendarUtils {

    private CalendarUtils() {
        throw new AssertionError("Shouldn't be called directly");
    }

    /**
     * @return time in milliseconds for today at 23:59:999
     */
    public static long getTodayDateTimeInMillis() {
        return getDateTimeInMillis().getTimeInMillis();
    }

    /**
     * @return time in milliseconds for tomorrow at 23:59:59.999
     */
    public static long getTomorrowDateTimeInMillis() {
        GregorianCalendar calendar = getDateTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    /**
     * @return time in milliseconds for next monday at 23:59:59.999
     */
    public static long getNextWeekDateTimeInMillis() {
        GregorianCalendar calendar = getDateTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DATE, 1);
        }
        return calendar.getTimeInMillis();
    }

    /**
     * Return time in milliseconds for particular year, month and day of month at 23:59:59.999.
     *
     * @param year       year
     * @param month      month
     * @param dayOfMonth day of month
     * @return time in milliseconds for particular year, month and day of month at 23:59:59.999
     */
    public static long getTimeInMillis(int year, int month, int dayOfMonth) {
        GregorianCalendar calendar = new GregorianCalendar(year, month, dayOfMonth);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * @return time in milliseconds for next overdue tasks reminder.
     */
    public static long getTimeToShowOverdueTasksReminder() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTimeInMillis();
    }

    private static GregorianCalendar getDateTimeInMillis() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }
}
