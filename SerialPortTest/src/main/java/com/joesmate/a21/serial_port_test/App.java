package com.joesmate.a21.serial_port_test;

import android.app.Application;

/**
 * Created by andre on 2017/8/3 .
 */

public class App extends Application {
    private static final String TAG = "com.joesmate.a21.backgroundservices.App";
    private static App mApp;
    //360描述符
    public int m_360fd = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static App getInstance() {
        return mApp;
    }
}
