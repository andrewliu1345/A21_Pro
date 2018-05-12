package com.joesmate.a21.backgroundservices.bin;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.sdk.KeyboardDev;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andre on 2017/7/25 .
 */

public class Keyboard {
    private static final Keyboard mInstance = new Keyboard();

    int m360fd = -1;

    public void setFD(int fd) {
        m360fd = fd;
    }

    private void Keyboard() {
    }

    public static Keyboard getInstance() {
        return mInstance;
    }

    /**
     * 获取密钥
     *
     * @param data
     * @return
     */
    public byte[] getPassword(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;
        byte[] iencryType = new byte[len];
        System.arraycopy(data, ++pos, iencryType, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] iTimes = new byte[len];
        System.arraycopy(data, ++pos, iTimes, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] iLength = new byte[len];
        System.arraycopy(data, ++pos, iLength, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] strVoice = new byte[len];
        System.arraycopy(data, ++pos, strVoice, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] EndType = new byte[len];
        System.arraycopy(data, ++pos, EndType, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] strTimeout = new byte[len];
        System.arraycopy(data, ++pos, strTimeout, 0, len);
        // pos += len;

        int enctryTppe = iencryType[0] & 0xff;
        int times = iTimes[0] & 0xff;
        int length = iLength[0] & 0xff;
        String voice = new String(strVoice);
        int endtype = EndType[0] & 0xff;
        int timeout = strTimeout[0] & 0xff;

        App.getInstance().tts.speak(voice);

        byte[] password = new byte[]{};

        byte[] password2 = new byte[]{};
        if (enctryTppe == 1)
            password = KeyboardDev.getInstance().getEnablePassword(m360fd, length, endtype, timeout * 1000);
        else if (enctryTppe == 5)
            password = KeyboardDev.getInstance().getCryptPassword(m360fd, length, endtype, timeout * 1000);

        List<byte[]> Passwords = new ArrayList<>();
        while (--times > 0) {
            App.getInstance().tts.speak("请再次输入密码");
            if (enctryTppe == 1)
                password2 = KeyboardDev.getInstance().getEnablePassword(m360fd, length, endtype, timeout * 1000);
            else if (enctryTppe == 5)
                password2 = KeyboardDev.getInstance().getCryptPassword(m360fd, length, endtype, timeout * 1000);
        }

        return password;
        //  return null;
    }

    /**
     * 初始化密码键盘
     *
     * @return
     */
    public int InitPinPad() {
        return KeyboardDev.getInstance().RestDefaultKey(m360fd);
    }

    /**
     * 密文更新主密钥
     *
     * @param data
     * @return
     */
    public int DownMKey(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;

        byte[] index = new byte[len];
        System.arraycopy(data, ++pos, index, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] length = new byte[len];
        System.arraycopy(data, ++pos, length, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] Zmk = new byte[len];
        System.arraycopy(data, ++pos, Zmk, 0, len);
        int ZmkIndex = index[0], ZmkLength = length[0];

        if (ZmkLength == 8)
            ZmkLength = 1;
        else if (ZmkLength == 16)
            ZmkLength = 2;
        else if (ZmkLength == 32)
            ZmkLength = 3;
        return KeyboardDev.getInstance().DownMkey(m360fd, ZmkIndex, ZmkLength, Zmk);
    }

    /**
     * 明文更新主密钥
     *
     * @param data
     * @return
     */
    public int DownMKey2(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;

        byte[] index = new byte[len];
        System.arraycopy(data, ++pos, index, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] length = new byte[len];
        System.arraycopy(data, ++pos, length, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] Zmk = new byte[len];
        System.arraycopy(data, ++pos, Zmk, 0, len);
        int ZmkIndex = index[0], ZmkLength = length[0];

        if (ZmkLength == 8)
            ZmkLength = 1;
        else if (ZmkLength == 16)
            ZmkLength = 2;
        else if (ZmkLength == 32)
            ZmkLength = 3;
        return KeyboardDev.getInstance().DownMkey(m360fd, ZmkIndex, ZmkLength, Zmk);
    }

    /**
     * 下载工作密钥
     *
     * @param data
     * @return
     */
    public int DownWKey(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;

        byte[] Mindex = new byte[len];
        System.arraycopy(data, ++pos, Mindex, 0, len);
        pos += len;

        byte[] Windex = new byte[len];
        System.arraycopy(data, ++pos, Windex, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] length = new byte[len];
        System.arraycopy(data, ++pos, length, 0, len);
        pos += len;

        len = data[pos] & 0xff;
        byte[] Zwk = new byte[len];
        System.arraycopy(data, ++pos, Zwk, 0, len);
        int ZmkIndex = Mindex[0], ZwkIndex = Windex[0], ZmkLength = length[0];
        if (ZmkLength == 8)
            ZmkLength = 1;
        else if (ZmkLength == 16)
            ZmkLength = 2;
        else if (ZmkLength == 32)
            ZmkLength = 3;
        return KeyboardDev.getInstance().DownWkey(m360fd, ZmkIndex, ZwkIndex, ZmkLength, Zwk);
    }


    /**
     * 激活工作密钥
     *
     * @param data
     * @return
     */
    public int ActiveWKey(byte[] data) {
        int pos = 3;
        int len = data[pos] & 0xff;

        byte[] Mindex = new byte[len];
        System.arraycopy(data, ++pos, Mindex, 0, len);
        pos += len;

        byte[] Windex = new byte[len];
        System.arraycopy(data, ++pos, Windex, 0, len);
        int MKeyIndex = Mindex[0], WKeyIndex = Windex[0];
        return KeyboardDev.getInstance().ActiveWKey(m360fd, MKeyIndex, WKeyIndex);
    }
}
