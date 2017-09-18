package com.joesmate.a21.sdk;

import android.app.Application;
import android.content.Context;

import com.joesmate.BaesTextToSpeech;
import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.util.Arrays;

/**
 * Created by andre on 2017/6/8 .
 */

public class IDCardDev {
    int _fd = -1;
    BaesTextToSpeech m_tts;

    public IDCardDev(int fd, BaesTextToSpeech tts) {
        _fd = fd;
        m_tts = tts;
    }

    public String ReadID() {
        String num = "";
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activeid);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活B卡
            byte[] in = new byte[4096];
            iRet = libserialport_api.device_read(_fd, in, 1000);
            if (iRet > 0) {
                send = ToolFun.toPackData((byte) 0x60, CMD.readidinfo);
                iRet = libserialport_api.device_write(_fd, send, send.length);
                if (iRet > 0) {//读身份证号
                    Arrays.fill(in, (byte) 0);
                    iRet = libserialport_api.device_read(_fd, in, 1000);
                    if (iRet > 0) {
                        num = new String(in);
                    }
                }
            }
        }
        return num;
    }

    GPIO gpio4 = new GPIO(4, 0);
    GPIO gpio12 = new GPIO(12, 0);

    public byte[] ReadBaseMsg() {
        byte[] tmp = new byte[2049];
//        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
        int iRet = -1;
//        int iRet = libserialport_api.device_write(_fd, send, send.length);
//        libserialport_api.flush(_fd);
//        if (iRet > 0) {//激活A卡
        gpio12.Down(1);
        libserialport_api.RF_Control(_fd, (byte) 0);
        gpio4.Up(1);
        ToolFun.Dalpey(200);
        iRet = libserialport_api.device_ReadBaseData(_fd, tmp, 8000);
        //gpio4.Down(1);
        if (iRet > 0) {
            int len = iRet;
            byte[] return_data = new byte[len];
            Arrays.fill(return_data, (byte) 0x00);
            System.arraycopy(tmp, 0, return_data, 0, iRet);
            m_tts.speak("读卡成功");
            return return_data;
        } else {
            m_tts.speak("读卡失败");
            byte[] return_data = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x80};
            return return_data;
        }

//        }
        // m_tts.speak("读卡失败");
        // return tmp;
    }

    public byte[] ReadBaseMsgFp() {
        byte[] tmp = new byte[2049];
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活A卡
            libserialport_api.RF_Control(_fd, (byte) 0);
            gpio4.Up(1);
            ToolFun.Dalpey(200);
            iRet = libserialport_api.device_ReadBaseDataFp(_fd, tmp, 2000);
            //  gpio4.Down(1);
            if (iRet > 0) {
                int len = iRet + 2;
                byte[] return_data = new byte[len];
                Arrays.fill(return_data, (byte) 0x00);
                System.arraycopy(tmp, 0, return_data, 2, iRet);
                return return_data;
            } else {
                byte[] return_data = {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x80};
                return return_data;
            }

        }
        return tmp;
    }


}
