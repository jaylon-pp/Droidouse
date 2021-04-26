package com.droid.ouse.utils;

import android.util.Log;

public class LogUtils {

    private static String TAG = "Droidouse";

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String msg, Throwable tr) {
        Log.e(TAG, msg, tr);
    }

    public static void v(String format, Object... args) {
        String msg = String.format(format, args);
        v(msg);
    }

    public static void d(String format, Object... args) {
        String msg = String.format(format, args);
        d(msg);
    }

    public static void i(String format, Object... args) {
        String msg = String.format(format, args);
        i(msg);
    }

    public static void w(String format, Object... args) {
        String msg = String.format(format, args);
        w(msg);
    }

    public static void e(String format, Object... args) {
        String msg = String.format(format, args);
        e(msg);
    }
}
