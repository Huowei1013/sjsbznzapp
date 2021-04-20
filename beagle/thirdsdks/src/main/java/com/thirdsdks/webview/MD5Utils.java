package com.thirdsdks.webview;

import java.security.MessageDigest;

/**
 * Created by zhaotao on 2020/12/20 21:02.
 */

public class MD5Utils {
    public static String md5(String passwd) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(passwd.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                int i = b & 0xff;
                String hexString = Integer.toHexString(i);
                if (hexString.length() < 2) {
                    hexString = "0" + hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
