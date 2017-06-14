package com.example.vaio.technicalnews.model.application;

import java.util.Calendar;

/**
 * Created by vaio on 17/03/2017.
 */

public class MyCalendar {
    private static Calendar calendar = Calendar.getInstance();

    public static int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth() {
        return calendar.get(Calendar.MONTH);
    }

    public static int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public static int getSecond() {
        return calendar.get(Calendar.SECOND);
    }

    public static int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public static int getHour() {
        return calendar.get(Calendar.HOUR);
    }

    public static String getDate() {
        return getDay() + "/" + getMonth() + "/" + getYear();
    }

    public static String getTimeStamp() {
        return getHour() + ":" + getMinute() + ":" + getSecond();
    }
}
