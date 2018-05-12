package com.joesmate.a21.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;

import java.io.FileOutputStream;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_WORLD_WRITEABLE;

/**
 * Created by andre on 2017/7/1 .
 */

public class ReaderDev {
    private ReaderDev() {
    }

    private static volatile ReaderDev m_instance;

    public static ReaderDev getInstance() {
        if (m_instance == null) {
            synchronized (ReaderDev.class) {
                if (m_instance == null)
                    m_instance = new ReaderDev();
            }
        }
        return m_instance;
    }

    private GPIO CCoreGpio = new GPIO(2, 0);
    private GPIO FpGpio = new GPIO(64, 0);
    private GPIO BtGpio = new GPIO(80, 0);
    private GPIO EleScreenGpio = new GPIO(86, 0);


    /**
     * 360复位
     */
    public void CCoreRest() {
        CCorePowerOff();//下电
        Delay(10000);
        CCorePowerOn();//上电
    }

    /**
     * 指纹复位
     */
    public void FpRest() {
        FpPowerOff();//下电
        Delay(800);
        FpPowerOn();//上电
    }

    /**
     * 重置电磁屏
     */
    public void ES_Rest() {
        ESPowerOff();
        Delay(100);
        ESPowerOn();
    }

    /**
     * 360断电
     */
    public void CCorePowerOff() {
        CCoreGpio.Down(1);
    }

    /**
     * 360 下电
     */
    public void CCorePowerOn() {
        CCoreGpio.Up(1);
    }

    public int FpPowerOff() {
        try {
            FpGpio.Down(1);
            return 0;
        } catch (Exception ex) {
            return 1;
        }

    }

    public int FpPowerOn() {
        try {
            FpGpio.Up(1);
            return 0;
        } catch (Exception ex) {
            return 1;
        }

    }


    public int ESPowerOff() {
        try {
            EleScreenGpio.Down(1);
            return 0;
        } catch (Exception ex) {
            return 1;
        }

    }

    public int ESPowerOn() {
        try {
            EleScreenGpio.Up(1);
            return 0;
        } catch (Exception ex) {
            return 1;
        }
    }

    private void Delay(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭360射频
     *
     * @param fd
     * @return
     */
    public int RF_Off(int fd) {
        return libserialport_api.RF_Control(fd, (byte) 0x00);
    }

    /**
     * 打开360射频
     *
     * @param fd
     * @return
     */
    public int RF_On(int fd) {
        return libserialport_api.RF_Control(fd, (byte) 0x03);
    }

    /**
     * 获取序列号
     *
     * @param context
     * @return
     */
    public String getSnr(Context context) {
        //步骤1：创建一个SharedPreferences接口对象
        SharedPreferences read = context.getSharedPreferences("joesmate.dev", MODE_WORLD_WRITEABLE);
        //步骤2：获取文件中的值
        String value = read.getString("DevSnr", Build.SERIAL);
        return value;
    }

    /**
     * 设置序列号
     *
     * @param context
     * @param Snr
     * @return
     */
    public int setSnr(Context context, String Snr) {
        try {
            //步骤2-1：创建一个SharedPreferences.Editor接口对象，lock表示要写入的XML文件名，MODE_WORLD_WRITEABLE写操作
            SharedPreferences.Editor editor = context.getSharedPreferences("joesmate.dev", MODE_WORLD_WRITEABLE).edit();
            //步骤2-2：将获取过来的值放入文件
            editor.putString("DevSnr", Snr);
            //步骤3：提交
            editor.commit();
        } catch (Exception ex) {
            return -1;
        }

        return 0;
    }
}
