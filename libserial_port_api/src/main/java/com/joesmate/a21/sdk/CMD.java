package com.joesmate.a21.sdk;

/**
 * Created by andre on 2017/6/8 .
 */

public class CMD {

    /**
     * 激活A卡
     */
    public static final byte[] activenfc = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'B',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 激活B卡
     */
    public static final byte[] activeid = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'C',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 激活IC-1卡
     */
    public static final byte[] activeic = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'G',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 激活磁条卡
     */
    public static final byte[] activemc = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'H',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 获取RATS
     */
    public static final byte[] getRats = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'J',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 获取nfc卡号
     */
    public static final byte[] readnfc = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'M',
            (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x00};

    /**
     * 获取ic卡号
     */
    public static final byte[] readic = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'M',
            (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x01};

    /**
     * 获取身份证号
     */
    public static final byte[] readidnum = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'M',
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x02};

    /**
     * 获取身份证信息
     */
    public static final byte[] readidinfo = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'X',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 设备休眠
     */
    public static final byte[] device_sleep = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'F',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 序列号
     */
    public static final byte[] readSnr = {(byte) 0xb2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 0x41, (byte) 0x00, (byte) 0x00,
            (byte) 0x00};

    /**
     * 磁头
     */
    public static final byte[] readmc = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'H',
            (byte) 0x00, (byte) 0x00, (byte) 0x00};

    // ------------------------------------------密码键盘----------------------------------------------
    /**
     * 密码键盘明文
     */
    public static final byte[] readkb1 = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'Q',
            (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};

    /**
     * 密码键盘密文
     */
    public static final byte[] readkb2 = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'Q',
            (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00};

    /**
     * 恢复密钥
     */
    public static final byte[] restkb = {(byte) 0xB2, (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 'Q',
            (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00};

}
