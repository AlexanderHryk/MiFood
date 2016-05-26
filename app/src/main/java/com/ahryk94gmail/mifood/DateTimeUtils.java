package com.ahryk94gmail.mifood;

import java.util.Calendar;

public class DateTimeUtils {

    public static int startOfDay(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return (int) (cal.getTimeInMillis() / 1000);
    }

    public static int endOfDay(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        return (int) (cal.getTimeInMillis() / 1000);
    }

    public static int[] convertSecondsToHoursMinutes(int seconds) {
        int hours = seconds / (60 * 60);
        int minutes = (seconds % (60 * 60)) / 60;
        int[] result = {hours, minutes};

        return result;
    }
}
