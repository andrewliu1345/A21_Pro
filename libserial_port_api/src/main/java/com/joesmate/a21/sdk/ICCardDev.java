package com.joesmate.a21.sdk;

import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.util.Arrays;

/**
 * Created by andre on 2017/6/7 .
 */

public class ICCardDev {
    static int _fd = 0;

    public ICCardDev(int fd) {
        _fd = fd;
    }

    public String ReadCardNum() {
        String num = "";
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activeic);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活IC卡 并获取RATS
            byte[] in = new byte[4096];
            iRet = libserialport_api.device_read(_fd, in, 1000);
            if (iRet > 0) {
                send = ToolFun.toPackData((byte) 0x60, CMD.readic);
                iRet = libserialport_api.device_write(_fd, send, send.length);
                if (iRet > 0) {//读卡号
                    Arrays.fill(in, (byte) 0);
                    iRet = libserialport_api.device_read(_fd, in, 1000);
                    if (iRet > 0) {
                        for (int i = 0; i < iRet; i++) {
                            in[i] += 0x30;
                        }
                        num = new String(in);
                    }
                }
            }
        }
        return num;
    }

    public String sendAdpu() {
        String str = "";
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activeic);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活IC卡
            byte[] in = new byte[4096];
            int[] rlen = new int[]{0};
            iRet = libserialport_api.device_read(_fd, in, 1000);
            if (iRet > 0) {

                //String strsend = "FF55001F60B200000C4B00140000A404000E325041592E5359532E444446303100FA8688";
                String strsend = "FF55001F60B200000C4B00140000A404000E315041592E5359532E444446303100FA8788";
                String stradpu = "00A404000E315041592E5359532E444446303100";
                send = ToolFun.hexStringToBytes(stradpu); //ToolFun.hexStringToBytes(strsend);//ToolFun.toPackData((byte) 0x60, CMD.readidinfo);
                iRet = libserialport_api.card_ADPU(_fd, send.length, send, rlen, in, 4000);
                //iRet = libserialport_api.device_write(_fd, send, send.length);
                if (iRet ==0) {//ADPU
                    // Arrays.fill(in, (byte) 0);
                    //iRet = libserialport_api.device_read(_fd, in, 4000);
                    //  if (iRet > 0) {
                    str = ToolFun.printHexString(in);
                    //  }


                }
            }

        }

        return str;
    }
}
