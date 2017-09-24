package com.joesmate.a21.sdk;

import android.util.Log;

import com.a21.Hal;
import com.emv.CoreLogic;
import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.LogMg;
import com.joesmate.sdk.util.ToolFun;

/**
 * Created by andre on 2017/8/3 .
 */

public class PBOC {
    private static final PBOC mInstance = new PBOC();

    private void PBOC() {

    }

    static GPIO gpio4 = new GPIO(4, 0);
    static GPIO gpio12 = new GPIO(12, 0);

    public static PBOC getInstance() {
        gpio4.Down(1);
        gpio12.Up(1);
        return mInstance;
    }

    public String[] GetICCInfo(int fd, int ICType, String AIDList, String TagList, long Timeout) {
        libserialport_api.RF_Control(fd, (byte) 0x03);
        ToolFun.Dalpey(200);
        String[] aryRet = null;
        int iRet = Hal.initHal(fd);
        if (iRet == 0)
            iRet = FindCard(ICType, fd);
        //ToolFun.Dalpey(200);
        if (iRet != -1) {
//            ICType=iRet;
//            String aidlist = AIDList;
//            if (AIDList == null || AIDList.equals("")) {
//                byte[] aid = new byte[1024];
//                iRet = libserialport_api.GetAID(fd, (byte) ICType, aid);
//                if (iRet == 0)
//                    aidlist = new String(aid);
//
//            }
            ICType = iRet;
            if (AIDList == null || AIDList.equals(""))
                AIDList = "A000000333";
            aryRet = CoreLogic.GetICCInfo((byte) ICType, AIDList, TagList, Long.toString(Timeout));
        }
        libserialport_api.RF_Control(fd, (byte) 0x00);
        gpio4.Up(1);
        return aryRet;
    }

    public String[] GetICCArqc(int fd, int ICType, String TxData, String aidlist, long strTimeout) {
        libserialport_api.RF_Control(fd, (byte) 0x03);
        ToolFun.Dalpey(200);
        String[] aryRet = null;
        int iRet = Hal.initHal(fd);
        if (iRet == 0)
            iRet = FindCard(ICType, fd);
        if (iRet != -1) {
            ICType = iRet;
            // if (aidlist == null || aidlist.equals(""))
            aidlist = "A000000333";
            LogMg.d("A21-PBOC", "GetICCArqc--TxData=%s aidlist=%s", TxData, aidlist);
            aryRet = CoreLogic.GetICCArqc((byte) ICType, TxData, aidlist, Long.toString(strTimeout));
        }
        libserialport_api.RF_Control(fd, (byte) 0x00);
        gpio4.Up(1);
        return aryRet;
    }

    public String[] ARPCExeScript(int fd, int ICType, String Txdata, String ARPC, String CDol2) {
        libserialport_api.RF_Control(fd, (byte) 0x03);
        ToolFun.Dalpey(200);
        String[] aryRet = null;
        int iRet = Hal.initHal(fd);
        if (iRet == 0)
            iRet = FindCard(ICType, fd);
        if (iRet != -1) {
            ICType = iRet;
            LogMg.d("A21-PBOC", "ARPCExeScript--Txdata=%s ARPC=%s", Txdata, ARPC);
            aryRet = CoreLogic.ARPCExcScripts((byte) ICType, Txdata, ARPC, CDol2);
        }
        libserialport_api.RF_Control(fd, (byte) 0x00);
        gpio4.Up(1);
        return aryRet;


    }

    public String[] GetTrDetail(int fd, int ICType, int NOLog, long strTimeout) {
        libserialport_api.RF_Control(fd, (byte) 0x03);
        ToolFun.Dalpey(200);
        String[] aryRet = null;
        int iRet = Hal.initHal(fd);
        if (iRet == 0)
            iRet = FindCard(ICType, fd);
        if (iRet != -1) {
            ICType = iRet;
            aryRet = CoreLogic.GetICCTRXDetails((byte) ICType, NOLog, Long.toString(strTimeout));
        }
        gpio4.Up(1);
        return aryRet;
    }

