/*    */
package com.joesmate.sdk.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.util.Log;

import junit.runner.Version;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@SuppressLint("DefaultLocale")
public class ToolFun {
    static final String TAG = "com.joesmate.sdk.util.ToolFun";

    public static int asc_hex(byte[] asc, byte[] hex, int asclen) {
        String ss = new String(asc);
        int string_len = ss.length();
        int len = asclen;
        if (string_len % 2 == 1) {
            ss = "0" + ss;
            len++;
        }
        for (int i = 0; i < len; i++) {
            hex[i] = ((byte) Integer.parseInt(ss.substring(2 * i, 2 * i + 2), 16));
        }
        return 0;
    }

    public static String hex_split(byte[] hex, int blen) {
        String strsplit = "";
        byte[] asc = new byte[blen * 2];
        hex_asc(hex, asc, blen);
        String strasc = new String(asc);
        char[] charasc = strasc.toCharArray();
        for (char c : charasc) {
            strsplit += "3" + c;
        }
        String str = new String(hexStringToBytes(strsplit));
        return str;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.replace(" ", "").toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static int hex_asc(byte[] hex, byte[] asc, int blen) {
        for (int i = 0; i < blen; i++) {
            byte temp = (byte) (hex[i] & 0xF0);
            if (temp < 0) {
                temp = (byte) (hex[i] & 0x70);
                temp = (byte) ((byte) (temp >> 4) + 8);
            } else {
                temp = (byte) (hex[i] >> 4);
            }
            if ((temp >= 0) && (temp <= 9)) {
                asc[(i * 2 + 0)] = ((byte) (temp + 48));
            } else if ((temp >= 10) && (temp <= 15)) {
                asc[(i * 2 + 0)] = ((byte) (temp + 65 - 10));
            } else {
                asc[(i * 2 + 0)] = 48;
            }
            temp = (byte) (hex[i] & 0xF);
            if ((temp >= 0) && (temp <= 9)) {
                asc[(i * 2 + 1)] = ((byte) (temp + 48));
            } else if ((temp >= 10) && (temp <= 15)) {
                asc[(i * 2 + 1)] = ((byte) (temp + 65 - 10));
            } else asc[(i * 2 + 1)] = 48;
        }
        return 0;
    }


    public static int hex_asc(byte[] hex, int hexPos, byte[] asc, int blen) {
        for (int i = hexPos; i < hexPos + blen; i++) {
            byte temp = (byte) (hex[i] & 0xF0);
            if (temp < 0) {
                temp = (byte) (hex[i] & 0x70);
                temp = (byte) ((byte) (temp >> 4) + 8);
            } else {
                temp = (byte) (hex[i] >> 4);
            }
            if ((temp >= 0) && (temp <= 9)) {
                asc[((i - hexPos) * 2 + 0)] = ((byte) (temp + 48));
            } else if ((temp >= 10) && (temp <= 15)) {
                asc[((i - hexPos) * 2 + 0)] = ((byte) (temp + 65 - 10));
            } else {
                asc[((i - hexPos) * 2 + 0)] = 48;
            }
            temp = (byte) (hex[i] & 0xF);
            if ((temp >= 0) && (temp <= 9)) {
                asc[((i - hexPos) * 2 + 1)] = ((byte) (temp + 48));
            } else if ((temp >= 10) && (temp <= 15)) {
                asc[((i - hexPos) * 2 + 1)] = ((byte) (temp + 65 - 10));
            } else asc[((i - hexPos) * 2 + 1)] = 48;
        }
        return 0;
    }

    public static int cr_bcc(int len, byte[] data) {

        int temp = 0;
        for (int i = 0; i < len; i++)
            temp ^= data[i];
        return temp;
    }

    public static void splitFun(byte[] usplitdata, byte ulen, byte[] splitdata, byte slen) {

        int nI = 0;
        for (int nJ = 0; nI < ulen * 2; nJ++) {
            splitdata[nI] = ((byte) (((usplitdata[nJ] & 0xF0) >> 4) + 48));
            splitdata[(nI + 1)] = ((byte) ((usplitdata[nJ] & 0xF) + 48));
            nI += 2;
        }
    }

    public static byte[] unSplitFun(byte[] splitdata) {
        int len = splitdata.length / 2;
        byte[] data = new byte[len];
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            data[i] = (byte) (((splitdata[pos] & 0x0f) << 4) + (splitdata[pos + 1] & 0x0f));
        }
        return data;
    }

