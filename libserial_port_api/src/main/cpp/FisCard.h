//
// Created by andre on 2017/7/5 .
//

#ifndef A21_FISCARD_H
#define A21_FISCARD_H


class FisCard {
public:
    /**
     * 激活IC卡
     * @param fd  串口句柄
     * @param cardno 卡曹 0：卡曹1, 1:卡槽2, 其他自动查找
     * @param timeout 超时
     * @return <>0 错误 =0 正确
     */
    static int ActivationICCard(int fd, unsigned char cardno, int timeout);

    /**
     * 激活A卡
     * @param fd 串口句柄
     * @param timeout 超时
     * @return  <>0 错误 =0 正确
     */
    static int ActivationTypeACard(int fd, int timeout);

    /**
     * 获取RATS
     * @param fd 串口句柄
     * @param timeout 超时
     * @return  <>0 错误 =0 正确
     */
    static int GetRATS(int fd, int timeout);


    /**
__int16 st;
unsigned short rlen;
unsigned char datarecv[300];
memset(datarecv,0,300);
unsigned char databuff[]={0x00,0x84,0x00,0x00,0x08,0}; //取随机数
st=card_APDU(icdev,5,databuff,&rlen,datarecv，300);
     */


    /**
     * 发送adpu
     * @param fd 通讯设备标识符
     * @param slen 发送数据的字节长度
     * @param datasend 要发送的数据
     * @param rlen 返回的数据长度
     * @param datareceive 返回的指令应答信息
     * @param timeout 超时
     * @return <>0 错误 =0 正确
     */
    static int card_ADPU(int fd, int slen, unsigned char *datasend, int *
    rlen, unsigned char *datareceive,int timeout);

    /**
     *
     * @param icdev
     * @param cardno
     * @param _AIDList
     * @return
     */
    static int GetAID(int icdev,unsigned char cardno, char * _AIDList);
};


#endif //A21_FISCARD_H
