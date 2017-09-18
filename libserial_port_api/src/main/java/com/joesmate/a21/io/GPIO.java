package com.joesmate.a21.io;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by andre on 2017/7/1 .
 */

public class GPIO {
    private static final String TAG = "GPIO";
    private static final String gpioURI = "/sys/devices/virtual/misc/mtgpio/pin";
    String gpio_mode_cmd;
    private int m_gpio;
    private int m_mode;

    /**
     * GPIO 构造函数
     *
     * @param gpio gpio 口
     * @param mode gpio 模式 （一般为0）
     */
    public GPIO(int gpio, int mode) {
        m_gpio = gpio;
        m_mode = mode;
        gpio_mode_cmd = String.format("-wmode%d  %d", m_gpio, m_mode);
    }


    public void Up(int dir) {
        String gpio_dir_cmd = String.format("-wdir%d  %d", m_gpio, dir);
        String gpio_out_cmd = String.format("-wdout%d  1", m_gpio);
        writeToFile(gpioURI, gpio_dir_cmd);
        writeToFile(gpioURI, gpio_mode_cmd);
        writeToFile(gpioURI, gpio_out_cmd);
    }

    public void Down(int dir) {
        String gpio_dir_cmd = String.format("-wdir%d  %d", m_gpio, dir);
        String gpio_out_cmd = String.format("-wdout%d  0", m_gpio);
        writeToFile(gpioURI, gpio_dir_cmd);
        writeToFile(gpioURI, gpio_mode_cmd);
        writeToFile(gpioURI, gpio_out_cmd);
    }

    private static void writeToFile(String URI, String cmd) {
        try {
            Log.d(TAG,String.format("cmd=%s",cmd));
            FileWriter paramString1 = new FileWriter(URI);
            paramString1.write(cmd);
            paramString1.flush();
            paramString1.close();
        } catch (IOException ex) {
            Log.e(TAG, "writeToFile: ", ex);
        }
    }
}
