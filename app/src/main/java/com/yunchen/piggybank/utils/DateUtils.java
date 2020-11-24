package com.yunchen.piggybank.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public final class DateUtils {

    private static final long DAY_IN_MILLIS = TimeUnit.DAYS.toMillis(1);
    private static final Calendar calendar = Calendar.getInstance();

    public static long getNormalizedUtcDateForToday(){
        long utcNowMills = System.currentTimeMillis();

        TimeZone currentTz = TimeZone.getDefault();

        long gmtOffsetMills = currentTz.getOffset(utcNowMills);

        long timeSinceEpochLocalTimeMillis = utcNowMills+gmtOffsetMills;

        long daysSinceEpochLocal = TimeUnit.MILLISECONDS.toDays(timeSinceEpochLocalTimeMillis);

        return TimeUnit.DAYS.toMillis(daysSinceEpochLocal);
    }

    private static long elapsedDaysSinceEpoch(long utcDate) {
        TimeZone currentTz = TimeZone.getDefault();
        long gmtOffsetMills = currentTz.getOffset(System.currentTimeMillis());
        long modifiedDate = utcDate + gmtOffsetMills;
        return TimeUnit.MILLISECONDS.toDays(modifiedDate);
    }

    public static long normalizeDate(long date) {
        long daysSinceEpoch = elapsedDaysSinceEpoch(date);
        return daysSinceEpoch * DAY_IN_MILLIS;
    }

    public static long getNormalizedUtcDateForThisMonth() throws ParseException {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-01 00:00:00");
        Date date = new Date();
        String dateStr = fmt.format(date);
        Date dateFirst = fmt.parse(dateStr);
        return normalizeDate(dateFirst.getTime());
    }

    public static long getNormalizedUtcDateForNextMonth() throws ParseException {
        Date date = new Date(getNormalizedUtcDateForThisMonth());
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,1);
        return normalizeDate(calendar.getTimeInMillis());
    }


    public static long getNormalizedUtcDateForLastMonth() throws ParseException {
        Date date = new Date(getNormalizedUtcDateForThisMonth());
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,-1);
        return normalizeDate(calendar.getTimeInMillis());
    }

    public static boolean isTheFirstDayOfMonth(long now){
        Date date = new Date(now);
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day == 1;
    }

    public static String getMonthName(){
        Date date = new Date(getNormalizedUtcDateForToday());
        calendar.setTime(date);
        return MonthName.getName(calendar.get(Calendar.MONTH));
    }
}

enum MonthName {
    JANUARY("JANUARY",0), FEBRUARY("FEBRUARY",1), MARCH("MARCH",2), APRIL("APRIL",3), MAY("MAY",4),
    JUNE("JUNE",5), JULY("JULY",6), AUGUST("AUGUST",7), SEPTEMBER("SEPTEMBER",8),OCTOBER("OCTOBER",9),
    NOVEMBER("NOVEMBER",10), DECEMBER("DECEMBER",11);

    private String name;
    private int index;

    MonthName(String name, int index){
        this.name = name;
        this.index = index;
    }

    static String getName(int index){
        for(MonthName month:MonthName.values()){
            if(index == month.index){
                return month.name;
            }
        }
        return null;
    }
}