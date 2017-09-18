package com.joesmate.a21.serial_port_api;

import com.joesmate.sdk.util.ToolFun;

/**
 * Created by andre on 2017/5/23 .
 */

public class libserialport_api {
    static {
        System.loadLibrary("serial_port_api");
    }

    public static final int BT_PACKAGE = 512;

    /**
     * 打开设备
     *
     * @param path     要打开的路径
     * @param baudrate 波特率
     * @return 设备描述符
     */
    public static native int device_open(String path, int baudrate);

    /**
     * 设置波特率
     *
     * @param fd       描述符号
     * @param baudrate 波特率
     * @return
     */
    public static native int device_set_baud(int fd, int baudrate);

    /**
     * 关闭设备
     *
     * @param idecive 设备描述符
     */
    public static native void device_close(int idecive);

    /**
     * 写入数据
     *
     * @param idecive     设备描述符
     * @param OutputRepor 发送的数据
     * @param len         发送数据的长度
     * @return len 发送数据的长度
     */
    public static native int device_write(int idecive, byte[] OutputRepor, int len);

    /**
     * 读取数据（只识别FF 55 B3 的数据只保留有用数据）
     *
     * @param idecive     设备描述符
     * @param InputBuffer 返回数据
     * @param timeout     超时
     * @return 返回数据长度
     */
    public static native int device_read(int idecive, byte[] InputBuffer, long timeout);

    /**
     * 读取数据（无条件返回所有收到的数据）
     *
     * @param idecive     设备描述符
     * @param InputBuffer 返回数据(缓冲在512 以上)
     * @return 返回数据长度
     */
    public static native int device_read_all(int idecive, byte[] InputBuffer);

    /**
     * 清空输入输出缓存
     *
     * @param idevive 设备描述符
     */
    public static native void flush(int idevive);

    /**
     * 读取身份证（不带指纹）
     *
     * @param idevice  设备描述符
     * @param pucCHMsg 文字信息
     * @param pucPHMsg 头像信息
     * @param timeout  超时
     * @return 成功与否
     */
    public static native int device_ReadBaseMsg(int idevice, byte[] pucCHMsg, byte[] pucPHMsg, long timeout);

    /**
     * 读取身份证（带指纹）
     *
     * @param idevice  设备描述符
     * @param pucCHMsg 文字信息
     * @param pucPHMsg 头像信息
     * @param pucPFMsg 指纹信息
     * @param timeout  超时
     * @return 成功与否
     */
    public static native int device_ReadBaseMsgFp(int idevice, byte[] pucCHMsg, byte[] pucPHMsg, byte[] pucPFMsg, long timeout);

    /**
     * 获取ADPU
     *
     * @param fd          设备描述符
     * @param slen        ADPU 指令长度
     * @param datasend    ADPU指令
     * @param rlen        返回长度
     * @param datareceive 返回数据
     * @param timeout     超时时间
     * @return <>0 错误 =0 正确
     */
    public static native int card_ADPU(int fd, int slen, byte[] datasend, int[] rlen, byte[] datareceive, int timeout);

    /**
     * 读取身份证（不带指纹）
     *
     * @param idevice 设备描述符
     * @param pucData 全部信息
     * @param timeout 超时
     * @return 成功与否
     */
    public static native int device_ReadBaseData(int idevice, byte[] pucData, long timeout);

    /**
     * 读取身份证（带指纹）
     *
     * @param idevice 设备描述符
     * @param pucData 全部信息
     * @param timeout 超时
     * @return 成功与否
     */
    public static native int device_ReadBaseDataFp(int idevice, byte[] pucData, long timeout);

    public static native int GetAID(int icdev, byte cardno, byte[] _AIDList);

    /**
     * 初始化密码键盘
     *
     * @param m_fd 设备描述符
     * @param Mkey 主密钥
     * @param Wkey 工作密钥（用主密钥加密后的工作密钥密文）
     * @return 0 成功
     */
    public static native int RestDefaultKey(int m_fd, byte[] Mkey, byte[] Wkey);

    /**
     * 下载主密钥
     *
     * @param m_fd   设备描述符
     * @param MIndex 索引号
     * @param Mkey   主密钥
     * @return 0 成功
     */
    public static native int DownMKey(int m_fd, byte MIndex, byte MLength, byte[] Mkey);


    /**
     * 下载工作密钥
     *
     * @param m_fd    设备描述符
     * @param MIndex  索引号
     * @param WIndex
     * @param WLength
     * @param Wkey    工作密钥
     * @return 0 成功
     */
    public static native int DownWKey(int m_fd, byte MIndex, byte WIndex, byte WLength, byte[] Wkey);


    /**
     * 激活工作密钥
     *
     * @param m_fd   设备描述符
     * @param MIndex 主索引号
     * @param WIndex 工作索引号
     * @return
     */
    public static native int ActiveKey(int m_fd, byte MIndex, byte WIndex);

    /**
     * 蓝牙串口发送接口
     *
     * @param m_fd 设备描述符
     * @param buf  数据
     * @param len  数据长度
     */
    public static void device_bt_write(int m_fd, byte[] buf, int len) {
        if ((buf == null) || (len <= 0))
            return;
        if (len < BT_PACKAGE) {
            device_write(m_fd, buf, len);
        } else {
            int index, length;
            byte[] writebuf = new byte[BT_PACKAGE];
            for (index = 0; index < len; index += BT_PACKAGE) {
                length = ((index + BT_PACKAGE) < len) ? BT_PACKAGE : (len - index);
                System.arraycopy(buf, index, writebuf, 0, length);
                device_write(m_fd, writebuf, length);
                ToolFun.Dalpey(200);
            }
        }
    }

    /**
     * RAW 转 BMP
     *
     * @param raw    RAW 数据
     * @param bmp    bmp 数据
     * @param width  宽度
     * @param height 高度
     * @return 0 成功
     */
    public native static int Raw2Bmp(byte[] raw, int rawlen, byte[] bmp, int width, int height);

    /**
     *
     * @param fd 设备描述符
     * @param code 控制码 0 关闭，3 开启
     * @return 0 成功  其他失败
     */
    public native static int RF_Control(int fd,byte code);
}
