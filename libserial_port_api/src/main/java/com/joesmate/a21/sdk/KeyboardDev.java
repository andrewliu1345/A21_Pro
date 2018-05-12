package com.joesmate.a21.sdk;

import android.os.RemoteException;

import com.jl.pinpad.IRemotePinpad;
import com.joesmate.Errno;
import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

/**
 * Created by andre on 2017/8/3 .
 */

public class KeyboardDev {

    /**
     * 算法类型
     */
    public enum AlgorithmType {
        Des,// Des算法
        SM//国密算法
    }

    /**
     * 需要加密的类型
     */
    public enum EncryptData {
        all,//所有
        pwk,//pinblock 密钥
        mwk,//Mac 密钥
        tdk//磁道加密 密钥
    }

    private static final KeyboardDev mInstance = new KeyboardDev();

    private void KeyboardDev() {
    }

    GPIO gpio4 = new GPIO(4, 0);

    public static KeyboardDev getInstance() {

        return mInstance;
    }

    /**
     * 恢复密码键盘出厂密钥 ,主密钥为“8888888888888888”，工作密钥为“0000000000000000”加密后（“A17A07EB843F1C5D90BC483958A60FA5”）
     *
     * @param fd 描述符
     * @return int 0 成功 <0 失败
     */
    public int RestDefaultKey(int fd) {
        String strZMK = "8888888888888888";
        String strZPK = "0000000000000000";
        byte[] ZMK = strZMK.getBytes();
        byte[] ZPK = strZPK.getBytes();
        gpio4.Down(1);
        int iRet = libserialport_api.RestDefaultKey(fd, ZMK, ZPK);

        return 0;
    }

    /**
     * 获取明文密钥
     *
     * @param fd
     * @param timeout
     * @return
     */
    public byte[] getEnablePassword(int fd, int length, int endTpye, long timeout) {
        byte[] InputBuffer = new byte[4096];
        byte[] cmd = CMD.readkb1;
        cmd[CMD.readkb1.length - 2] = (byte) length;
        cmd[CMD.readkb1.length - 1] = (byte) endTpye;
        byte[] send = ToolFun.toPackData((byte) 0x60, cmd);
        gpio4.Down(1);
        libserialport_api.device_write(fd, send, send.length);
        int iRet = libserialport_api.device_read(fd, InputBuffer, timeout);
        if (iRet > 0) {
            int len = iRet; //InputBuffer[0]/2;
            byte[] mun = new byte[len];
            System.arraycopy(InputBuffer, 0, mun, 0, len);
            return mun;
            //   String str = ToolFun.printHexString(mun); //new String(InputBuffer, "GBK");
            // return str;


        } else {
            return null;

        }
    }

    /**
     * 获取密文密钥
     *
     * @param fd
     * @param timeout
     * @return
     */
    public byte[] getCryptPassword(int fd, int length, int endTpye, long timeout) {
        byte[] InputBuffer = new byte[4096];
        byte[] cmd = CMD.readkb2;
        cmd[CMD.readkb2.length - 2] = (byte) length;
        cmd[CMD.readkb2.length - 1] = (byte) endTpye;
        byte[] send = ToolFun.toPackData((byte) 0x60, cmd);
        gpio4.Down(1);
        libserialport_api.device_write(fd, send, send.length);
        int iRet = libserialport_api.device_read(fd, InputBuffer, timeout);
        if (iRet > 0) {
            int len = iRet; //InputBuffer[0]/2;
            byte[] mun = new byte[len];
            System.arraycopy(InputBuffer, 0, mun, 0, len);
            return mun;
        } else {
            return null;

        }
    }

    /**
     * 更新主密钥
     *
     * @param fd
     * @param ZmkIndex
     * @param ZmkLength
     * @param Zmk
     * @return
     */
    public int DownMkey(int fd, int ZmkIndex, int ZmkLength, byte[] Zmk) {
        gpio4.Down(1);
        int iRet = libserialport_api.DownMKey(fd, (byte) ZmkIndex, (byte) ZmkLength, Zmk);
        return 0;
    }

    /**
     * @param fd
     * @param MKeyIndex
     * @param WKeyIndex
     * @param WKeyLength
     * @param Key
     * @return
     */
    public int DownWkey(int fd, int MKeyIndex, int WKeyIndex, int WKeyLength, byte[] Key) {
        gpio4.Down(1);
        int iRet = libserialport_api.DownWKey(fd, (byte) MKeyIndex, (byte) WKeyIndex, (byte) WKeyLength, Key);
        return 0;
    }

    /**
     * @param fd
     * @param MKeyIndex
     * @param WKeyIndex
     * @return
     */
    public int ActiveWKey(int fd, int MKeyIndex, int WKeyIndex) {
        gpio4.Down(1);
        int iRet = libserialport_api.ActiveKey(fd, (byte) MKeyIndex, (byte) WKeyIndex);
        return 0;
    }

//    public int Init(IRemotePinpad pinpad) {
//        pinpad.init();
//    }

