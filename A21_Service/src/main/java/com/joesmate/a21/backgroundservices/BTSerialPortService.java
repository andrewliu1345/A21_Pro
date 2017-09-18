package com.joesmate.a21.backgroundservices;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.joesmate.a21.backgroundservices.bin.DeviceData;
import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.sdk.ReaderDev;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.util.Arrays;


public class BTSerialPortService extends Service {
    private static final String TAG = BTSerialPortService.class.getSimpleName();
    private boolean isClose = false;

    public BTSerialPortService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Server.onCreate");
        DeviceData.getInstance();
        m_btfd = App.getInstance().m_btfd;
        isClose = false;
        if (m_btfd < 0)
            return;
        mReadSerialPort.start();
    }

    int m_btfd = -1;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "Server.onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Server.onDestroy");
        isClose = true;
        super.onDestroy();
    }

    Thread mReadSerialPort = new Thread() {
        @Override
        public void run() {
            Intent m_intent = new Intent(AppAction.ACTION_BT_DATA);
            byte[] in = new byte[2048];
            byte[] tmp = new byte[4096];
            while (true) {
                if (isClose) {
                    ReaderDev.getInstance().FpPowerOff();
                    return;
                }
                Arrays.fill(in, (byte) 0x00);
                Arrays.fill(tmp, (byte) 0x00);
                int tmplen = 0;

                int iRet = libserialport_api.device_read_all(m_btfd, in);
                ToolFun.Dalpey(8);
                if (iRet > 0 && in[0] == 0x02) {
                    tmplen = iRet;
                    int len = (in[1] & 0xff << 8) + (in[2] & 0xff) + 5;
                    System.arraycopy(in, 0, tmp, 0, iRet);
                    if (tmp[len - 1] != (byte) 0x03 || len > iRet) {
                        for (int count = 0; count < 10; count++) {
                            Arrays.fill(in, (byte) 0x00);
                            iRet = libserialport_api.device_read_all(m_btfd, in);
                            System.arraycopy(in, 0, tmp, tmplen, iRet);
                            tmplen += iRet;
                            if (tmp[len - 1] == (byte) 0x03 && len <= tmplen) {
                                break;
                            }
                        }
                    }

                    m_intent.putExtra(AppTag.TAG_BT_IN_DATA, tmp);
                    App.getInstance().sendBroadcast(m_intent);

                    Log.d(TAG, ToolFun.printHexString(in));
                    Log.d(TAG, new String(in).replace("\0", ""));
                }

                ToolFun.Dalpey(16);
            }
        }
    };

}
