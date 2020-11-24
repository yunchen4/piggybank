package com.yunchen.piggybank.database.converter;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {

    static DateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");

    @TypeConverter
    public static Date strToDate(String dateStr) throws ParseException {
        Date date = fmt.parse(dateStr);
        return date;
    }

    @TypeConverter
    public static String dateToStr(Date date){
        String dateStr = fmt.format(date);
        return dateStr;
    }

    @TypeConverter
    public static long strToLong(String date) throws ParseException {
        Date dateD = strToDate(date);
        return dateD.getTime();
    }

    @TypeConverter
    public static String longToStr(long date) throws ParseException {
        String dateS = dateToStr(new Date(date));
        return dateS;
    }
}
