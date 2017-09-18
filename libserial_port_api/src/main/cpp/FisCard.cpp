//
// Created by andre on 2017/7/5 .
//

#include <string.h>
#include "FisCard.h"
#include "SerialPortHelp.h"
#include "CMD.h"
#include "ToolFun.h"

unsigned char SELECTION_FILE_1[19] = {0x00, 0xA4, 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E,
                                      0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31};
unsigned char SELECTION_FILE_2[19] = {0x00, 0xA4, 0x04, 0x00, 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E,
                                      0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31};
unsigned char READ_AID[5] = {0x00, 0xB2, 0x01, 0x0c, 0x00};

int FisCard::ActivationICCard(int fd, unsigned char cardno, int timeout) {
    int srclen = 0;//sizeof(CMD::ReadIdInfo);
    int srclen2 = 0;
    unsigned char send[64] = {0};
    unsigned char send2[64] = {0};
    unsigned char InputReport[4096] = {0};
    switch (cardno) {
        case 0: {
            srclen = sizeof(CMD::ActiveIC_1);
            ToolFun::toPackData(0x60, CMD::ActiveIC_1, srclen, send, &srclen);
        }
            break;
        case 1: {
            srclen2 = sizeof(CMD::ActiveIC_2);
            ToolFun::toPackData(0x60, CMD::ActiveIC_2, srclen2, send2, &srclen2);
        }
            break;
        default: {
            srclen = sizeof(CMD::ActiveIC_1);
            ToolFun::toPackData(0x60, CMD::ActiveIC_1, srclen, send, &srclen);

            srclen2 = sizeof(CMD::ActiveIC_2);
            ToolFun::toPackData(0x60, CMD::ActiveIC_2, srclen2, send2, &srclen2);
        }
            break;
    }
    int iRet = 0;
    if (srclen > 0) {
        SerialPortHelp::dev_write(fd, send, srclen);
        iRet = SerialPortHelp::dev_read(fd, InputReport, timeout);
    }
    if (srclen2 > 0) {
        SerialPortHelp::dev_write(fd, send2, srclen2);
        iRet = SerialPortHelp::dev_read(fd, InputReport, timeout);
    }
    if (iRet > 0) { return 0; }
    else { return -1; }
}

int FisCard::ActivationTypeACard(int fd, int timeout) {
    int srclen = 0;//sizeof(CMD::ReadIdInfo);
    unsigned char send[64] = {0};
    unsigned char InputReport[4096] = {0};
    srclen = sizeof(CMD::ActiveNfc);
    ToolFun::toPackData(0x60, CMD::ActiveNfc, srclen, send, &srclen);
    SerialPortHelp::dev_write(fd, send, srclen);
    int iRet = SerialPortHelp::dev_read(fd, InputReport, timeout);
    if (iRet > 0) { return 0; }
    else { return -1; }
}

int FisCard::GetRATS(int fd, int timeout) {
    int srclen = 0;//sizeof(CMD::ReadIdInfo);
    unsigned char send[64] = {0};
    unsigned char InputReport[4096] = {0};
    srclen = sizeof(CMD::GetRats);
    ToolFun::toPackData(0x60, CMD::GetRats, srclen, send, &srclen);
    SerialPortHelp::dev_write(fd, send, srclen);
    int iRet = SerialPortHelp::dev_read(fd, InputReport, timeout);
    if (iRet > 0) { return 0; }
    else { return -1; }
}

int FisCard::GetAID(int icdev, unsigned char cardno, char *_AIDList) {
    //选择应用1PAY.SYS.DDF01
    int retlen1 = 0;
    unsigned char ret1[64] = {0};
    unsigned char selectionFile[19] = {0};
    if (cardno == '0')
        memcpy(selectionFile, SELECTION_FILE_1, 19);
    else
        memcpy(selectionFile, SELECTION_FILE_2, 19);
    int st = FisCard::card_ADPU(icdev, 19, selectionFile, &retlen1, ret1, 5000);
    if (st != 0) {
        return -1;
    }
    //
    // 	//61xx处理
    // 	unsigned char send61[5] = { 0x00,0xC0,0x00,0x00};
    // 	send61[4] = ret1[1];
    // 	unsigned char ret2[25] = { 0 };
    // 	int st = card_APDU(icdev, cardno, 5, send61, &retlen1, ret2);

    //读取记录
    int retlen = 0;
    unsigned char rettmp[64] = {0};
    st = FisCard::card_ADPU(icdev, 5, READ_AID, &retlen, rettmp, 5000);
    if (st != 0) {
        return -1;
    }
    memcpy(_AIDList, rettmp + 7, 8);
    return 0;
}

int FisCard::card_ADPU(int fd, int slen, unsigned char *datasend, int *rlen,
                       unsigned char *datareceive, int timeout) {
    int srclen = slen + 8;
    unsigned char *adpu = new unsigned char[srclen];
    unsigned char InputReport[4096] = {0};
    memset(adpu, 0, srclen);
    memcpy(adpu, CMD::ADPU, 8);
    adpu[6] = slen & 0xFF;
    adpu[7] = (slen >> 8) & 0xFF;
    memcpy(adpu + 8, datasend, slen);
    unsigned char send[64] = {0};
    ToolFun::toPackData(0x60, adpu, srclen, send, &srclen);
    delete[]adpu;
    SerialPortHelp::dev_write(fd, send, srclen);
    int iRet = SerialPortHelp::dev_read(fd, InputReport, timeout);
    if (iRet > 0) {
        *rlen = iRet;
        memcpy(datareceive, InputReport, iRet);
        return 0;
    } else { return -1; }
}