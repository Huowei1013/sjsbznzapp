package com.bysj.znzapp.utils;

import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author: huowei
 * @CreateDate: 2021/4/14 11:51
 * @Describe:
 * @Version: 1.0.0
 */

public class DateUtil {

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDate(long mi) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date(mi));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Date getDateFromYYMM(String yymm) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        try {
            return format.parse(yymm);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateYYYYMMDD(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String removeHMS(String s) {
        if (TextUtils.isEmpty(s)) {
            return s;
        }
        if (!TextUtils.isEmpty(s)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = format.parse(s);
                s = new SimpleDateFormat("yyyy-MM-dd").format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentDateYYMMDDHHMMSS() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getYYMMDDHHMMSS(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentDateYYMMDDHHMM() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(new Date());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentDateHHMMSS() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentDateStrYYMMDD() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new Date());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentDateStrYYMM() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return formatter.format(new Date());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateStrYYMM(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        return formatter.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateStrYYMM2(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        return formatter.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDateStrYYMMDD2(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Date getDateFromYYMMDDHHMMSS(String s) {
        if (TextUtils.isEmpty(s)) {
            return null;
        }
        if (!TextUtils.isEmpty(s)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                return format.parse(s);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getStringFromYYMMDDHHMMSSDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentDateStrYYMM2() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
        return formatter.format(new Date());
    }

    /**
     * 判断时间是不是今天
     *
     * @param date
     * @return 是返回true，不是返回false
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isToday(Date date) {
        //当前时间
        Date now = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
        //获取今天的日期
        String nowDay = sf.format(now);


        //对比的时间
        String day = sf.format(date);

        return day.equals(nowDay);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String transferDate(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat resultFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = format.parse(dateStr);
            return resultFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param currentTime 2018-03-14 16:54:45
     * @param onDutyTime  08:30
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isBeforeDutyTime(String currentTime, String onDutyTime) {
        if (TextUtils.isEmpty(onDutyTime) || TextUtils.isEmpty(currentTime)) {
            return false;
        }
        String temp = "2013-05-03" + onDutyTime;
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-ddHH:mm");// 格式化类型
        Date d2 = null;
        try {
            d2 = format2.parse(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long dutytime = d2.getHours() * 60 * 60 * 1000 + d2.getMinutes() * 60 * 1000;
        Log.d("yeying", "dutyTime" + dutytime);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.setTime(getDateFromYYMMDDHHMMSS(currentTime));
        long current = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        dutytime = morning + dutytime;
        Log.d("yeying", "dutyTime" + dutytime);
        Log.d("yeying", "current" + current);
        return current < dutytime;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean isBeforeDutyTime(String onDutyTime) {
        if (TextUtils.isEmpty(onDutyTime)) {
            return false;
        }
        String temp = "2013-05-03_" + onDutyTime;
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd_HH:mm");// 格式化类型
        Date d2 = null;
        try {
            d2 = format2.parse(temp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long dutytime = d2.getHours() * 60 * 60 * 1000 + d2.getMinutes() * 60 * 1000;
        Log.d("yeying", "dutyTime" + dutytime);
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long current = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        dutytime = morning + dutytime;
        Log.d("yeying", "dutyTime" + dutytime);
        Log.d("yeying", "current" + current);
        return current < dutytime;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getNextYearToday() {
        Date date = new Date();
        date.setYear(date.getYear() + 1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    // 倒计时
    public static String formatCountDown(long elapsed) {
        int hour, minute, second, milli;

        milli = (int) (elapsed % 1000);
        elapsed = elapsed / 1000;

        second = (int) (elapsed % 60);
        elapsed = elapsed / 60;

        minute = (int) (elapsed % 60);
        elapsed = elapsed / 60;

        hour = (int) (elapsed % 60);

        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getMonthBegin(String s) {
        Date date = getDateFromYYMM(s);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date1 = calendar.getTime();
        return getStringFromYYMMDDHHMMSSDate(date1);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getMonthEnd(String s) {
        Date date = getDateFromYYMM(s);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        Date date1 = calendar.getTime();
        return getStringFromYYMMDDHHMMSSDate(date1);


    }
}
