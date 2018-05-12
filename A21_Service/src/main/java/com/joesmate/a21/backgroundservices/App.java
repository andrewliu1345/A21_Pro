package com.joesmate.a21.backgroundservices;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.util.Log;

import com.jl.pinpad.IRemotePinpad;
import com.joesmate.AndroidTTS.*;
import com.joesmate.BaesTextToSpeech;
import com.joesmate.a21.sdk.ReaderDev;
import com.joesmate.a21.serial_port_api.libserialport_api;
//import com.jollytech.app.Platform;
import java.io.File;
import java.util.List;

/**
 * Created by andre on 2017/7/3 .
 */

public class App extends Application {
    private static final String TAG = "com.joesmate.a21.backgroundservices.App";
    private static App mApp;
    final String m_360path = "/dev/ttyMT1";//360地址
    final String m_fppath = "/dev/ttyMT2";//指纹地址
    final String m_btpath = "/dev/ttyMT3";//蓝牙地址

    public IRemotePinpad m_pinpad;

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
        connectPinpad();
    }

    void connectPinpad() {

        Intent service = new Intent("com.remote.service.PINPAD");
        Intent eintent = new Intent(getExplicitIntent(mApp.getApplicationContext(), service));
        bindService(eintent, connection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // TODO Auto-generated method stub
//            Tools.Loginfo(getClass().getName(), "pinpad disconnected!");
            m_pinpad = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            // TODO Auto-generated method stub
//            Tools.Loginfo(getClass().getName(), "pinpad connected!!!!");
            m_pinpad = IRemotePinpad.Stub.asInterface(arg1);

        }
    };

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

    public static Intent getExplicitIntent(Context context,
                                           Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent,
                0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}
