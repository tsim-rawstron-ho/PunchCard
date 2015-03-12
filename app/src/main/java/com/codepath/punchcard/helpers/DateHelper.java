package com.codepath.punchcard.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {
    private static String format = "yyyy/MM/dd HH:mm:ss";

    /**
     * Converts a date from the local time-zone to UTC.
     */
    public static Date localTimeToUTC(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gmtTime = new Date(simpleDateFormat.format(time));
        return gmtTime;
    }
}
