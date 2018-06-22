package com.joesmate.a21.backgroundservices.bin;


import android.content.Intent;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.backgroundservices.DataProcessingService;
import com.joesmate.a21.backgroundservices.Main2Activity;
import com.joesmate.a21.backgroundservices.R;
import com.joesmate.a21.sdk.FingerDev;
import com.joesmate.a21.sdk.ReaderDev;
import com.joesmate.a21.sdk.WlFingerDev;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;
import com.jostmate.IListen.OnReturnListen;

import java.sql.SQLSyntaxErrorException;

/**
 * Created by andre on 2017/9/18 .
 */

public class DataProcessingRunnable implements Runnable {
    private final String TAG = getClass().getName();
    byte[] data;
    byte[] in;
    int m_fd = -1;
    App mApp = App.getInstance();

    private DataProcessingRunnable() {
    }

    public DataProcessingRunnable(byte[] in_data, int fd) {
        in = in_data;
        data = getData(in_data);
        m_fd = fd;
    }

    @Override
    public void run() {
        Log.e(TAG, "Is Runing");
        int len = (in[1] & 0xff << 8) + (in[2] & 0xff);
        switch (data[0]) {
            case (byte) 0xc0: {//设备信息
                switch (data[1]) {
                    case (byte) 0x01: {//获取序列号
                        String snr = ReaderDev.getInstance().getSnr(App.getInstance());
                        byte[] buffer = snr.getBytes();
                        SendReturnData(buffer, buffer.length);
                        break;
                    }
                    case (byte) 0x02: {//修改序列号
                        int pos = 2;
                        int _len = data[pos] & 0xff;
                        byte[] h = new byte[_len];
                        System.arraycopy(data, ++pos, h, 0, _len);
                        String snr = new String(h);
                        int iRet = ReaderDev.getInstance().setSnr(App.getInstance(), snr);
                        if (iRet == 0)
                            sendOK();
                        else
                            sendErr();
                        break;
                    }
                    case (byte) 0x03: {
                        byte[] iRec = new byte[len - 2];
                        System.arraycopy(data, 2, iRec, 0, len - 2);
                        String strname = new String(iRec);
                        int iRet = DeviceData.setBtName(strname);
                        if (iRet == 0) {
                            sendOK();
                        } else {
                            sendErr();
                        }

                        break;
                    }
                    case (byte) 0x0d://设置时间
                    {
                        try {
                            DeviceData.getInstance().SetSysTime(data);
                            sendOK();
                        } catch (Exception ex) {
                            sendErr();
                            Log.e(TAG, ex.getMessage());
                        }
                        break;
                    }
                    case (byte) 0x0c://获取电量
                    {
                        try {
                            int p = DeviceData.getInstance().getPower();
                            byte[] send = new byte[3];
                            send[2] = (byte) p;
                            SendReturnData(send, send.length);
                        } catch (Exception ex) {
                            sendErr();
                            Log.e(TAG, ex.getMessage());
                        }

                        break;
                    }
                    case (byte) 0x09://获取标示符
                    {
                        try {
                            String sn = DeviceData.getInstance().getSerialNumber();
                            byte[] buff = sn.getBytes();
                            SendReturnData(buff, buff.length);
                        } catch (Exception ex) {
                            sendErr();
                            Log.e(TAG, ex.getMessage());
                        }
                        break;
                    }
                    case (byte) 0x0e: {//播放声音
                        try {
                            byte[] strdata = new byte[len - 2];
                            System.arraycopy(data, 2, strdata, 0, len - 2);
                            String s = new String(strdata);
                            mApp.tts.speak(s);
                            ToolFun.Dalpey(10);
                            sendOK();
                        } catch (Exception ex) {
                            sendErr();
                        }
                    }
                }
                break;
            }
            case (byte) 0x31: {
                switch (data[1]) {
                    case (byte) 0x11: {//固件版本号
                        try {
                            String ver = DeviceData.getVersionName();
                            byte[] buff = ver.getBytes();
                            SendReturnData(buff, buff.length);
                        } catch (Exception ex) {
                            sendErr();
                            Log.e(TAG, ex.getMessage());
                        }

                        break;
                    }

                }
                break;
            }
            case (byte) 0x32: {
                switch (data[1]) {
                    case (byte) 0x50: {
                        try {
                            mApp.tts.speak(mApp.getString(R.string.PleaseDropIdCard));
                            IDCardRead.getInstance().ShowActivity(App.getInstance().getApplicationContext(), Main2Activity.class);
                            byte[] send = IDCardRead.getInstance().ReadBaseMsg();
                            IDCardRead.getInstance().CloseActivity(App.getInstance());
                            SendReturnData(send, send.length);
                        } catch (Exception ex) {
                            sendErr();
                            Log.e(TAG, ex.getMessage());
                        }
                        break;
                    }
                    case (byte) 0x60: {//读取卡信息
                        int code[] = new int[]{-1};
                        byte[] iData = MagneticCard.getInstance().ReadData(code);
                        if (code[0] > 0)
                            SendReturnData(iData, iData.length);
                        else {
                            SendData(iData, iData.length);
                        }
                        break;
                    }
                    case (byte) 0x62: {//开始刷卡
                        App.getInstance().tts.speak(mApp.getString(R.string.PleaseBrushMagneticCard));
                        ToolFun.Dalpey(1000);
                        int iRet = MagneticCard.getInstance().SlotCard();
                        if (iRet < 0)
                            sendErr();
                        else
                            sendOK();
                        break;
                    }
                }
                break;
            }
            case (byte) 0xD0: {
                switch (data[1]) {
                    case (byte) 0x06: {//读身份证
                        try {
                            App.getInstance().tts.speak(mApp.getString(R.string.PleaseDropIdCard));
                            byte[] send = IDCardRead.getInstance().ReadBaseMsg();
                            SendReturnData(send, send.length);
                        } catch (Exception ex) {
                            sendErr();
                            Log.e(TAG, ex.getMessage());
                        }
                        break;
                    }
                    case (byte) 0x07: {//读身份证+指纹
                        try {
                            App.getInstance().tts.speak(mApp.getString(R.string.PleaseDropIdCard));
                            byte[] send = IDCardRead.getInstance().ReadBaseMsg();
                            SendReturnData(send, send.length);
                        } catch (Exception ex) {
                            sendErr();
                            Log.e(TAG, ex.getMessage());
                        }
                        break;
                    }
                }
                break;
            }
            case (byte) 0xC6: {
                switch (data[1]) {
                    case (byte) 0x01://设置波特率
                    {
                        int iRet = PassThrough.getInstance().setBaud(data[2]);
                        if (iRet >= 0)
                            sendOK();
                        else
                            sendErr();
                        break;
                    }
                    case (byte) 0x02: {//指纹
                        switch (data[2]) {
                            case (byte) 0x00: {//透传
                                App.getInstance().tts.speak(mApp.getString(R.string.PleasePushFp));
                                FingerDev fingerDev = new WlFingerDev();
                                byte[] send = new byte[len - 3];
                                System.arraycopy(data, 3, send, 0, len - 3);
                                fingerDev.ShowActivity(App.getInstance().getApplicationContext(), Main2Activity.class);
                                int iRet = PassThrough.getInstance().SendCMD(send);
                                fingerDev.CloseActivity(App.getInstance());
                                if (iRet >= 0)
                                    sendOK();
                                else
                                    sendErr();
                                break;
                            }
                            case (byte) 0x01: {//返回信息
                                int[] recode = new int[]{-1};
                                byte[] returndata = PassThrough.getInstance().getBuffer(recode);
                                if (recode[0] > 0)
                                    SendReturnData(returndata, returndata.length);
                                else
                                    SendData(returndata, returndata.length);
                                break;
                            }
                            case (byte) 0x02: {
                                switch (data[3]) {
                                    case 0x00: {
                                        FingerDev fingerDev = new WlFingerDev();
                                        fingerDev.setDevFd(App.getInstance().m_fpfd);
                                        try {
                                            //fingerDev.ShowActivity(App.getInstance().getApplicationContext(), Main2Activity.class);
                                            byte[] returndata = fingerDev.imgFingerPrint();
                                            SendReturnData(returndata, returndata.length);
                                        } catch (Exception e) {
                                            sendErr();
                                        }

                                        break;
                                    }
                                    case 0x01:
                                        break;
                                    case 0x02:
                                        break;
                                    case 0x03:
                                        break;
                                    default:
                                        sendErr();
                                }
                                break;
                            }
                        }
                        break;
                    }
                    case (byte) 0x03: {//密码键盘
                        switch (data[2]) {//模式
                            case (byte) 0x00: {
                                //App.getInstance().tts.speak("请输入密码");
                                int datalen = (data[5] & 0xff);
                                byte[] d = new byte[datalen];
                                System.arraycopy(data, 8, d, 0, datalen);
                                break;
                            }
                            case (byte) 0x01: {//获取密码
                                Keyboard.getInstance().setFD(App.getInstance().m_360fd);
                                byte[] send = Keyboard.getInstance().getPassword(data);
                                if (send != null && send.length > 0) {
                                    SendReturnData(send, send.length);
                                } else {
                                    sendErr();
                                }
                                break;
                            }
                            case (byte) 0x02: {
                                Keyboard.getInstance().setFD(App.getInstance().m_360fd);
                                int iRet = Keyboard.getInstance().InitPinPad();
                                if (iRet == 0)
                                    sendOK();
                                else
                                    sendErr();
                                break;
                            }
                            case (byte) 0x03: {
                                Keyboard.getInstance().setFD(App.getInstance().m_360fd);
                                int iRet = Keyboard.getInstance().DownMKey(data);
                                if (iRet == 0)
                                    sendOK();
                                else
                                    sendErr();
                                break;

                            }
                            case (byte) 0x04: {
                                Keyboard.getInstance().setFD(App.getInstance().m_360fd);
                                int iRet = Keyboard.getInstance().DownWKey(data);
                                if (iRet == 0)
                                    sendOK();
                                else
                                    sendErr();
                                break;
                            }
                            case (byte) 0x05: {
                                Keyboard.getInstance().setFD(App.getInstance().m_360fd);
                                int iRet = Keyboard.getInstance().ActiveWKey(data);
                                if (iRet == 0)
                                    sendOK();
                                else
                                    sendErr();
                                break;
                            }
                            default:
                                sendErr();
                                break;

                        }
                        break;
                    }
                }
                break;

            }
            case (byte) 0xc7: {
                switch (data[1]) {
                    case (byte) 0x01: {//签名
                        try {
                            App.getInstance().tts.speak(mApp.getString(R.string.PleaseSign));
                            Signature.getInstance().Start(App.getInstance().getApplicationContext(), new OnReturnListen() {
                                @Override
                                public void onSuess(Intent intent) {
                                    byte send[] = intent.getByteArrayExtra("imgbuff");
                                    Signature.getInstance().setBuffer(send);
                                    sendOK();//SendReturnData(send, send.length);
                                }

                                @Override
                                public void onErr(int code) {
                                    byte send[] = new byte[]{(byte) 0x01, (byte) 0x00};
                                    SendData(send, send.length);
                                }
                            });
                            sendOK();
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                            sendErr();
                        }

                        break;
                    }
                    case (byte) 0x02: {
                        try {
                            libserialport_api.Cancel();
                            Signature.getInstance().Exit();
                            sendOK();
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                            sendErr();
                        }
                        break;
                    }
                    case (byte) 0x03: {
                        try {

                            Signature.getInstance().Clear();
                            sendOK();
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                            sendErr();
                        }
                        break;
                    }
                    case (byte) 0x04: {
                        try {
                            int pos = 2;
                            int _len = data[pos] & 0xff;
                            byte[] h = new byte[_len];
                            System.arraycopy(data, ++pos, h, 0, _len);

                            pos += _len;
                            _len = data[pos] & 0xff;
                            byte[] w = new byte[_len];
                            System.arraycopy(data, ++pos, w, 0, _len);

                            String strheight = new String(h);
                            String strwidth = new String(w);

                            int heigth = Integer.parseInt(strheight);
                            int width = Integer.parseInt(strwidth);
                            App.getInstance().tts.speak(mApp.getString(R.string.PleaseSign));
                            Signature.getInstance().Start(App.getInstance().getApplicationContext(), heigth, width,
                                    new OnReturnListen() {
                                        @Override
                                        public void onSuess(Intent intent) {
                                            byte send[] = intent.getByteArrayExtra("imgbuff");
                                            Signature.getInstance().setBuffer(send);
                                            sendOK();
                                            //SendReturnData(send, send.length);
                                        }

                                        @Override
                                        public void onErr(int code) {
                                            byte send[] = new byte[]{(byte) 0x01, (byte) 0x00};
                                            SendData(send, send.length);
                                        }
                                    });
                            //sendOK();
                        } catch (Exception ex) {
                            Log.e(TAG, ex.getMessage());
                            sendErr();
                        }
                        break;
                    }
                    case 05: {
                        byte send[] = Signature.getInstance().getBuffer();
                        if (send != null) {
                            SendReturnData(send, send.length);
                        } else {
                            sendErr();
                        }
                        break;
                    }
                }
                break;
            }
            case (byte) 0x50: {//银行卡
                switch (data[1]) {
                    case (byte) 0x04: {//pboc
                        App.getInstance().tts.speak(mApp.getString(R.string.PleaseBankCard));
                        switch (data[2]) {
                            case (byte) 0x00: {
                                try {
                                    byte[] send = IDCardRead.getInstance().GetICCInfo(data);
                                    if (send == null)
                                        sendErr();
                                    else
                                        SendReturnData(send, send.length);
                                } catch (Exception ex) {
                                    sendErr();
                                }

                            }
                            break;
                            case (byte) 0x01: {
                                try {
                                    byte[] send = IDCardRead.getInstance().GetARQC(data);
                                    if (send == null)
                                        sendErr();
                                    else
                                        SendReturnData(send, send.length);
                                } catch (Exception ex) {
                                    sendErr();
                                }

                            }
                            break;
                            case (byte) 0x02: {
                                try {
                                    byte[] send = IDCardRead.getInstance().ARPCExeScript(data);
                                    if (send == null)
                                        sendErr();
                                    else
                                        SendReturnData(send, send.length);
                                } catch (Exception ex) {
                                    sendErr();
                                }

                            }
                            break;
                            case (byte) 0x03: {
                                try {
                                    byte[] send = IDCardRead.getInstance().GetTrDetail(data);
                                    if (send == null)
                                        sendErr();
                                    else
                                        SendReturnData(send, send.length);
                                } catch (Exception ex) {
                                    sendErr();
                                }

                            }
                            break;
                            case (byte) 0x04: {
                                try {
                                    byte[] send = IDCardRead.getInstance().GetLoadLog(data);
                                    if (send == null)
                                        sendErr();
                                    else
                                        SendReturnData(send, send.length);
                                } catch (Exception ex) {
                                    sendErr();
                                }

                            }
                            break;
                            case (byte) 0x05: {
                                try {
                                    byte[] send = IDCardRead.getInstance().GetICAndARQCInfo(data);
                                    if (send == null)
                                        sendErr();
                                    else
                                        SendReturnData(send, send.length);
                                } catch (Exception ex) {
                                    sendErr();
                                }

                            }
                            break;

                        }
                    }
                    break;
                    default:
                        sendErr();
                        break;
                }
            }
            break;
        }
    }