    public String[] GetLoadLog(int fd, int ICType, int NOLog, String AIDList, long strTimeout) {
        libserialport_api.RF_Control(fd, (byte) 0x03);
        ToolFun.Dalpey(200);
        String[] aryRet = null;
        int iRet = Hal.initHal(fd);
        if (iRet == 0)
            iRet = FindCard(ICType, fd);
        if (iRet != -1) {
            ICType = iRet;
            if (AIDList == null || AIDList.equals(""))
                AIDList = "A000000333";
            aryRet = CoreLogic.GetICCLoadDetails((byte) ICType, NOLog, AIDList, Long.toString(strTimeout));
        }
        libserialport_api.RF_Control(fd, (byte) 0x00);
        gpio4.Up(1);
        return aryRet;
    }

    public String[] GetICAndARQCInfo(int fd, int ICType, String AIDList, String TagList, String TxData, long strTimeout) {
        libserialport_api.RF_Control(fd, (byte) 0x03);
        ToolFun.Dalpey(600);
        long start = System.currentTimeMillis();
        String[] aryRet = new String[4];
        int iRet = Hal.initHal(fd);
        if (iRet == 0)
            iRet = FindCard(ICType, fd);
        if (iRet != -1) {
            ICType = iRet;
            if (AIDList == null || AIDList.equals(""))
                AIDList = "A000000333";
            String[] aryRet1 = CoreLogic.GetICCArqc((byte) ICType, TxData, AIDList, Long.toString(strTimeout));
            strTimeout = System.currentTimeMillis() - start;
            String[] aryRet2 = CoreLogic.GetICCInfo((byte) ICType, AIDList, TagList, Long.toString(strTimeout));
            if (aryRet1[0] == "0" && aryRet2[0] == "0") {
                aryRet[0] = aryRet1[0];
                aryRet[1] = aryRet1[1];
                aryRet[2] = aryRet2[1];
                aryRet[3] = aryRet2[2];
            }
        }
        libserialport_api.RF_Control(fd, (byte) 0x00);
        gpio4.Up(1);
        return aryRet;
    }

    /**
     * 找卡
     *
     * @param IcType 找卡模式：0-IC卡接触卡座；1-IC卡非接触卡座；2-自动；
     * @return 找到卡的类型
     */
    private int FindCard(int IcType, int fd) {
        int iRet = -1;
        switch (IcType) {
            case 0x00:
                iRet = Hal.SmartCardActive7816_1();
                if (iRet == 0)
                    return 0;
            case 0x01:
                iRet = ActiveTypeA(fd);
                if (iRet == 0) {
                    iRet = Hal.PiccActivteA();
                    if (iRet == 0)
                        return 1;
                }
                iRet = ActiveTypeB(fd);
                if (iRet == 0) {
                    iRet = Hal.PiccActivteB();
                    if (iRet == 0)
                        return 1;
                }
            case 0x02:
                iRet = Hal.SmartCardActive7816_1();
                if (iRet == 0)
                    return 0;
                iRet = ActiveTypeA(fd);
                if (iRet == 0) {
                    iRet = Hal.PiccActivteA();
                    if (iRet == 0)
                        return 1;
                }
                iRet = ActiveTypeB(fd);
                if (iRet == 0) {
                    iRet = Hal.PiccActivteB();
                    if (iRet == 0)
                        return 1;
                }
            default:
                return -1;

        }
    }

    private int ActiveTypeA(int _fd) {
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活IC卡
            byte[] in = new byte[4096];
            iRet = libserialport_api.device_read(_fd, in, 1000);
            if (iRet > 0) {
                return 0;
            }
        }
        return -1;
    }

    private int ActiveTypeB(int _fd) {
        byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activeid);
        int iRet = libserialport_api.device_write(_fd, send, send.length);
        if (iRet > 0) {//激活IC卡
            byte[] in = new byte[4096];
            iRet = libserialport_api.device_read(_fd, in, 1000);
            if (iRet > 0) {
                return 0;
            }
        }
        return -1;
    }
}
