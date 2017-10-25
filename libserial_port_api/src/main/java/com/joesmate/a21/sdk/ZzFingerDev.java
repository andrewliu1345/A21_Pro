package com.joesmate.a21.sdk;

import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

/**
 * Created by andre on 2017/10/24 .
 */

public class ZzFingerDev extends FingerDev {
    @Override
    public byte[] regFingerPrint(int outTime) {
        return new byte[0];
    }

    @Override
    public byte[] sampFingerPrint(int outTime) {
        long start = System.currentTimeMillis();
        long sut = 0;
        ReaderDev.getInstance().FpPowerOn();
        setBaud(9600);//设置波特率
        libserialport_api.flush(m_fp_fd);

        byte[] temp = new byte[7];
        byte[] splitdata = new byte[14];
        byte[] fpdata = new byte[16];
        temp[0] = 0;
        temp[1] = 4;
        temp[2] = 0x0c;
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
        return new byte[0];
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
}
