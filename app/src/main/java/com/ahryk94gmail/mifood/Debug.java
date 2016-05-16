package com.ahryk94gmail.mifood;

import android.util.Log;

public final class Debug {
    public static final String TAG = "MiFood";

    public static void WriteLog(boolean flag, String message) {
        if (flag) {
            Log.d(TAG, message);
        }
    }
}
