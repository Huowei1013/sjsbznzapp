package com.thirdsdks.recorder;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhaotao on 2021/01/04 20:52.
 */

public class ProgressTextUtils {

    public static String getProgressText(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        double minute = calendar.get(Calendar.MINUTE);
        double second = calendar.get(Calendar.SECOND);

        DecimalFormat format = new DecimalFormat("00");
        return format.format(minute) + ":" + format.format(second);
    }
}