    public static String printHexString(byte[] b) {
        String a = "";
        if (b == null || b.length == 0)
            return "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            a = a + hex;
        }
        return a;
    }

    public static int byteArrayToInt(byte[] b) {
        return b[1] & 0xFF |
                (b[0] & 0xFF) << 8;
    }

    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * 减法校验
     *
     * @param input 输入数组
     * @return 双字节校验值
     */
    public static byte getCheckSubCRC(byte[] input) {

        byte sub = input[0];
        for (int i = 1; i < input.length; i++) {
            sub -= input[i] & 0xff;
        }

        return sub;
    }

    /**
     * 加法校验
     *
     * @param input 输入数组
     * @return 双字节校验值
     */
    public static byte[] getCheckSumCRC(byte[] input) {
        byte[] output;
        short sub = 0;
        for (byte item :
                input) {
            sub += item & 0xFF;
        }
        output = intToByteArray(~sub);
        return output;
    }

    /**
     * 拆包
     *
     * @param data 需要拆解的数据
     * @return 拆解后的数据
     */
    public static byte[] unPackData(byte[] data) {
        int info_data_len = data[7] & 0xff + (data[6] << 8) & 0xff;
        byte[] info_data = new byte[info_data_len];
        System.arraycopy(data, 8, info_data, 0, info_data_len);
        return info_data;
    }

    static final byte[] handdata = {(byte) 0xFF, (byte) 0x55};

    /**
     * 数据装包
     *
     * @param type Command ID
     * @param data 需要装包的数据
     * @return 打包好的数据
     */
    public static byte[] toPackData(byte type, byte[] data) {
        int length = data.length;
        byte[] senddata = new byte[length + 6 + handdata.length];//头+长度+CommdID+数据+CRC+CRC
        byte[] tempdata = new byte[length + 5];//长度+CommdID+数据+CRC
        byte[] lengthdata = intToByteArray(length + 3);//长度字段
        byte[] sumcrc = getCheckSumCRC(data);//数据校验
        System.arraycopy(handdata, 0, senddata, 0, handdata.length);//头
        System.arraycopy(lengthdata, 0, tempdata, 0, lengthdata.length);//长度
        tempdata[lengthdata.length] = type;//CommdID
        System.arraycopy(data, 0, tempdata, lengthdata.length + 1, data.length);//数据
        System.arraycopy(sumcrc, 0, tempdata, lengthdata.length + 1 + data.length, 2);//数据校验

        System.arraycopy(tempdata, 0, senddata, handdata.length, tempdata.length);
        byte subcrc = getCheckSubCRC(tempdata);
        senddata[0 + handdata.length + lengthdata.length + 1 + data.length + 2] = subcrc;//数据包效验

        return senddata;
    }

    public static void Dalpey(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception ex) {
            Log.e(TAG, "Daley: ", ex);
        }
    }

    /**
     * 保存图片
     *
     * @param path   保存路径
     * @param bm     Bitmap
     * @param format 保存格式
     */
    public static void saveBitmap(String path, Bitmap bm, int quality, Bitmap.CompressFormat format) {
        // Log.e(TAG, "保存图片");
        File f = new File(path);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(format, quality, out);
            out.flush();
            out.close();
            // Log.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * @param inputByte 待解压缩的字节数组
     * @return 解压缩后的字节数组
     * @throws IOException
     */
    public static byte[] uncompress(byte[] inputByte) throws IOException {

        int len = 0;
        Inflater infl = new Inflater(false);
        infl.setInput(inputByte);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] outByte = new byte[1024];
        try {
            while (!infl.finished()) {
                // 解压缩并将解压缩后的内容输出到字节输出流bos中
                len = infl.inflate(outByte);
                if (len == 0) {
                    break;
                }
                bos.write(outByte, 0, len);
            }
            infl.end();
        } catch (Exception e) {
            //
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    /**
     * 压缩.
     *
     * @param inputByte 待压缩的字节数组
     * @return 压缩后的数据
     * @throws IOException
     */
    public static byte[] compress(byte[] inputByte) throws IOException {
        int len = 0;
        Deflater defl = new Deflater(Deflater.BEST_COMPRESSION, false);
        defl.setStrategy(Deflater.FILTERED);
        defl.setInput(inputByte);
        defl.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] outputByte = new byte[1024];
        try {
            while (!defl.finished()) {
                // 压缩并将压缩后的内容输出到字节输出流bos中
                len = defl.deflate(outputByte);
                bos.write(outputByte, 0, len);
            }
            defl.end();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return 版本号
     */
    public static String getApplicationVersionName(Context context) {
        String VersionName = "";
        int VersionCode = -1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageInfo.INSTALL_LOCATION_AUTO);
            VersionName = pi.versionName;
            VersionCode = pi.versionCode;
            if (VersionName == null || VersionName.length() <= 0) {
                return "";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return VersionName;
    }

    /**
     * 蓝牙控制指令组合
     *
     * @param cmd  指令类型
     * @param data 指令数据
     * @return 完成的指令
     */
    public static byte[] CreBtSendData(byte[] cmd, byte[] data) {
        int len = cmd.length + data.length;
        byte[] lendata = intToByteArray(len);
        byte[] tmp = new byte[len + 2];
        int flag = 0;

        System.arraycopy(lendata, 0, tmp, flag, 2);
        flag += 2;
        System.arraycopy(cmd, 0, tmp, flag, cmd.length);
        flag += cmd.length;
        System.arraycopy(data, 0, tmp, flag, data.length);
        flag += data.length;
        int sum = 0;
        for (byte item :
                tmp) {
            sum += item & 0xff;
        }
        int crcsum = 0x0100 - (sum & 0x00ff);

        byte[] buff = new byte[flag + 2];
        flag = 0;
        buff[flag++] = (byte) 0xaa;
        System.arraycopy(tmp, 0, buff, flag, tmp.length);
        flag += tmp.length;
        buff[flag++] = (byte) crcsum;
        return buff;
    }
}
