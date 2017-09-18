//
// Created by andre on 2017/8/11 .
//

#ifndef A21_CODEKEYBOARD_H
#define A21_CODEKEYBOARD_H

//恢复密钥
typedef struct {
    unsigned char MKeyIndex = 0;
    unsigned char MKeyLength = 0;
    unsigned char MKeyTab[16] = {0};

    unsigned char WKeyIndex = 0;
    unsigned char WKeyLength = 0;
    unsigned char WKeyTab[16] = {0};
} _MW_KEY;

//主密钥
typedef struct {
    unsigned char MKeyIndex;
    unsigned char MKeyLength;
    unsigned char MKeyTab[16] = {0};//
} _ZMK;

//工作密钥
typedef struct {
    unsigned char MKeyIndex;
    unsigned char WKeyIndex;
    unsigned char WKeyLength;
    unsigned char WKeyTab[16] = {0};//
} _ZWK;

//激活密钥
typedef struct {
    unsigned char MKeyIndex;
    unsigned char WKeyIndex;
} _ACTWKey;

class CodeKeyboard {
public:
    /**
     * 恢复出厂设置
     * @param _fd 描述符
     * @param m_key 恢复密钥
     * @return 成功 0 ，失败 其他
     */
    static int FactoryReset(int _fd, _MW_KEY r_key);

    /**
     * 下载主密钥
     * @param _fd 描述符
     * @param m_key 主密钥
     * @return 成功 0 ，失败 其他
     */
    static int DownLoadMKey(int _fd, _ZMK m_key);

    /**
     * 下载工作密钥
     * @param _fd 描述符
     * @param w_key 工作密钥
     * @return 成功 0 ，失败 其他
     */
    static int DownLoadWKey(int _fd, _ZWK w_key);

    /**
     * 激活工作密钥
     * @param _fd 描述符
     * @param act 激活索引号
     * @return 成功 0 ，失败 其他
     */
    static int ActiveWKey(int _fd, _ACTWKey act);

};


#endif //A21_CODEKEYBOARD_H
