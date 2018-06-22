package com.joesmate.a21.sdk;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.LogMg;
import com.joesmate.sdk.util.ToolFun;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by andre on 2017/10/25 .
 */

public class TcFingerDev extends FingerDev {
    @Override
    public byte[] regFingerPrint(int outTime) {
        return new byte[0];
    }

    @Override
    public byte[] sampFingerPrint(int outTime) {
        long start = System.currentTimeMillis();
        long sut = 0;
        ReaderDev.getInstance().FpPowerOn();
        setBaud(9600);//设置波特率
        libserialport_api.flush(m_fp_fd);

        byte[] writeBuf = new byte[9];
        byte[] temp = new byte[7];
        writeBuf[0] = 126;
        writeBuf[1] = 66;
        writeBuf[2] = 100;
        writeBuf[3] = 0;
        writeBuf[4] = 0;
        writeBuf[5] = 0;
        writeBuf[6] = 1;
        writeBuf[7] = 2;
        System.arraycopy(writeBuf, 1, temp, 0, 7);
        writeBuf[8] = (byte) ToolFun.cr_bcc(7, temp);

        int iRet = libserialport_api.device_write(m_fp_fd, writeBuf, writeBuf.length);//发送指令
        if (iRet < 0) {
            return new byte[0];
        }
        byte[] in = new byte[2048];
        sut = System.currentTimeMillis() - start;
        while (sut < outTime) {
            int dRead = libserialport_api.device_read_all(m_fp_fd, in);
            if (dRead > 0) {
                byte[] tmp = parseTcFingerData(in, dRead);
                if (tmp == null)
                    return new byte[0];
//                int userLen1;
//                if (tmp[0] < 0) {
//                    userLen1 = tmp[0] + 256;
//                } else {
//                    userLen1 = tmp[0];
//                }
//
//                int fingerLen1;
//                if (tmp[1] < 0) {
//                    fingerLen1 = tmp[1] + 256;
//                } else {
//                    fingerLen1 = tmp[1];
//                }
//                byte[] recvData = new byte[fingerLen1];
//                System.arraycopy(tmp, 2 + userLen1, recvData, 0, fingerLen1);
                return tmp;
            }
            sut = System.currentTimeMillis() - start;
        }
        return new byte[0];
    }

    @Override
    public byte[] imgFingerPrint(int outTime) {
        long start = System.currentTimeMillis();
        long sut = 0;
        ReaderDev.getInstance().FpPowerOn();
        setBaud(9600);//设置波特率
        libserialport_api.flush(m_fp_fd);

        byte[] writeBuf = new byte[12];
        byte[] temp = new byte[10];
        writeBuf[0] = 126;
        writeBuf[1] = 66;
        writeBuf[2] = -128;
        writeBuf[3] = 0;
        writeBuf[4] = 0;
        writeBuf[5] = 0;
        writeBuf[6] = 4;
        writeBuf[7] = 0;
        writeBuf[8] = 0;
        writeBuf[9] = 1;
        writeBuf[10] = 0;
        System.arraycopy(writeBuf, 1, temp, 0, 10);
        writeBuf[11] = (byte) ToolFun.cr_bcc(10, temp);

        int iRet = libserialport_api.device_write(m_fp_fd, writeBuf, writeBuf.length);//发送指令
        if (iRet < 0) {
            return new byte[0];
        }
        byte[] in = new byte[2048];
        sut = System.currentTimeMillis() - start;
        while (sut < outTime) {
            int dRead = libserialport_api.device_read_all(m_fp_fd, in);
            if (dRead > 0) {
                byte[] tmp = parseTcFingerData(in, dRead);
                int stSave = TcFingerDev.this.saveTcFigerImage(tmp,tmp.length,
                        "/sdcard/finger.png");
            }
            sut = System.currentTimeMillis() - start;
        }
        return new byte[0];
    }

    @Override
    public byte[] imgFingerPrint() {
        return new byte[0];
    }

    protected byte[] parseTcFingerData(byte[] readBuf, int readLen) {
        byte[] tempBuf1 = new byte[readLen];
        byte[] revData = new byte[readLen];
        int[] revLen = new int[]{0};

        if (readLen < 8) {
            return null;
        } else if (readBuf[0] != 126) {
            return null;
        } else if (readBuf[1] != 66) {
            return null;
        } else {
            System.arraycopy(readBuf, 1, tempBuf1, 0, readLen - 1);
            if (ToolFun.cr_bcc(readLen - 1, tempBuf1) != 0) {
                return null;
            } else if (readBuf[3] != 0) {
                LogMg.e("TcFingerDev", " readBuf[3]" + readBuf[3]);
                return null;
            } else {

                int len11;
                if (readBuf[4] < 0) {
                    len11 = readBuf[4] + 256;
                } else {
                    len11 = readBuf[4];
                }

                int len21;
                if (readBuf[5] < 0) {
                    len21 = readBuf[5] + 256;
                } else {
                    len21 = readBuf[5];
                }

                int len31;
                if (readBuf[6] < 0) {
                    len31 = readBuf[6] + 256;
                } else {
                    len31 = readBuf[6];
                }

                int len41;
                if (readBuf[7] < 0) {
                    len41 = readBuf[7] + 256;
                } else {
                    len41 = readBuf[7];
                }

                if ((len11 << 24) + (len21 << 16) + (len31 << 8) + len41 + 9 != readLen) {
                    return null;
                } else {
                    revLen[0] = readLen - 9;
                    System.arraycopy(readBuf, 8, revData, 0, revLen[0]);
                    return revData;
                }
            }
        }
    }

    private int saveTcFigerImage(byte[] recvData, int recvLen, String dirPath) {
        LogMg.i("TcFingerDev", "saveTcFigerImage enter");
        Bitmap bm = BitmapFactory.decodeByteArray(recvData, 0, recvLen);
        File f = new File(dirPath);
        if (f.exists()) {
            f.delete();
        }

        try {
            FileOutputStream ex = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, ex);
            ex.flush();
            ex.close();
        } catch (Exception var7) {
            LogMg.e("TcFingerDev", "saveTcFigerImage error:" + var7.getMessage());
            return -211;
        }

        LogMg.i("TcFingerDev", "saveTcFigerImage return=0");
        return 0;
    }
}
