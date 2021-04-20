package com.thirdsdks.videoplay;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaotao on 2021/01/05 11:30.
 */

public class StringUtil {

    public static String notNull(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        } else {
            return str;
        }
    }

    public static String notNullAndNullStr(String str, String unit) {
        if (TextUtils.isEmpty(str) || TextUtils.equals(str, "null")) {
            return "";
        } else {
            return str + unit;
        }
    }

    public static String notNullAndNullStr(String str) {
        if (TextUtils.isEmpty(str) || TextUtils.equals(str, "null")) {
            return "";
        } else {
            return str;
        }
    }

    public static boolean isNull(String str) {
        return TextUtils.isEmpty(str) || TextUtils.equals(str, "null");
    }

    /**
     * 判断是否是纯数字
     *
     * @param str
     * @return
     */
    public static boolean isNumRic(String str) {
        return str.matches("[0-9]*");
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time    需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern 输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *                <p/>
     *                如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String time, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            try {
                String mTime;
                if (isNumRic(time)) {
                    SimpleDateFormat format = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    mTime = format.format(Long.parseLong(time));
                } else {
                    if (!TextUtils.isEmpty(time) && time.contains(".")) {
                        String[] timeSplit = time.split("\\.");
                        mTime = timeSplit[0];
                    } else {
                        mTime = time;
                    }
                }
                Date tDate = new SimpleDateFormat(pattern).parse(mTime);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM-dd HH:mm");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(tDate);
                    } else {
                        if (dTime < tMin) {
                            display = "刚刚";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "分钟前";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "小时前";
                        } else if (tDate.after(beforeYes)
                                && tDate.before(yesterday)) {
                            display = "昨天" + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = halfDf.format(tDate);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return time;
            }
        }
        return display;
    }

    public static String getCurrentDateStr() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return formatter.format(new Date());
    }
}
