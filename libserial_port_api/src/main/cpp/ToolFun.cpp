//
// Created by andre on 2017/5/22 .
//

#include "ToolFun.h"
#include <sys/time.h>
#include <stdio.h>
#include <string.h>

jstring ToolFun::CStr2Jstring(JNIEnv *env, const char *pat) {
    // 定义java String类 strClass
    jclass strClass = env->FindClass("java/lang/String;");
    // 获取java String类方法String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = env->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    // 建立byte数组
    jbyteArray bytes = env->NewByteArray((jsize) strlen(pat));
    // 将char* 转换为byte数组
    env->SetByteArrayRegion(bytes, 0, (jsize) strlen(pat), (jbyte *) pat);
    //设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = env->NewStringUTF("utf-8");
    //将byte数组转换为java String,并输出
    return (jstring) env->NewObject(strClass, ctorID, bytes, encoding);
}

int ToolFun::Sizeof(unsigned char *p) {


    int n = 0;
    while (p++) n++;
    p -= n + 1;
    return n;
}

long ToolFun::GetTickCount() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

void ToolFun::FindStart(unsigned char *data, int length, int *pos) {
    for (int i = 0; i < length; i++) {
        if (data[i] == 0xff && data[i + 1] == 0x55) {
            *pos = i;
            return;
        }
    }
    *pos = 0;
}

//char *ToolFun::bytesToHexstring(unsigned char *bytes, int bytelength) {
//    char hexstring[65535] = {0};
//    if (bytelength * 2 > 65535) { bytelength = 65535 / 2; }
//    // memset(hexstring, 0, bytelength * 2);
//    unsigned char str2[16] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
//                              'E', 'F'};
//    for (int i = 0, j = 0; i < bytelength && j < bytelength * 2; i++, j++) {
//        int b;
//        b = 0x0f & (bytes[i] >> 4);
//        char s1 = str2[b];
//        hexstring[j] = s1;
//        b = 0x0f & bytes[i];
//        char s2 = str2[b];
//        j++;
//        hexstring[j] = s2;
//        j++;
//        hexstring[j] = ' ';
//    }
//    return hexstring;
//}

std::string ToolFun::bytesToHexstring(unsigned char *bytes, int bytelength) {
    std::string hexstr;
    for (int i = 0; i < bytelength; i++) {
        char hex1;
        char hex2;
        int value = bytes[i];
        int v1 = value / 16;
        int v2 = value % 16;
        if (v1 >= 0 && v1 <= 9)
            hex1 = (char) (48 + v1);
        else
            hex1 = (char) (55 + v1);
        if (v2 >= 0 && v2 <= 9)
            hex2 = (char) (48 + v2);
        else
            hex2 = (char) (55 + v2);

        hexstr = hexstr + hex1 + hex2 + " ";
    }
    return hexstr;
}

static unsigned char handdata[2] = {0xFF, 0x55};

int ToolFun::toPackData(unsigned char msgtype, unsigned char *src, int srclen, unsigned char *dest,
                        int *destlen) {
    *destlen = srclen + 6 + 2;
    unsigned char *senddata = new unsigned char[srclen + 6 + 2];//头+长度+CommdID+数据+CRC+CRC
    unsigned char *tempdata = new unsigned char[srclen + 5];//长度+CommdID+数据+CRC
    unsigned char lengthdata[2] = {0};
    intToByteArray(srclen + 3, lengthdata);//长度字段
    unsigned char sumcrc[2] = {0};
    getCheckSumCRC(src, srclen, sumcrc);//数据校验


    memcpy(senddata, handdata, 2); //System.arraycopy(handdata, 0, senddata, 0, handdata.length);//头
    memcpy(tempdata, lengthdata,
           2);//System.arraycopy(lengthdata, 0, tempdata, 0, lengthdata.length);//长度
    tempdata[2] = msgtype;//CommdID
    memcpy(tempdata + 2 + 1, src,
           srclen);//System.arraycopy(data, 0, tempdata, lengthdata.length + 1, data.length);//数据
    memcpy(tempdata + 2 + 1 + srclen, sumcrc,
           2);//System.arraycopy(sumcrc, 0, tempdata, lengthdata.length + 1 + data.length, 2);//数据校验

    memcpy(senddata + 2, tempdata,
           srclen + 5);//System.arraycopy(tempdata, 0, senddata, handdata.length, tempdata.length);
    unsigned char subcrc[1] = {0};
    getCheckSubCRC(tempdata, srclen + 5, subcrc);
    senddata[0 + 2 + 2 + 1 + srclen + 2] = subcrc[0];//数据包效验
    memcpy(dest, senddata, *destlen);
    delete[] senddata;
    delete[]tempdata;
    return 0;
}

