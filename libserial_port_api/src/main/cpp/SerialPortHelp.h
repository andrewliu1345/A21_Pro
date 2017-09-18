//
// Created by andre on 2017/5/23 .
//
#include <string>

using std::string;
#ifndef MYAPPLICATION_SERIALPORTHELP_H
#define MYAPPLICATION_SERIALPORTHELP_H


class SerialPortHelp {
private:
    static const char *TAG;

public:
    /***
     * 打开设备
     * @param path 串口路径
     * @param baudrate 波特率
     * @param flags 打开类型
     * @return fd
     */
    static int dev_open(const char *path, int baudrate, int flags);

    /***
     * 关闭串口
     * @param fd
     */
    static void dev_close(int fd);

    /***
     *写数据
     * @param device 设备fd
     * @param OutputRepor 发送数据
     * @param len 发送数据长度
     * @return 成功0,失败-1
     */
    static int dev_write(int device, unsigned char *OutputRepor, int len);

    /***
     *读数据
     * @param device 设备fd
     * @param InputBuffer 返回数据
     * @param timeout 超时
     * @return 数据长度
     */
    static int dev_read(int device, unsigned char *InputBuffer, long timeout);

    /***
     * 读取所有串口数据
     * @param device fd
     * @param InputBuffer 返回数据
     * @return 数据长度
     */
    static int dev_read_all(int device, unsigned char *InputBuffer);

    /***
     * 设置串口
     * @param fd  打开串口的文件句柄
     * @param nSpeed 串口速度
     * @param nBits 数据位   取值 为 7 或者8   数据位为7位或8位
     * @param nEvent 效验类型 取值为N,E,O,,S     N->无奇偶校验，O->奇校验 E->为偶校
     * @param nStop 停止位   取值为 1 或者2*    停止位为1或2位
     * @return
     */
    static int set_opt(int fd, int nSpeed, int nBits, char nEvent, int nStop);

    /**
     * 清除缓存
     * @param fd  打开文件描述符
     */
    static void flush(int fd);

};


#endif //MYAPPLICATION_SERIALPORTHELP_H
