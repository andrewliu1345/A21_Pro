package com.joesmate.a21.backgroundservices.bin;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.sdk.CMD;
import com.joesmate.a21.sdk.ICCardDev;
import com.joesmate.a21.sdk.IDCardDev;
import com.joesmate.a21.sdk.PBOC;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.util.Arrays;

/**
 * Created by andre on 2017/7/20 .
 */

public class IDCardRead {
    static final String TAG = IDCardRead.class.toString();
    static int m360fd = App.getInstance().m_360fd;

    private IDCardRead() {
    }

    private static final IDCardRead mInstance = new IDCardRead();

    public static IDCardRead getInstance() {
        return mInstance;
    }

    public byte[] ReadBaseMsg() {
//        byte[] tmp = new byte[2049];
//        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
//        int iRet = libserialport_api.device_write(m360fd, send, send.length);
//        if (iRet > 0) {//激活A卡
//
//            iRet = libserialport_api.device_ReadBaseData(m360fd, tmp, 6000);
//            if (iRet > 0) {
//                int len = iRet;
//                byte[] return_data = new byte[len];
//                Arrays.fill(return_data, (byte) 0x00);
//                System.arraycopy(tmp, 0, return_data, 0, iRet);
//                App.getInstance().tts.speak("读卡成功");
//                return return_data;
//            } else {
//                App.getInstance().tts.speak("读卡失败");
//                byte[] return_data = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x80};
//                return return_data;
//            }
//
//        }
//        App.getInstance().tts.speak("读卡失败");
        IDCardDev id = new IDCardDev(m360fd, App.getInstance().tts);
        return id.ReadBaseMsg();
    }

    public byte[] ReadBaseMsgFp() {
//        byte[] tmp = new byte[2049];
//        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
//        int iRet = libserialport_api.device_write(m360fd, send, send.length);
//        if (iRet > 0) {//激活A卡
//
//            iRet = libserialport_api.device_ReadBaseDataFp(m360fd, tmp, 2000);
//            if (iRet > 0) {
//                int len = iRet + 2;
//                byte[] return_data = new byte[len];
//                Arrays.fill(return_data, (byte) 0x00);
//                System.arraycopy(tmp, 0, return_data, 2, iRet);
//                return return_data;
//            } else {
//                byte[] return_data = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x80};
//                return return_data;
//            }
//
//        }
        IDCardDev id = new IDCardDev(m360fd, App.getInstance().tts);
        return id.ReadBaseMsgFp();
    }

    public byte[] GetICCInfo(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;
        byte[] ictype = new byte[len];
        System.arraycopy(data, ++pos, ictype, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] aidList = new byte[len];
        System.arraycopy(data, ++pos, aidList, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] tagList = new byte[len];
        System.arraycopy(data, ++pos, tagList, 0, len);


        pos += len;
        len = data[pos] & 0xff;
        byte[] timeout = new byte[len];
        System.arraycopy(data, ++pos, timeout, 0, len);

        String[] str = PBOC.getInstance().GetICCInfo(m360fd, ictype[0], new String(aidList), new String(tagList), timeout[0] & 0xff);
        byte[] arrRet = null;
        if (str != null)
            arrRet = String.format("%s|%s", str[0], str[1]).getBytes();
        return arrRet;
    }

    public byte[] GetARQC(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;
        byte[] ictype = new byte[len];
        System.arraycopy(data, ++pos, ictype, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] txtdata = new byte[len];
        System.arraycopy(data, ++pos, txtdata, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] aidList = new byte[len];
        System.arraycopy(data, ++pos, aidList, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] timeout = new byte[len];
        System.arraycopy(data, ++pos, timeout, 0, len);

        String[] str = PBOC.getInstance().GetICCArqc(m360fd, ictype[0], new String(txtdata), new String(aidList), timeout[0] & 0xff);
        byte[] arrRet = null;
        if (str != null)
            arrRet = String.format("%s|%s", str[0], str[1]).getBytes();
        return arrRet;
    }

    public byte[] ARPCExeScript(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;
        byte[] ictype = new byte[len];
        System.arraycopy(data, ++pos, ictype, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] txtdata = new byte[len];
        System.arraycopy(data, ++pos, txtdata, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] arpc = new byte[len];
        System.arraycopy(data, ++pos, arpc, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] CDol2 = new byte[len];
        System.arraycopy(data, ++pos, CDol2, 0, len);

        String[] str = PBOC.getInstance().ARPCExeScript(m360fd, ictype[0], new String(txtdata), new String(arpc), new String(CDol2));
        byte[] arrRet = null;
        if (str != null)
            arrRet = String.format("%s|%s|%s", str[0], str[1], str[3]).getBytes();
        return arrRet;
    }

    public byte[] GetTrDetail(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;
        byte[] ictype = new byte[len];
        System.arraycopy(data, ++pos, ictype, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] NOLog = new byte[len];
        System.arraycopy(data, ++pos, NOLog, 0, len);


        pos += len;
        len = data[pos] & 0xff;
        byte[] timeout = new byte[len];
        System.arraycopy(data, ++pos, timeout, 0, len);

        String[] str = PBOC.getInstance().GetTrDetail(m360fd, ictype[0], NOLog[0] & 0xff, timeout[0] & 0xff);
        byte[] arrRet = null;
        if (str != null)
            arrRet = String.format("%s|%s", str[0], str[1]).getBytes();
        return arrRet;
    }

    public byte[] GetLoadLog(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;
        byte[] ictype = new byte[len];
        System.arraycopy(data, ++pos, ictype, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] NOLog = new byte[len];
        System.arraycopy(data, ++pos, NOLog, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] aidList = new byte[len];
        System.arraycopy(data, ++pos, aidList, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] timeout = new byte[len];
        System.arraycopy(data, ++pos, timeout, 0, len);

        String[] str = PBOC.getInstance().GetLoadLog(m360fd, ictype[0], NOLog[0] & 0xff, new String(aidList), timeout[0] & 0xff);
        byte[] arrRet = null;
        if (str != null)
            arrRet = String.format("%s|%s", str[0], str[1]).getBytes();
        return arrRet;
    }

    public byte[] GetICAndARQCInfo(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;
        byte[] ictype = new byte[len];
        System.arraycopy(data, ++pos, ictype, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] aidList = new byte[len];
        System.arraycopy(data, ++pos, aidList, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] tagList = new byte[len];
        System.arraycopy(data, ++pos, tagList, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] txtdata = new byte[len];
        System.arraycopy(data, ++pos, txtdata, 0, len);

        pos += len;
        len = data[pos] & 0xff;
        byte[] timeout = new byte[len];
        System.arraycopy(data, ++pos, timeout, 0, len);

        String[] str = PBOC.getInstance().GetICAndARQCInfo(m360fd, ictype[0], new String(aidList), new String(tagList), new String(txtdata), timeout[0] & 0xff);
        byte[] arrRet = null;
        if (str != null)
            arrRet = String.format("%s|%s|%s", str[0], str[1], str[3]).getBytes();
        return arrRet;
    }
}
