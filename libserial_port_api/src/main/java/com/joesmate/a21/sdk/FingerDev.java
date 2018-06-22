package com.joesmate.a21.sdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.joesmate.a21.serial_port_api.libserialport_api;

/**
 * Created by andre on 2017/8/9 .
 */

public abstract class FingerDev  {
    private static final String TAG = "com.joesmate.a21.sdk.FingerDev";

    /**
     * 显示界面
     */
    public void ShowActivity(Context context,Class<?> cls) {

        Intent intent = new Intent();
        intent.setClass(context,cls);
        intent.putExtra("action",1);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public  void CloseActivity(Application app)
    {
        Intent intent = new Intent("action.view");
        intent.putExtra("action", 1);
        app.sendBroadcast(intent);
    }

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

    /**
     * 获取指纹图像
     *
     * @return
     */
    public abstract byte[] imgFingerPrint() throws Exception;

    //设置指纹接口波特率
    protected int setBaud(int baud) {
        return libserialport_api.device_set_baud(m_fp_fd, baud);
    }
}
