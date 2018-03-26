package com.joesmate.USB;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by andre on 2017/11/14 .
 */

public class UsbApi {
    private static final String TAG = UsbApi.class.toString();
    Context m_context = null;
    private UsbManager mManager;
    private UsbDeviceConnection mDeviceConnection;
    private UsbInterface intf;
    private UsbEndpoint mEndpointOut;
    private UsbEndpoint mEndpointIn;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    public UsbApi(int pid, int vid, Context context) {
        m_context = context;
        int iret = FindAndOpenFpDev(pid, vid);
        if (iret != 0)
            Log.e(TAG, "FindUSBdevice  fail");
        else
            Log.d(TAG, "FindUSBdevice  ok");
    }

    // 查找USB设备
    private int FindAndOpenFpDev(int pid, int vid) {
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
            Log.i(TAG, "--->device: VID = " + vid + "PID = " + pid);
//            for (UsbDevice device :
//                    deviceIterator) {
//
//            }
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                Log.i(TAG, "--->device: VID = " + device.getVendorId() + "PID = " + device.getProductId());
                if ((device.getVendorId() == vid && device.getProductId() == pid)) {
                    Log.i(TAG, "--->get fpdev ok");
                    if (!mManager.hasPermission(device)) {
                        mManager.requestPermission(device, mPermissionIntent);
                        Log.i(TAG, "--->mManager.requestPermission");
                    }
                    mDeviceConnection = mManager.openDevice(device);

                    if (mDeviceConnection != null) {

                        UsbInterface usbInterface = findusbnterface(device);
                        if (mDeviceConnection.claimInterface(usbInterface, true)) {

                        } else {
                            mDeviceConnection.close();
                        }
                        getusbInterface(usbInterface);
//                        m_fp_fd = mDeviceConnection.getFileDescriptor();
//                        Log.i(TAG, "--->getFileDescriptor:" + String.valueOf(m_fp_fd));

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
//            Log.i(TAG, "--->mUsbReceiver" + m_fp_fd);
            Log.i(TAG, "--->mUsbReceiver" + intent.getAction());
            if (intent.getAction().equals(ACTION_USB_PERMISSION) || intent.getAction().equals("USB_DEVICE_ATTACHED")) {
                if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,
                        false)) {
                    UsbDevice dev = (UsbDevice) intent
                            .getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    mDeviceConnection = mManager.openDevice(dev);

                    if (mDeviceConnection != null) {

                        UsbInterface usbInterface = findusbnterface(dev);
                        getusbInterface(usbInterface);
                        // m_fp_fd = mDeviceConnection.getFileDescriptor();
                        //Log.i(TAG, "mUsbReceiver" + m_fp_fd);
                        // return 0;
                    }
                    Log.i(TAG, "mUsbReceiver");
                }
            }
        }
    };

    private UsbInterface findusbnterface(UsbDevice device) {
        int count = device.getInterfaceCount();
        UsbInterface intf = null;

        for (int i = 0; i < count; i++) {
            intf = device.getInterface(i);

            if ((intf.getInterfaceClass() == 8)
                    && (intf.getInterfaceSubclass() == 6)
                    && (intf.getInterfaceProtocol() == 80)) {
                return intf;
            }
        }

        return null;
    }

    void getusbInterface(UsbInterface intf) {
        UsbEndpoint epOut = null;
        UsbEndpoint epIn = null;
        for (int i = 0; i < intf.getEndpointCount(); i++) {
            UsbEndpoint ep = intf.getEndpoint(i);

            if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                epOut = ep;
                Log.d("输出端点", "index:"+i+" "+"端点号"+ep.getEndpointNumber());
            } else if(ep.getDirection()==UsbConstants.USB_DIR_IN){
                epIn = ep;
                Log.d("输入端点", "index:"+i+" "+"端点号"+ep.getEndpointNumber()+" 包大小"+ep.getMaxPacketSize());
            }
        }
        if ((epOut == null) || (epIn == null)) {

            return;
        }
        this.mEndpointOut = epOut;
        this.mEndpointIn = epIn;
    }


    public int write(byte[] data, int timeout2, int lenth) {
        return this.mDeviceConnection.bulkTransfer(this.mEndpointOut,
                data, lenth, timeout2 * 1000);

    }

    public int read(byte[] data, int timeout2, int lenth) {
        return this.mDeviceConnection.bulkTransfer(this.mEndpointIn,
                data, lenth, timeout2 * 1000);

    }
}
