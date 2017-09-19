package com.joesmate.a21.sdk;

import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.util.Arrays;

/**
 * Created by andre on 2017/6/8 .
 */

public class PICCardDev {
    int _fd = -1;

    public PICCardDev(int fd) {
        _fd = fd;
    }

    GPIO gpio4 = new GPIO(4, 0);

    public String ReadCardNum() {
        String num = "";
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
        gpio4.Down(1);
        libserialport_api.RF_Control(_fd, (byte) 3);
        ToolFun.Dalpey(400);
        //ToolFun.Dalpey(1000);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活IC卡
            byte[] in = new byte[4096];
            iRet = libserialport_api.device_read(_fd, in, 1000);
            if (iRet > 0) {
//                send = ToolFun.toPackData((byte) 0x60, CMD.getRats);
//                iRet = libserialport_api.device_write(_fd, send, send.length);
//                if (iRet > 0) {
//                    iRet = libserialport_api.device_read(_fd, in, 1000);
//                    if (iRet > 0) {
                send = ToolFun.toPackData((byte) 0x60, CMD.readnfc);
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
//                        }
//                    }
                }
            }
        }
        // ToolFun.Dalpey(200);
        libserialport_api.RF_Control(_fd, (byte) 0x00);
       // gpio4.Up(1);
        return num;
    }

    public String sendAdpu() {

        String str = "";
        gpio4.Down(1);
        libserialport_api.RF_Control(_fd, (byte) 3);
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活IC卡
            byte[] in = new byte[4096];
            int[] rlen = new int[]{0};
            iRet = libserialport_api.device_read(_fd, in, 1000);
            if (iRet > 0) {

                //String strsend = "FF55001F60B200000C4B00140000A404000E325041592E5359532E444446303100FA8688";
                String stradpu = "00A404000E325041592E5359532E444446303100";
                // FF55001F60B200320C4B00140000A404000E325041592E5359532E444446303100FA5488
                //String strsend = "FF55001F60B200000C4B00140000A404000E315041592E5359532E444446303100FA8788";
                send = ToolFun.hexStringToBytes(stradpu);//ToolFun.toPackData((byte) 0x60, CMD.readidinfo);
                iRet = libserialport_api.card_ADPU(_fd, send.length, send, rlen, in, 4000);
                //iRet = libserialport_api.device_write(_fd, send, send.length);
                if (iRet == 0) {//ADPU
                    str = ToolFun.printHexString(in);
                }
                // iRet = libserialport_api.device_write(_fd, send, send.length);
                //if (iRet > 0) {//ADPU
                //  Arrays.fill(in, (byte) 0);
                // iRet = libserialport_api.device_read(_fd, in, 4000);
                // if (iRet > 0) {
                // str = ToolFun.printHexString(in);
                // }


//                        }
//                    }
                // }
            }

        }
        libserialport_api.RF_Control(_fd, (byte) 0);
        return str;
    }
}
