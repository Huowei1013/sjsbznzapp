package com.thirdsdks.filedeal;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by zhaotao on 2020/12/30 19:04.
 */

public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static void showToast(Context context, String content, boolean rightNow) {
        if (rightNow) {
            Toast.makeText(context, content, Toast.LENGTH_SHORT).show(); //新弹出一个，避免后面的覆盖前面的
        } else {
            showToast(context, content);
        }
    }
}
