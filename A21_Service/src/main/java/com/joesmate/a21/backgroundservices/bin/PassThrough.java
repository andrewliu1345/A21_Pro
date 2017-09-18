package com.joesmate.a21.backgroundservices.bin;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;

import java.util.Arrays;

/**
 * Created by andre on 2017/7/20 .
 */

public class PassThrough {
    private PassThrough() {
    }

    private static int m_fpfd = App.getInstance().m_fpfd;
    private static boolean havadata = false;
    private static int cache = 2048;
    private static byte[] buffer = new byte[cache];

    private static final PassThrough mInstance = new PassThrough();

    public static PassThrough getInstance() {
        return mInstance;
    }

    public int SendCMD(byte[] cmd) {
        return libserialport_api.device_write(m_fpfd, cmd, cmd.length);
    }

    public byte[] getBuffer(int[] reCode) {
        Arrays.fill(buffer, (byte) 0x00);
        int iRet = libserialport_api.device_read_all(m_fpfd, buffer);
        reCode[0] = iRet;
        if (iRet <= 0)
            return new byte[]{(byte) 0x30, (byte) 0x11};
        byte[] buff = new byte[iRet];
        System.arraycopy(buffer, 0, buff, 0, iRet);
        return buff;
    }

    public int setBaud(int baud) {
        int b = 9600;
        switch (baud) {
            case 0:
                b = 9600;
                break;
            case 1:
                b = 19200;
                break;
            case 2:
                b = 38400;
                break;
            case 3:
                b = 57600;
                break;
            case 4:
                b = 115200;
                break;
        }
        return libserialport_api.device_set_baud(m_fpfd, b);
    }
}
