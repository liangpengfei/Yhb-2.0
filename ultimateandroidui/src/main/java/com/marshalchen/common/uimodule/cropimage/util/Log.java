package com.marshalchen.common.uimodule.cropimage.util;

public class Log {

    private static final String TAG = "android-crop";

    public static final void e(String msg, String filename) {
        android.util.Log.e(TAG, msg);
    }

    public static final void e(String msg, Throwable e) {
        android.util.Log.e(TAG, msg, e);
    }

}