    public byte[] getInputPin(IRemotePinpad pinpad, int timeOutS, int amount, String lenSet, int keyId, String pan, int alg, int strTimeout) {
        byte[] pinBlock = {0};
        try {
            int iRet = pinpad.InputPin(timeOutS, amount, lenSet, keyId, pan, alg, pinBlock, alg, strTimeout);
        } catch (RemoteException e) {
            return pinBlock;
        }
        return pinBlock;
    }

    public String getInputPlainTextPin(IRemotePinpad pinpad, int timeOutS, int amount, String lenSet, int finishMode) {
        String strPin = "";
        try {
            strPin = pinpad.InputPlainTextPin(timeOutS, amount, lenSet, finishMode);
        } catch (Exception e) {
            return strPin;
        }
        return strPin;
    }

    /**
     * 明文注入初始主密钥ZMK
     *
     * @param pinpad      IRemotePinpad
     * @param ZmkIndex    密钥索引
     * @param key         密钥
     * @param CheckValues 校验
     * @param type        密钥类型 01 des密钥 02 SM密钥
     * @return 0成功，其他失败
     */
    public int LoadClearZMK(IRemotePinpad pinpad, int ZmkIndex, byte[] key, byte[] CheckValues, int type) {
        int ret = 0;

        if (key == null) {
            System.out.println("key is null");
            return Errno.ERR_KEY_IS_NULL;
        }

        if (key.length != 8 && key.length != 16) {
            System.out.println("key length invalid! should only be 8 or 16, current is:" + key.length);
            return Errno.ERR_KEY_LEN_INVALID;
        }

//		ret = Platform.getApiShadow().ddi_innerkey_inject(MK_AREA, id, key);
//		ret = StoreKey(-1, id, key, KEYTYPE_MK);
        try {
            ret = pinpad.storeTmk(key, ZmkIndex, -1, CheckValues, type);//明文注入
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = -1;
        }
        return ret;
    }

    /**
     * 明文注入主密钥
     *
     * @param pinpad IRemotePinpad
     * @param id     主密钥索引
     * @param key    主密钥
     * @param cv     校验
     * @param type   算法类型 01 des密钥 02 SM密钥
     * @return 0成功 其他失败
     */
    int LoadZMK(IRemotePinpad pinpad, int id, byte[] key, byte[] cv, int type) {
        int ret = 0;

        if (key == null) {
            System.out.println("key is null");
            return Errno.ERR_KEY_IS_NULL;
        }

        if (key.length != 8 && key.length != 16) {
            System.out.println("key length invalid! should only be 8 or 16, current is:" + key.length);
            return Errno.ERR_KEY_LEN_INVALID;
        }

        try {
            ret = pinpad.storeTmk(key, id, -1, cv, type);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = -1;
        }

        return ret;
    }

    int LoadZMK(IRemotePinpad pinpad, int dependId, int id, byte[] key, byte[] cv, int type) {
        int ret = 0;

        if (key == null) {
            System.out.println("key is null");
            return Errno.ERR_KEY_IS_NULL;
        }

        if (key.length != 8 && key.length != 16) {
            System.out.println("key length invalid! should only be 8 or 16, current is:" + key.length);
            return Errno.ERR_KEY_LEN_INVALID;
        }

        try {
            ret = pinpad.storeTmk(key, id, dependId, cv, type);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = -1;
        }

        return ret;
    }


    /**
     * 密文注入工作密钥
     *
     * @param pinpad
     * @param dependId
     * @param id
     * @param key
     * @param cv
     * @param type
     * @param encryptData
     * @return
     */
    int LoadWorkKey(IRemotePinpad pinpad, int dependId, int id, byte[] key, byte[] cv, int type, EncryptData encryptData) {
        int ret = 0;

        if (key == null) {
            System.out.println("key is null");
            return Errno.ERR_KEY_IS_NULL;
        }

        if (key.length != 8 && key.length != 16) {
            System.out.println("key length invalid! should only be 8 or 16, current is:" + key.length);
            return Errno.ERR_KEY_LEN_INVALID;
        }

        try {
            switch (encryptData) {
                case all: {
                    ret = pinpad.storePinWK(key, id, dependId, cv, type);
                    if (ret != 0)
                        return ret;
                    ret = pinpad.storeMacWK(key, id, dependId, cv, type);
                    if (ret != 0)
                        return ret;
                    ret = pinpad.storeTDK(key, id, dependId, cv, type);
                    if (ret != 0)
                        return ret;
                }
                break;
                case mwk: {
                    ret = pinpad.storeMacWK(key, id, dependId, cv, type);
                }
                break;
                case pwk: {
                    ret = pinpad.storePinWK(key, id, dependId, cv, type);
                }
                break;
                case tdk: {
                    ret = pinpad.storeTDK(key, id, dependId, cv, type);
                }
                break;
            }


        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ret = -1;
        }

        return ret;
    }

}
