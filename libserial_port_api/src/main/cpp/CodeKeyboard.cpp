//
// Created by andre on 2017/8/11 .
//

#include <string.h>
#include "CodeKeyboard.h"
#include "CMD.h"
#include "ToolFun.h"
#include "SerialPortHelp.h"

int CodeKeyboard::DownLoadMKey(int _fd, _ZMK m_key) {
    int len = sizeof(m_key);
    unsigned char *data = new unsigned char[len + 8];
    memset(data, 0, len + 8);
    memcpy(data, CMD::DownMKB, 8);
    data[6] = (len & 0x00ff);
    data[7] = (len >> 8);
    memcpy(data + 8, (char *) &m_key, len);
    unsigned char send[64] = {0};
    int sendlen = 0;
    ToolFun::toPackData(0x60, data, len + 8, send, &sendlen);
    delete[]data;
    int iRet = SerialPortHelp::dev_write(_fd, send, sendlen);
    if (iRet > 0) {
        unsigned char in[2048] = {0};
        iRet = SerialPortHelp::dev_read(_fd, in, 5000);
        if (iRet > 0)
            return 0;
    }
    return -1;
}

int CodeKeyboard::DownLoadWKey(int _fd, _ZWK w_key) {
    int len = sizeof(w_key);
    unsigned char *data = new unsigned char[len + 8];
    memset(data, 0, len + 8);
    memcpy(data, CMD::DownWKB, 9);
    data[6] = (len & 0x00ff);
    data[7] = (len >> 8);
    memcpy(data + 8, (char *) &w_key, len);
    unsigned char send[64] = {0};
    int sendlen = 0;
    ToolFun::toPackData(0x60, data, len + 8, send, &sendlen);
    delete[]data;
    int iRet = SerialPortHelp::dev_write(_fd, send, sendlen);
    if (iRet > 0) {
        unsigned char in[2048] = {0};
        iRet = SerialPortHelp::dev_read(_fd, in, 5000);
        if (iRet > 0)
            return 0;
    }
    return -1;
}

int CodeKeyboard::FactoryReset(int _fd, _MW_KEY r_key) {

    int len = sizeof(r_key);
    unsigned char *data = new unsigned char[len + 8];
    memset(data, 0, len + 8);
    memcpy(data, CMD::RestKB, 9);
    data[6] = (len & 0x00ff);
    data[7] = (len >> 8);
    memcpy(data + 8, (char *) &r_key, len);
    unsigned char send[64] = {0};
    int sendlen = 0;
    ToolFun::toPackData(0x60, data, len + 8, send, &sendlen);
    delete[]data;
    int iRet = SerialPortHelp::dev_write(_fd, send, sendlen);
    if (iRet > 0) {
        unsigned char in[2048] = {0};
        iRet = SerialPortHelp::dev_read(_fd, in, 1000);
        if (iRet > 0)
            return 0;
    }
    return -1;
}

int CodeKeyboard::ActiveWKey(int _fd, _ACTWKey act) {
    int len = sizeof(act);
    unsigned char *data = new unsigned char[len + 8];
    memset(data, 0, len + 8);
    memcpy(data, CMD::ActiveKB, 9);
    data[6] = (len & 0x00ff);
    data[7] = (len >> 8);
    memcpy(data + 8, (char *) &act, len);
    unsigned char send[64] = {0};
    int sendlen = 0;
    ToolFun::toPackData(0x60, data, len + 8, send, &sendlen);
    delete[]data;
    int iRet = SerialPortHelp::dev_write(_fd, send, sendlen);
    if (iRet > 0) {
        unsigned char in[2048] = {0};
        iRet = SerialPortHelp::dev_read(_fd, in, 1000);
        if (iRet > 0)
            return 0;
    }
    return -1;
}