    private void SendData(byte[] writedata, int writeLen) {
        int len = writeLen;
        byte[] Writebuffer = new byte[len + 5];
        Writebuffer[0] = 2;
        Writebuffer[1] = (byte) (len >> 8);
        Writebuffer[2] = (byte) (len % 256);
        System.arraycopy(writedata, 0, Writebuffer, 3, writeLen);
        Writebuffer[3 + writeLen] = cr_bcc(writedata);
        Writebuffer[4 + writeLen] = 3;
        String sendStr = "";

        for (byte buff : Writebuffer) {
            sendStr = sendStr + String.format("%02X", buff) + " ";
        }

        Log.d(TAG, String.format("Return Send: SendData=%s", sendStr));
        libserialport_api.device_bt_write(m_fd, Writebuffer, Writebuffer.length);
    }

    private void SendReturnData(byte[] writedata, int writeLen) {
        int len = writeLen + 2;
        byte[] Writebuffer = new byte[len + 5];
        Writebuffer[0] = 2;
        Writebuffer[1] = (byte) (len >> 8);
        Writebuffer[2] = (byte) (len % 256);
        Writebuffer[3] = 0;
        Writebuffer[4] = 0;
        System.arraycopy(writedata, 0, Writebuffer, 5, writeLen);
        Writebuffer[5 + writeLen] = cr_bcc(writedata);
        Writebuffer[6 + writeLen] = 3;
//        String sendStr = "";
//
//        for (byte buff : Writebuffer) {
//            sendStr = sendStr + String.format("%02X", buff) + " ";
//        }
//
//        Log.d(TAG, String.format("Return Send :len=%d CRC=%02X SendReturnData=%s", len + 5, Writebuffer[5 + writeLen], sendStr));
        libserialport_api.device_bt_write(m_fd, Writebuffer, Writebuffer.length);
    }


    private byte cr_bcc(byte[] data) {
        byte temp = 0;
        for (byte item :
                data) {
            temp ^= item;
        }
        return temp;
    }

    private byte[] getData(byte[] src) {
        if (src[0] != 0x02)
            return null;
        int len = (src[1] & 0xff << 8) + src[2] & 0xff;
        if (src[len + 4] != (byte) 0x03)
            return null;
        byte[] dest = new byte[len];
        System.arraycopy(src, 3, dest, 0, len);
        byte crc = cr_bcc(dest);
        if (src[len + 3] != crc)
            return null;
        return dest;
    }


    static final byte[] err = {(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x01, (byte) 0x03};
    static final byte[] ok = {(byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03};

    private void sendErr() {
        libserialport_api.device_write(m_fd, err, err.length);
    }

    private void sendOK() {
        libserialport_api.device_write(m_fd, ok, ok.length);
    }

    interface OnDataProcessingEventLisnter {
        void onCardDected();

        void onCancel();

        void onError(String error);

        void onFinish(String[] result);
    }
}
