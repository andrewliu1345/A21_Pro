//
// Created by andre on 2017/5/22 .
//
#include <jni.h>
#include <string>

#ifndef MYAPPLICATION_TOOLFUN_H
#define MYAPPLICATION_TOOLFUN_H


class ToolFun {
public:
    static int Sizeof(unsigned char *p);

    static jstring CStr2Jstring(JNIEnv *env, const char *pat);

    static long GetTickCount(void);

    static void FindStart(unsigned char *data, int length, int *pos);

    static std::string bytesToHexstring(unsigned char *bytes, int bytelength);

    /***
     * 数据组包
     * @param msgtype 消息类型 60 为数据，70为调试信息
     * @param src 原数据
      * @param srclen 原数据长度
     * @param dest 组包后的数据
     * @param destlen 组包后的数据长度
     * @return 0为成功，其他为失败
     */
    static int
    toPackData(unsigned char msgtype, unsigned char *src, int srclen, unsigned char *dest,
               int *destlen);


    /***
     * 加法校验
     * @param data  输入数组
     * @param len  输入数组长度
     * @param crc 双字节校验值
     * @return 0 成功，其他失败
     */
    static int getCheckSumCRC(unsigned char *data, int len, unsigned char crc[2]);

    /***
     *
     * @param data
     * @param len
     * @param crc
     * @return
     */
    static int getCheckSubCRC(unsigned char *data, int len, unsigned char crc[1]);

    /***
     * 转双字节
     * @param a 数字
     * @param arr 双字节
     */
    static void intToByteArray(int a, unsigned char arr[2]);

    /**
     * RAW 转换成 BMP
     * @param pImage
     * @param pBmp
     * @param iWidth
     * @param iHeight
     * @return
     */
    static int  RAW2BMP(unsigned char *pImage, unsigned char *pBmp, int iWidth, int iHeight);
};


#endif //MYAPPLICATION_TOOLFUN_H
