package com.thirdsdks.map.track.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.format.Time;

import com.beagle.component.logger.LogCat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by zhaotao on 2021/01/05 18:13.
 */

public class AlarmUtils {
    public static final String ALARM_ACTION = "com.beagle.jczlapp.alarm";

    public static final long BEGIN_TIME = AlarmManager.INTERVAL_HOUR * 8 + AlarmManager.INTERVAL_FIFTEEN_MINUTES * 2;
    public static final long END_TIME = AlarmManager.INTERVAL_HOUR * 17 + AlarmManager.INTERVAL_FIFTEEN_MINUTES * 2;

    public static final int REQUEST_CODE_BEGIN = 100;
    public static final int REQUEST_CODE_END = 200;

    public static void setBeginAndFinishAlarm(Context context, Class<? extends BroadcastReceiver> classT) {
        LogCat.d("setBeginAndFinishAlarm status is ");
        setAlarm(context, getBeginTimeInDay(), true, classT);
        setAlarm(context, getEndTimeInDay(), false, classT);
    }

    public static void cancellAllAlarm(Context context, Class<? extends BroadcastReceiver> classT) {
        cancelAlarm(context, REQUEST_CODE_BEGIN, classT);
        cancelAlarm(context, REQUEST_CODE_END, classT);
    }

    public static void setAlarm(Context context, long selectTime, boolean status, Class<? extends BroadcastReceiver> classT) {
        LogCat.d("selectTime is " + getTime(selectTime));
        Intent intent = new Intent(context, classT);
        intent.putExtra("status", status);
        PendingIntent sender = PendingIntent.getBroadcast(context, status ? REQUEST_CODE_BEGIN : REQUEST_CODE_END, intent, 0);

        long firstTime = SystemClock.elapsedRealtime(); // 开机之后到现在的运行时间(包括睡眠时间)
        long systemTime = System.currentTimeMillis();
        // 选择的定时时间
        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
            selectTime += AlarmManager.INTERVAL_DAY;
        }
        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        firstTime += time;
        // 进行闹铃注册
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LogCat.d("setExactAndAllowWhileIdle ");
            manager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, sender);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, sender);
            LogCat.d("setExact ");
        } else {
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, AlarmManager.INTERVAL_DAY, sender);
            LogCat.d("setRepeating ");
        }

        LogCat.d("time ==== " + getTime(time) + ", selectTime ===== " + getTime(selectTime) + ", systemTime ==== " + getTime(systemTime) + ", firstTime === " + getTime(firstTime));
    }

    public static long getBeginTimeInDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        morning = morning + BEGIN_TIME;
        return morning;
    }

    public static long getBeginTimeInDay0() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        return morning;
    }

    public static long getBeginTimeInDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        return morning + BEGIN_TIME;
    }

    public static long getEndTimeInDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        return morning + END_TIME;
    }

    public static long getEndTimeInDay(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        return morning + END_TIME;
    }

    public static long getEndTimeInDay0(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long morning = cal.getTimeInMillis();
        return morning + AlarmManager.INTERVAL_DAY - 1000;
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1 = new Date(time);
        return format.format(d1);
    }

    public static void cancelAlarm(Context context, int id, Class<? extends BroadcastReceiver> classT) {
        Intent intent = new Intent(context, classT);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent
                .FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }

    //when是秒
    public static boolean isToday(long when) {
        Time time = new Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    public static int getYear() {
        Time time = new Time();
        time.set(System.currentTimeMillis());
        return time.year;
    }


}
