package com.droid.ouse;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

//    static private Context context = MyApplication.getContextObject();

    public static void showShort(String message) {
        Toast.makeText(MyApplication.getContextObject(), message, Toast.LENGTH_SHORT).show();
    }
}
