package com.joesmate.general.utils;

import android.util.Log;

/**
 * Created by andre on 2017/7/11 .
 */

public class tools {
    static {
        System.loadLibrary("general");
    }

    static final String TAG = "com.joesmate.general.utils.tools";

    public static native int asc_hex(byte[] asc, byte[] hex, int asclen);

    public static native String hex_split(byte[] hex, int blen);

    public static native byte[] hexStringToBytes(String hexString);

    public static void Dalpey(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception ex) {
            Log.e(TAG, "Daley: ", ex);
        }
    }
}
