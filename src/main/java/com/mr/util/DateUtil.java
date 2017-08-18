package com.mr.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/19.
 */
public class DateUtil {
    //得到当前时间
    public static String todayFormate(String temple){
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat( temple).format(cal.getTime());
    }

    //得到后/前的日期
    public static String getNdaysDate(int n,String temple){
        Calendar cal   =   Calendar.getInstance();
        cal.add(Calendar.DATE,   n);
        return new SimpleDateFormat( temple).format(cal.getTime());
    }
    //得到两个日期相差天数
    public static int daysBetween(Date date1, Date date2)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date2);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }

}
