package com.joesmate.a21.backgroundservices;

import android.app.Service;
import android.content.Intent;

import android.os.IBinder;
import android.util.Log;

import com.joesmate.a21.backgroundservices.bin.DataProcessingRunnable;
import com.joesmate.a21.backgroundservices.bin.DeviceData;
import com.joesmate.a21.backgroundservices.bin.IDCardRead;
import com.joesmate.a21.backgroundservices.bin.Keyboard;
import com.joesmate.a21.backgroundservices.bin.MagneticCard;
import com.joesmate.a21.backgroundservices.bin.PassThrough;
import com.joesmate.a21.backgroundservices.bin.Signature;
import com.joesmate.a21.sdk.ReaderDev;
import com.joesmate.a21.sdk.WlFingerDev;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DataProcessingService extends Service {
    static final String TAG = DataProcessingService.class.toString();
    static final ExecutorService DataProcessingThreadPool = Executors.newSingleThreadExecutor();

    public DataProcessingService() {
    }

    int m_fd = -1;
    App mApp = App.getInstance();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "DataProcessingService.onStartCommand");
        if (intent == null)
            return super.onStartCommand(intent, flags, startId);
        m_fd = App.getInstance().m_btfd;
        byte[] in = intent.getByteArrayExtra(AppTag.TAG_BT_IN_DATA);
        if (in == null || in.length < 7) {
            sendErr();
            return super.onStartCommand(intent, flags, startId);
        }
        String strRev = ToolFun.printHexString(in);
        Log.d(TAG, String.format("ReceiveIn=%s", strRev));
        Runnable dataProcessing = new DataProcessingRunnable(in, m_fd);
        DataProcessingThreadPool.execute(dataProcessing);



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "DataProcessingService.onDestroy");
        super.onDestroy();
    }

    private void SendData(byte[] writedata, int writeLen) {
        int len = writeLen;
        byte[] Writebuffer = new byte[len + 5];
        Writebuffer[0] = 2;
        Writebuffer[1] = (byte) (len >> 8);
        Writebuffer[2] = (byte) (len % 256);
        System.arraycopy(writedata, 0, Writebuffer, 3, writeLen);
        Writebuffer[3 + writeLen] = cr_bcc(writedata);
        Writebuffer[4 + writeLen] = 3;
        String sendStr = "";

        for (byte buff : Writebuffer) {
            sendStr = sendStr + String.format("%02X", buff) + " ";
        }

        Log.d(TAG, String.format("Return Send: SendData=%s", sendStr));
        libserialport_api.device_bt_write(m_fd, Writebuffer, Writebuffer.length);
    }

    private void SendReturnData(byte[] writedata, int writeLen) {
        int len = writeLen + 2;
        byte[] Writebuffer = new byte[len + 5];
        Writebuffer[0] = 2;
        Writebuffer[1] = (byte) (len >> 8);
        Writebuffer[2] = (byte) (len % 256);
        Writebuffer[3] = 0;
        Writebuffer[4] = 0;
        System.arraycopy(writedata, 0, Writebuffer, 5, writeLen);
        Writebuffer[5 + writeLen] = cr_bcc(writedata);
        Writebuffer[6 + writeLen] = 3;
//        String sendStr = "";
//
//        for (byte buff : Writebuffer) {
//            sendStr = sendStr + String.format("%02X", buff) + " ";
//        }
//
//        Log.d(TAG, String.format("Return Send :len=%d CRC=%02X SendReturnData=%s", len + 5, Writebuffer[5 + writeLen], sendStr));
        libserialport_api.device_bt_write(m_fd, Writebuffer, Writebuffer.length);
    }


    private byte cr_bcc(byte[] data) {
        byte temp = 0;
        for (byte item :
                data) {
            temp ^= item;
        }
        return temp;
    }

    private byte[] getData(byte[] src) {
        if (src[0] != 0x02)
            return null;
        int len = (src[1] & 0xff << 8) + src[2] & 0xff;
        if (src[len + 4] != (byte) 0x03)
            return null;
        byte[] dest = new byte[len];
        System.arraycopy(src, 3, dest, 0, len);
        byte crc = cr_bcc(dest);
        if (src[len + 3] != crc)
            return null;
        return dest;
    }


    static final byte[] err = {(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x03};
    static final byte[] ok = {(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03};

    private void sendErr() {
        libserialport_api.device_write(m_fd, err, err.length);
    }

    private void sendOK() {
        libserialport_api.device_write(m_fd, ok, ok.length);
    }
}
