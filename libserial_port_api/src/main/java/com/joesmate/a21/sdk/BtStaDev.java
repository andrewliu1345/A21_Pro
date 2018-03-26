package com.joesmate.a21.sdk;

import android.util.Xml;

import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by andre on 2018/3/19 .
 */
//蓝牙模块
public class BtStaDev {

    private static final BtStaDev mInstance = new BtStaDev();
    private GPIO BtGpio = new GPIO(80, 0);

    /**
     * 蓝牙模块单例
     *
     * @return 蓝牙模块类
     */
    public static BtStaDev getInstance() {
        return mInstance;
    }

    /**
     * 打蓝牙电源
     *
     * @return 成功为0, 设备为1.
     */
    public int BtPowerOn() {
        try {
            BtGpio.Up(1);
            return 0;
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * 关闭蓝牙电源
     *
     * @return 成功为0, 设备为1.
     */
    public int BtPowerOff() {
        try {
            BtGpio.Down(1);
            return 0;
        } catch (Exception ex) {
            return 1;
        }
    }

    public int ChangeBtName(int btfd, String BtName) {
        try {
            if (BtName.equals(null) || BtName.equals(""))
                BtName = "joesmate";
            byte[] cmd = {(byte) 0x08, (byte) 0x01};
            byte[] NameBuff = BtName.getBytes("US-ASCII");
            int len=NameBuff.length+1;
            byte[]tmp=new byte[NameBuff.length+5];

            byte[] buff = ToolFun.CreBtSendData(cmd, NameBuff);

            BtPowerOff();
            Thread.sleep(1000);
            BtPowerOn();
            int iRet = libserialport_api.device_write(btfd, buff, buff.length);
            BtPowerOff();
            Thread.sleep(1000);
            BtPowerOn();
        } catch (Exception ex) {

            return 1;
        }


        return 0;

    }
}
