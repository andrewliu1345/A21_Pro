package com.joesmate.a21.backgroundservices.bin;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.sdk.CMD;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.LogMg;
import com.joesmate.sdk.util.ToolFun;

/**
 * Created by andre on 2017/7/21 .
 */

public class MagneticCard {
    private static final String TAG = MagneticCard.class.toString();
    private static MagneticCard mInstance = new MagneticCard();

    private MagneticCard() {

    }

    public static MagneticCard getInstance() {
        return mInstance;
    }

    int m360fd = App.getInstance().m_360fd;


    public int SlotCard() {
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.readmc);
        return libserialport_api.device_write(m360fd, send, send.length);
    }

    public byte[] ReadData(int[] retcode) {
        byte[] in = new byte[4096];
        int iRet = libserialport_api.device_read(m360fd, in, 300);
        retcode[0] = iRet;
        if (iRet < 0)
            return new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x02, (byte) 0x03};
        if (iRet == 0) {
            SlotCard();
            //ToolFun.Dalpey(400);
            return new byte[]{(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x03, (byte) 0x03};
        }

        LogMg.d(TAG, "in=%s", ToolFun.printHexString(in));
        int len1 = 0, len2 = 0, len3 = 0;
        //int l1 = 0, l2 = 0, l3 = 0;


        if (in[0] == (byte) 0xF1) {
            len1 = in[1];
            if (len1 + 3 < iRet)
                len2 = in[len1 + 3];
            if (len1 + len3 + 5 < iRet)
                len3 = in[len1 + len2 + 5];

        } else if (in[0] == (byte) 0xF2) {
            len2 = in[1];
            if (len2 + 3 < iRet)
                len3 = in[len2 + 3];
        } else if (in[0] == (byte) 0xF3) {
            len3 = in[1];
        }

        int len = len1 + len2 + len3 + 3;
        byte[] f1 = new byte[len1];
        byte[] f2 = new byte[len2];
        byte[] f3 = new byte[len3];

        if (len1 > 0) {
            System.arraycopy(in, 2, f1, 0, len1);
            System.arraycopy(in, len1 + 4, f2, 0, len2);
            System.arraycopy(in, len1 + len2 + 6, f3, 0, len3);
        } else if (len2 > 0) {
            System.arraycopy(in, 2, f2, 0, len2);
            System.arraycopy(in, len2 + 4, f3, 0, len3);
        } else if (len3 > 0) {
            System.arraycopy(in, 2, f3, 0, len3);
        }
        byte[] buffer = new byte[len];

        buffer[0] = (byte) len1;
        buffer[1] = (byte) len2;
        buffer[2] = (byte) len3;

        System.arraycopy(f1, 0, buffer, 3, len1);
        System.arraycopy(f2, 0, buffer, 3 + len1, len2);
        System.arraycopy(f3, 0, buffer, 3 + len1 + len2, len3);
        LogMg.d(TAG, "len1=%d,len2=%d,len3=%d buffer=%s", len1, len2, len3, ToolFun.printHexString(buffer));
        return buffer;
    }
}
