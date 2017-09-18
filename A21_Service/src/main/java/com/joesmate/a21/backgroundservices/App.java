package com.joesmate.a21.backgroundservices;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.joesmate.AndroidTTS.*;
import com.joesmate.BaesTextToSpeech;
import com.joesmate.a21.backgroundservices.bin.DeviceData;
import com.joesmate.a21.sdk.ReaderDev;
import com.joesmate.a21.serial_port_api.libserialport_api;

import java.io.File;

/**
 * Created by andre on 2017/7/3 .
 */

public class App extends Application {
    private static final String TAG = "com.joesmate.a21.backgroundservices.App";
    private static App mApp;
    final String m_360path = "/dev/ttyMT1";//360地址
    final String m_fppath = "/dev/ttyMT2";//指纹地址
    final String m_btpath = "/dev/ttyMT3";//蓝牙地址

    public BaesTextToSpeech tts;
    //360描述符
    public int m_360fd = -1;
    //指纹描述符
    public int m_fpfd = -1;
    //蓝牙描述符
    public int m_btfd = -1;

    final File m_360file = new File(m_360path);
    final File m_fpfile = new File(m_fppath);
    final File m_btfile = new File(m_btpath);
//    private static int BT_Fd = -1;
//    private static int m360_Fd = -1;
//    private static int Fp_Fd = -1;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "<====>App_onCreate<====>");
        mApp = this;
        tts = new TextToSpeechEx(this.getApplicationContext());
        ReaderDev.getInstance().FpPowerOn();
        m_btfd = libserialport_api.device_open(m_btpath, 115200);
        m_360fd = libserialport_api.device_open(m_360path, 115200);
        m_fpfd = libserialport_api.device_open(m_fppath, 9600);
    }

    public static App getInstance() {
        return mApp;
    }

    public interface OnReturnListen

    {
        public void onSuess(Intent intent);

        public void onErr(int code);
    }

    private OnReturnListen mlisten = null;

    public void setOnReturnListen(OnReturnListen listen) {
        mlisten = listen;
    }

    public OnReturnListen getOnReturnListen() {
        if (mlisten != null)
            return mlisten;
        return null;
    }
}
