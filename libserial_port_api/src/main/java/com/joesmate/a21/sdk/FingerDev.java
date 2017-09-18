package com.joesmate.a21.sdk;

import com.joesmate.a21.serial_port_api.libserialport_api;

/**
 * Created by andre on 2017/8/9 .
 */

public abstract class FingerDev {
    private static final String TAG = "com.joesmate.a21.sdk.FingerDev";

    /**
     * 指纹设备描述符
     */
    protected static int m_fp_fd = -1;

    public void setDevFd(int fd) {
        m_fp_fd = fd;
    }

    /**
     * 注册指纹
     *
     * @param outTime
     * @return
     */
    public abstract byte[] regFingerPrint(int outTime);

    /**
     * 获取指纹
     *
     * @param outTime
     * @return
     */
    public abstract byte[] sampFingerPrint(int outTime);

    /**
     * 获取指纹图像
     *
     * @param outTime
     * @return
     */
    public abstract byte[] imgFingerPrint(int outTime);

    //设置指纹接口波特率
    protected int setBaud(int baud) {
        return libserialport_api.device_set_baud(m_fp_fd, baud);
    }
}
