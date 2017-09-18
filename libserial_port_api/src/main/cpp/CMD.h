//
// Created by andre on 2017/6/1 .
//

#ifndef MYAPPLICATION_CMD_H
#define MYAPPLICATION_CMD_H


class CMD {
public:
    /**
     * 读序列号
     */
    static unsigned char ReadSnr[8];

    /**
     * 激活IC卡-1
     */
    static unsigned char ActiveIC_1[8];

    /**
     * 激活IC卡-2
     */
    static unsigned char ActiveIC_2[8];

    /**
     * 激活A卡
     */
    static unsigned char ActiveNfc[8];

    /**
     * 激活B卡
     */
    static unsigned char ActiveTypeB[8];

    /**
     * 获取RATS
     */
    static unsigned char GetRats[8];

    static unsigned char ADPU[8];

    /**
     * 读身份证信息（无指纹）
     */
    static unsigned char ReadIdInfo[8];

    /**
     *指纹特征
     */
    static unsigned char FpFeaturesData[16];

    /**
     * 恢复密钥
     */
    static unsigned char RestKB[8];

    /**
     * 下载主密钥
     */
    static unsigned char DownMKB[8];

    /**
     * 下载工作密钥
    */
    static unsigned char DownWKB[8];

    /**
     * 激活工作密钥
     */
    static unsigned char ActiveKB[8];

    /**
     * 射频控制
     */
    static unsigned char RFControl[9];
};


#endif //MYAPPLICATION_CMD_H
