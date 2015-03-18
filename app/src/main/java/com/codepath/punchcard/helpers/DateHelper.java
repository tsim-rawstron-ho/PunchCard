package com.codepath.punchcard.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

    public static Date parseDate(String dateString) {
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
      Date d = null;
      try {
        d = formatter.parse(dateString);
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return d;
    }

    public static Date getSelectedDate(int year, int month, int day) {
      return parseDate((month + 1) + "/" + day + "/" + year);
    }

    public static String getSelectedDateString(int year, int month, int day) {
      Date selectedDate = getSelectedDate(year, month, day);
      Date date = new Date();
      if (year % 100 == date.getYear() % 100 && month == date.getMonth() && day == date.getDay() + 1) {
        return "Today";
      } else {
          return formateDate(selectedDate);
      }
    }

    public static String formateDate(Date date) {
        DateFormat df = new SimpleDateFormat("EEE, MMM d", Locale.US);
        return df.format(date);
    }

    public static String formateTime(Date startTime) {
        DateFormat df = new SimpleDateFormat("KK:mm a", Locale.US);
        return df.format(startTime);
    }
}
