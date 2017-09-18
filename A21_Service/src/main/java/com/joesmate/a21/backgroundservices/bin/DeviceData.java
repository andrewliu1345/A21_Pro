package com.joesmate.a21.backgroundservices.bin;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.SystemClock;
import android.util.Log;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.sdk.ReaderDev;

import java.lang.reflect.Method;
import java.util.Calendar;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Created by andre on 2017/7/19 .
 */

public class DeviceData {
    static final String TAG = DeviceData.class.toString();

    private DeviceData() {
        ReaderDev.getInstance().BtPowerOn();
    }

    private static final DeviceData mInstance = new DeviceData();

    public static DeviceData getInstance() {
        return mInstance;
    }

    public void SetSysTime(byte[] time) {
        if (time.length < 6)
            return;
        int year = time[2] + 2000;
        int month = time[3];
        int day = time[4];
        int hour = time[5];
        int minute = time[6];
        int second = time[7];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        long when = calendar.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);

        }
    }

    public int getPower() {
        int power;
        BatteryManager batteryManager = (BatteryManager) App.getInstance().getSystemService(BATTERY_SERVICE);
        power = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        Log.d(TAG, String.format("getPower: %d", power));
        return power;
    }

    public String getSerialNumber() throws Exception {
//        Context context = App.getInstance().getApplicationContext();
//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        String serial = tm.getSimSerialNumber();
        String serial = null;

        Class<?> c = Class.forName("android.os.SystemProperties");
        Method get = c.getMethod("get", String.class);
        serial = (String) get.invoke(c, "ro.boot.serialno");

        Log.d(TAG, String.format("getSerialNumber: %s", serial));
        return serial;

    }

    public static String getVersionName() throws Exception {
        Context context = App.getInstance().getApplicationContext();
        String versionName = null;
        PackageManager manager = context.getPackageManager();

        PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
        versionName = packageInfo.packageName + "_V" + packageInfo.versionName;
        Log.d(TAG, String.format("getVersionName:%s ", versionName));
        return versionName;
    }

//    public void OpenBT_GPIO() {
//        GPIO gpio = new GPIO(80, 0);
//        gpio.Up(1);
//    }
}
