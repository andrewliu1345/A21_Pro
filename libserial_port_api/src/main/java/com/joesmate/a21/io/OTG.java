package com.joesmate.a21.io;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by andre on 2017/6/1 .
 */

public class OTG {
    private static final String TAG = "OTG";
    private static final String gpioURI = "/sys/devices/virtual/misc/mtgpio/pin";

    //GPIO 2
    private static final String gpio2_dir_cmd = "-wdir2  1";
    private static final String gpio2_mode_cmd = "-wmode2  0";
    private static final String gpio2_out_cmd_0 = "-wdout2  0";
    private static final String gpio2_out_cmd_1 = "-wdout2  1";

    //GPIO 3
    private static final String gpio3_dir_cmd = "-wdir3  1";
    private static final String gpio3_mode_cmd = "-wmode3  0";
    private static final String gpio3_out_cmd_0 = "-wdout3  0";
    private static final String gpio3_out_cmd_1 = "-wdout3  1";

    //GPIO 86
    private static final String gpio86_dir_cmd = "-wdir86  1";
    private static final String gpio86_mode_cmd = "-wmode86  0";
    private static final String gpio86_out_cmd_0 = "-wdout86  0";
    private static final String gpio86_out_cmd_1 = "-wdout86  1";

    public static int Open() {
        writeToFile(gpioURI, gpio3_mode_cmd);
        writeToFile(gpioURI, gpio3_dir_cmd);
        writeToFile(gpioURI, gpio3_out_cmd_1);
        try {
            Thread.sleep(10L);
            writeToFile(gpioURI, gpio86_mode_cmd);
            writeToFile(gpioURI, gpio86_dir_cmd);
            writeToFile(gpioURI, gpio86_out_cmd_1);
            return 0;
        } catch (Exception paramAnonymousCompoundButton) {
            try {
                Thread.sleep(100L);
                writeToFile(gpioURI, gpio2_mode_cmd);
                writeToFile(gpioURI, gpio2_dir_cmd);
                writeToFile(gpioURI, gpio2_out_cmd_0);
                return 0;

            } catch (Exception ex) {
                Log.e(TAG, "Open: ", ex);
            }
        }
        return -1;
    }

    public static int Close() {
        writeToFile(gpioURI, gpio3_mode_cmd);
        writeToFile(gpioURI, gpio3_dir_cmd);
        writeToFile(gpioURI, gpio3_out_cmd_0);
        try {
            Thread.sleep(10);
            writeToFile(gpioURI, gpio2_mode_cmd);
            writeToFile(gpioURI, gpio2_dir_cmd);
            writeToFile(gpioURI, gpio2_out_cmd_1);
            writeToFile(gpioURI, gpio86_mode_cmd);
            writeToFile(gpioURI, gpio86_dir_cmd);
            writeToFile(gpioURI, gpio86_out_cmd_0);
            return 0;
        } catch (Exception ex) {
            Log.e(TAG, "Open: ", ex);
        }
        return -1;
    }

    private static void writeToFile(String URI, String cmd) {
        try {
            FileWriter paramString1 = new FileWriter(URI);
            paramString1.write(cmd);
            paramString1.flush();
            paramString1.close();
        } catch (IOException ex) {
            Log.e(TAG, "writeToFile: ", ex);
        }
    }
}
