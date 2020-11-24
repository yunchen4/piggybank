package com.yunchen.piggybank.database.converter;

import androidx.room.TypeConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class AmountConverter {

    @TypeConverter
    public static Long toLong(BigDecimal value){
        return value==null? null:value.multiply(new BigDecimal(100)).longValue();
    }

    @TypeConverter
    public static String toString(Long value){
        if(value!=null && value!=((long)0)) {
            BigDecimal bd = new BigDecimal(value).divide(new BigDecimal(100));
            DecimalFormat df = new DecimalFormat("#.00");
            if (bd.compareTo(new BigDecimal(1))==-1
                    && bd.compareTo(new BigDecimal(0)) == 1){
                return "0"+df.format(bd);
            }
            return df.format(bd);
        }
        return "0.00";
    }

}