int ToolFun::getCheckSumCRC(unsigned char *data, int len, unsigned char *crc) {
    short sub = 0;
    for (int i = 0; i < len; ++i) {
        sub += data[i] & 0xFF;
    }
    intToByteArray(~sub, crc);
    return 0;
}

int ToolFun::getCheckSubCRC(unsigned char *data, int len, unsigned char *crc) {
    unsigned char sub = data[0];
    for (int i = 1; i < len; i++) {
        sub -= data[i] & 0xff;
    }
    crc[0] = sub;
    return 0;
}

void ToolFun::intToByteArray(int a, unsigned char *arr) {
    arr[0] = (a >> 8) & 0xFF;
    arr[1] = a & 0xff;
}

int ToolFun::RAW2BMP(unsigned char *pImage, unsigned char *pBmp, int iWidth, int iHeight) {
    unsigned int num;
    int i, j;
    unsigned char head[1078] = {
            /***************************/
            //file header
            0x42, 0x4d,//file type
            //0x36,0x6c,0x01,0x00, //file size***
            0x0, 0x0, 0x0, 0x00, //file size***
            0x00, 0x00, //reserved
            0x00, 0x00,//reserved
            0x36, 0x4, 0x00, 0x00,//head byte***
            /***************************/
            //infoheader
            0x28, 0x00, 0x00, 0x00,//struct size

            //0x00,0x01,0x00,0x00,//map width***
            0x00, 0x00, 0x00, 0x00,//map width***
            //0x68,0x01,0x00,0x00,//map height***
            0x00, 0x00, 0x00, 0x00,//map height***

            0x01, 0x00,//must be 1
            0x08, 0x00,//color count***
            0x00, 0x00, 0x00, 0x00, //compression
            //0x00,0x68,0x01,0x00,//data size***
            0x00, 0x00, 0x00, 0x00,//data size***
            0x00, 0x00, 0x00, 0x00, //dpix
            0x00, 0x00, 0x00, 0x00, //dpiy
            0x00, 0x00, 0x00, 0x00,//color used
            0x00, 0x00, 0x00, 0x00,//color important

    };

    //确定图象宽度数值
    num = iWidth;
    head[18] = num & 0xFF;
    num = num >> 8;
    head[19] = num & 0xFF;
    num = num >> 8;
    head[20] = num & 0xFF;
    num = num >> 8;
    head[21] = num & 0xFF;
    //确定图象高度数值
    num = iHeight;
    head[22] = num & 0xFF;
    num = num >> 8;
    head[23] = num & 0xFF;
    num = num >> 8;
    head[24] = num & 0xFF;
    num = num >> 8;
    head[25] = num & 0xFF;

    //确定调色板数值
    j = 0;
    for (i = 54; i < 1078; i = i + 4) {
        head[i] = head[i + 1] = head[i + 2] = j;
        head[i + 3] = 0;
        j++;
    }

    // 写BMP头
    memcpy(pBmp, head, 1078 * sizeof(char));

    //写入图象数据
    for (i = 0; i < iHeight; i++) {
        int pos = 1078 * sizeof(char) + (iHeight - 1 - i) * iWidth;
        memcpy(&pBmp[pos], &pImage[i * iWidth], iWidth * sizeof(char));
    }

//    // 保存图像
//    FILE *fp;
//    fp = fopen("/sdcard/finger.bmp", "wb");
//    if (fp == NULL)
//        return 0;
//    fseek(fp, 0, SEEK_SET);
//    fwrite(pBmp, (1078 + iWidth * iHeight) * sizeof(char), 1, fp);
//    fclose(fp);

    return 0;
}