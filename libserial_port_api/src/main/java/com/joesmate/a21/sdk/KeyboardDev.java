package com.joesmate.a21.sdk;

import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

/**
 * Created by andre on 2017/8/3 .
 */

public class KeyboardDev {
    private static final KeyboardDev mInstance = new KeyboardDev();

    private void KeyboardDev() {
    }

    GPIO gpio4 = new GPIO(4, 0);

    public static KeyboardDev getInstance() {

        return mInstance;
    }

    /**
     * 恢复密码键盘出厂密钥 ,主密钥为“8888888888888888”，工作密钥为“0000000000000000”加密后（“A17A07EB843F1C5D90BC483958A60FA5”）
     *
     * @param fd 描述符
     * @return int 0 成功 <0 失败
     */
    public int RestDefaultKey(int fd) {
        String strZMK = "8888888888888888";
        String strZPK = "0000000000000000";
        byte[] ZMK = strZMK.getBytes();
        byte[] ZPK = strZPK.getBytes();
        gpio4.Down(1);
        int iRet = libserialport_api.RestDefaultKey(fd, ZMK, ZPK);

        return 0;
    }

    /**
     * 获取明文密钥
     *
     * @param fd
     * @param timeout
     * @return
     */
    public byte[] getEnablePassword(int fd, int length, int endTpye, long timeout) {
        byte[] InputBuffer = new byte[4096];
        byte[] cmd = CMD.readkb1;
        cmd[CMD.readkb1.length - 2] = (byte) length;
        cmd[CMD.readkb1.length - 1] = (byte) endTpye;
        byte[] send = ToolFun.toPackData((byte) 0x60, cmd);
        gpio4.Down(1);
        libserialport_api.device_write(fd, send, send.length);
        int iRet = libserialport_api.device_read(fd, InputBuffer, timeout);
        if (iRet > 0) {
            int len = iRet; //InputBuffer[0]/2;
            byte[] mun = new byte[len];
            System.arraycopy(InputBuffer, 0, mun, 0, len);
            return mun;
            //   String str = ToolFun.printHexString(mun); //new String(InputBuffer, "GBK");
            // return str;


        } else {
            return null;

        }
    }

    /**
     * 获取密文密钥
     *
     * @param fd
     * @param timeout
     * @return
     */
    public byte[] getCryptPassword(int fd, int length, int endTpye, long timeout) {
        byte[] InputBuffer = new byte[4096];
        byte[] cmd = CMD.readkb2;
        cmd[CMD.readkb2.length - 2] = (byte) length;
        cmd[CMD.readkb2.length - 1] = (byte) endTpye;
        byte[] send = ToolFun.toPackData((byte) 0x60, cmd);
        gpio4.Down(1);
        libserialport_api.device_write(fd, send, send.length);
        int iRet = libserialport_api.device_read(fd, InputBuffer, timeout);
        if (iRet > 0) {
            int len = iRet; //InputBuffer[0]/2;
            byte[] mun = new byte[len];
            System.arraycopy(InputBuffer, 0, mun, 0, len);
            return mun;
        } else {
            return null;

        }
    }

    /**
     * 更新主密钥
     *
     * @param fd
     * @param ZmkIndex
     * @param ZmkLength
     * @param Zmk
     * @return
     */
    public int DownMkey(int fd, int ZmkIndex, int ZmkLength, byte[] Zmk) {
        gpio4.Down(1);
        int iRet = libserialport_api.DownMKey(fd, (byte) ZmkIndex, (byte) ZmkLength, Zmk);
        return 0;
    }

    /**
     * @param fd
     * @param MKeyIndex
     * @param WKeyIndex
     * @param WKeyLength
     * @param Key
     * @return
     */
    public int DownWkey(int fd, int MKeyIndex, int WKeyIndex, int WKeyLength, byte[] Key) {
        gpio4.Down(1);
        int iRet = libserialport_api.DownWKey(fd, (byte) MKeyIndex, (byte) WKeyIndex, (byte) WKeyLength, Key);
        return 0;
    }

    /**
     * @param fd
     * @param MKeyIndex
     * @param WKeyIndex
     * @return
     */
    public int ActiveWKey(int fd, int MKeyIndex, int WKeyIndex) {
        gpio4.Down(1);
        int iRet = libserialport_api.ActiveKey(fd, (byte) MKeyIndex, (byte) WKeyIndex);
        return 0;
    }
}
