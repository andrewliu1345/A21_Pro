package com.joesmate.a21.sdk;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.LogMg;
import com.joesmate.sdk.util.ToolFun;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by andre on 2017/9/6 .
 */

public class WlForUsbFingerDev extends FingerDev {

    private static final String TAG = WlForUsbFingerDev.class.toString();
    Context m_context = null;
    private UsbManager mManager;
    private UsbDeviceConnection mDeviceConnection;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public WlForUsbFingerDev(Context context) {
        m_context = context;
        int iret = FindAndOpenFpDev();
        if (iret != 0)
            Log.e(TAG, "FindUSBdevice {" + m_fp_fd + "} fail");
        else
            Log.d(TAG, "FindUSBdevice {" + m_fp_fd + "} ok");
    }

    // 查找指纹设备
    public int FindAndOpenFpDev() {
        Log.i(TAG, "FindAndOpenFpDev");
        this.mManager = ((UsbManager) m_context
                .getSystemService(m_context.USB_SERVICE));
        if (this.mManager == null) {
            Log.i(TAG, "--->getSystemService failed ");
            Log.i(TAG, "FindAndOpenFpDev End");
            return -1;
        }

        Log.i(TAG, "--->getSystemService OK");
        HashMap<String, UsbDevice> deviceList = mManager.getDeviceList();
        if (deviceList != null && deviceList.size() != 0) {
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            PendingIntent mPermissionIntent = PendingIntent.getBroadcast(
                    m_context, 0, new Intent(ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            filter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
            filter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            m_context.registerReceiver(this.mUsbReceiver, filter);

            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                Log.i(TAG, "--->device: VID = " + device.getVendorId() + "PID = " + device.getProductId());
                if ((device.getVendorId() == 0x2796 && device.getProductId() == 0x0998) || (device.getVendorId() == 0x2796 && device.getProductId() == 0x8195)
                        || (device.getVendorId() == 0x7750 && device.getProductId() == 0x8195)) {
                    Log.i(TAG, "--->get fpdev ok");
                    if (!mManager.hasPermission(device)) {
                        mManager.requestPermission(device, mPermissionIntent);
                        Log.i(TAG, "--->mManager.requestPermission");
                    }
                    mDeviceConnection = mManager.openDevice(device);

                    if (mDeviceConnection != null) {
                        m_fp_fd = mDeviceConnection.getFileDescriptor();
                        Log.i(TAG, "--->getFileDescriptor:" + String.valueOf(m_fp_fd));

                        Log.i(TAG, "FindAndOpenFpDev End");
                        return 0;
                    }
                }

            }
        } else {
            Log.i(TAG, "--->Not Find Finger Device");
            Log.i(TAG, "FindAndOpenFpDev End");
            return -1;
        }

        Log.i(TAG, "FindAndOpenFpDev End");
        return -1;

    }

    BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "BroadcastReceiver");
            Log.i(TAG, "--->mUsbReceiver" + m_fp_fd);
            Log.i(TAG, "--->mUsbReceiver" + intent.getAction());
            if (intent.getAction().equals(ACTION_USB_PERMISSION) || intent.getAction().equals("USB_DEVICE_ATTACHED")) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,
                        false)) {
                    UsbDevice dev = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    mDeviceConnection = mManager.openDevice(dev);

                    if (mDeviceConnection != null) {
                        m_fp_fd = mDeviceConnection.getFileDescriptor();
                        Log.i(TAG, "mUsbReceiver" + m_fp_fd);
                    }
                    Log.i(TAG, "mUsbReceiver");
                }
            }
        }
    };

    @Override
    public byte[] regFingerPrint(int outTime) {
        return new byte[0];
    }

    @Override
    public byte[] sampFingerPrint(int outTime) {
        long start = System.currentTimeMillis();
        long sut = 0;
        ReaderDev.getInstance().FpPowerOn();
       // setBaud(9600);//设置波特率
        libserialport_api.flush(m_fp_fd);

        byte[] temp = new byte[7];
        byte[] splitdata = new byte[14];
        byte[] fpdata = new byte[64];
       int i = 0;
        temp[i++] = 0;
        temp[i++] = 4;
        temp[i++] = 0x01;
        temp[i++] = 0;//(byte) (outTime & 0xff);
        temp[i++] = 0;
        temp[i++] = 0;
        temp[i++] = (byte) ToolFun.cr_bcc(6, temp);
       // ToolFun.splitFun(temp, (byte) 7, splitdata, (byte) 14);
        fpdata[0] = 2;
        System.arraycopy(temp, 0, fpdata, 1, 7);
        fpdata[8] = 3;

        int iRet = libserialport_api.device_write(m_fp_fd, fpdata, fpdata.length);//发送指令
        if (iRet <= 0) {
            return new byte[0];
        }
        byte[] in = new byte[2048];
        sut = System.currentTimeMillis() - start;
        while (sut < outTime) {
            int dRead = libserialport_api.device_read_all(m_fp_fd, in);
            if (dRead > 0) {
                byte[] tmp = parseWelFingerData(in);
                if (tmp == null)
                    return new byte[0];
                int len = ((tmp[0] & 0xff) << 8) + (tmp[1] & 0xff);
                if (tmp[2] != 0)
                    return null;
                byte[] fp_data = new byte[len - 2];
                System.arraycopy(tmp, 4, fp_data, 0, len - 2);
                return fp_data;
            }
            sut = System.currentTimeMillis() - start;
        }
        return new byte[0];
    }

    @Override
    public byte[] imgFingerPrint(int outTime) {
        ReaderDev.getInstance().FpPowerOn();
        // setBaud(9600);//设置波特率
        libserialport_api.flush(m_fp_fd);
        long start = System.currentTimeMillis();
        int[] w_and_l = getImage(outTime);

        if (w_and_l[0] != 0 && w_and_l[1] != 0) {
            int width = w_and_l[0];
            int heigth = w_and_l[1];
            LogMg.d(TAG, "weigth=%d  ,length=%d", width, heigth);
            //setBaud(115200);
            outTime -= (System.currentTimeMillis() - start);
            byte[] image = getUploadImage(outTime);
            // setBaud(9600);
            ReaderDev.getInstance().FpRest();
            byte[] pBmp = new byte[1024 * 260];
            libserialport_api.Raw2Bmp(image, image.length, pBmp, width, heigth);
            return pBmp;
        }
        //setBaud(9600);
        ReaderDev.getInstance().FpRest();
        return new byte[0];
    }


    private int[] getImage(int outTime) {
        long start = System.currentTimeMillis();
        long sut = 0;
        ReaderDev.getInstance().FpPowerOn();
        // setBaud(9600);//设置波特率
        libserialport_api.flush(m_fp_fd);

        byte[] temp = new byte[7];
        byte[] splitdata = new byte[14];
        byte[] fpdata = new byte[16];
        temp[0] = 0;
        temp[1] = 4;
        temp[2] = (byte) 0x18;
        temp[3] = (byte) (outTime & 0xff);
        temp[4] = 0;
        temp[5] = 0;
        temp[6] = (byte) ToolFun.cr_bcc(6, temp);
        ToolFun.splitFun(temp, (byte) 7, splitdata, (byte) 14);
        fpdata[0] = 2;
        System.arraycopy(splitdata, 0, fpdata, 1, 14);
        fpdata[15] = 3;

        int iRet = libserialport_api.device_write(m_fp_fd, fpdata, fpdata.length);//发送指令
        if (iRet < 0) {
            return new int[]{0x00, 0x00};
        }
        byte[] in = new byte[4096];
        sut = System.currentTimeMillis() - start;
        while (sut < outTime) {
            int dRead = libserialport_api.device_read_all(m_fp_fd, in);
            if (dRead > 0) {
                byte[] tmp = parseWelFingerData(in);
                if (tmp == null)
                    return new int[]{0x00, 0x00};
                int len = ((tmp[0] & 0xff) << 8) + (tmp[1] & 0xff);
                byte[] fp_data = new byte[len];
                System.arraycopy(tmp, 2, fp_data, 0, len);
                if (fp_data[0] != 0)
                    return new int[]{0x00, 0x00};
                int width = (fp_data[2] & 0xff << 8) + (fp_data[3] & 0xff);
                int height = (fp_data[4] & 0xff << 8) + (fp_data[5] & 0xff);
                return new int[]{width, height};
            }
            sut = System.currentTimeMillis() - start;
        }
        return new int[]{0x00, 0x00};
    }

    private byte[] getUploadImage(int outTime) {
        byte[] imageBuffer = new byte[1024 * 260];
        long start = System.currentTimeMillis();
        int len = 0;
        int num = 0;
        while (System.currentTimeMillis() - start < outTime) {
            byte[] tmp = getImageByNum(1000, num);
            if (tmp == null || tmp.length < 1024)
                break;
            try {
                LogMg.d(TAG, "tmp=%s", ToolFun.printHexString(tmp));
                LogMg.d(TAG, "tmp.length=%d ,  len=%d", tmp.length, len);
                System.arraycopy(tmp, 0, imageBuffer, len, tmp.length);
                len += tmp.length;
                num++;
            } catch (Exception ex) {
                LogMg.e(TAG, ex.getMessage());
            }

            ToolFun.Dalpey(8);
        }
        byte[] image = new byte[len];
        System.arraycopy(imageBuffer, 0, image, 0, len);
        return image;
    }

    private byte[] getImageByNum(int timeout, int num) {
        long start = System.currentTimeMillis();
        byte[] temp = new byte[9];
        byte[] splitdata = new byte[18];
        byte[] writeBuf = new byte[20];
        temp[0] = 0;
        temp[1] = 6;
        temp[2] = (byte) 0x19;
        temp[3] = 0;
        temp[4] = 0;
        temp[5] = 0;
        temp[6] = (byte) (num >> 8);
        temp[7] = (byte) (num & 0x0f);
        temp[8] = (byte) ToolFun.cr_bcc(8, temp);
        ToolFun.splitFun(temp, (byte) 9, splitdata, (byte) 18);
        writeBuf[0] = 2;
        System.arraycopy(splitdata, 0, writeBuf, 1, 18);
        writeBuf[19] = 3;
        int st = libserialport_api.device_write(m_fp_fd, writeBuf, writeBuf.length);
        if (st > 0) {
            byte[] in = new byte[4096];
            long sut = System.currentTimeMillis() - start;
            while (sut < timeout) {
                int dRead = libserialport_api.device_read_all(m_fp_fd, in);
                if (dRead > 0) {
                    byte[] tmp = parseWelFingerData(in);
                    if (tmp == null)
                        return new byte[0];
                    if (tmp[2] != 0)
                        return null;
                    int len = ((tmp[0] & 0xff) << 8) + (tmp[1] & 0xff);
                    byte[] fp_data = new byte[len - 2];
                    System.arraycopy(tmp, 4, fp_data, 0, len - 2);
                    return fp_data;
                }
                ToolFun.Dalpey(8);
                sut = System.currentTimeMillis() - start;
            }

        }
        return null;
    }


    private byte[] parseWelFingerData(byte[] read_data) {
        int len = 0;
        int endpos = 0;
        if (read_data[0] != 0x02)
            return null;
        for (byte item :
                read_data) {
            len++;
            if (item == 0x03)
                break;
            endpos++;
        }
        if (len - 1 != endpos)
            return null;
        byte[] tmp = new byte[len];
        System.arraycopy(read_data, 1, tmp, 0, len - 2);
//        String str = ToolFun.hex_split(tmp, tmp.length);
        return ToolFun.unSplitFun(tmp);
    }

//    private int FindFindger(int time)
//    {
//
//    }
}
