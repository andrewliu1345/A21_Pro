//
// Created by andre on 2017/5/23 .
//
#include "SerialPortHelp.h"
#include <android/log.h>
#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include "ToolFun.h"


#define FRAME_MAXSIZE        0x40000
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  SerialPortHelp::TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, SerialPortHelp::TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, SerialPortHelp::TAG, fmt, ##args)

const char *SerialPortHelp::TAG = "serial_port_help";

bool SerialPortHelp::isCancel = false;

static speed_t getBaudrate(int baudrate) {
    switch (baudrate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return -1;
    }
}

int SerialPortHelp::dev_open(const char *path, int baudrate, int flags) {
    int fd = false;
//    speed_t speed;
//    speed = getBaudrate(baudrate);
//    if (speed == -1) {
//        /* TODO: throw an exception */
//        LOGE("Invalid baudrate");
//        return -1;
//    }
    LOGD("Opening serial port %s with flags 0x%x", path, O_RDWR | flags);
    fd = open(path, O_RDWR | flags);
    LOGD("open() fd = %d", fd);
    if (fd < 0) {
        /* Throw an exception */
        LOGE("Cannot open port");
        /* TODO: throw an exception */
        return fd;
    }
    int opt = SerialPortHelp::set_opt(fd, baudrate, 8, 'N', 1);
    if (opt < 0) {
        close(fd);
        return opt;
    }


//    struct termios cfg;
//    LOGD("Configuring serial port");
//    if (tcgetattr(fd, &cfg)) {
//        LOGE("tcgetattr() failed");
//        close(fd);
//        /* TODO: throw an exception */
//        return -1;
//    }
//
//    cfmakeraw(&cfg);
//    cfsetispeed(&cfg, speed);
//    cfsetospeed(&cfg, speed);
////    cfg.c_cflag &= ~CRTSCTS;//不使用流控制
////    //无奇偶校验位。
////    cfg.c_cflag &= ~PARENB;
////    cfg.c_iflag &= ~INPCK;
////    //修改输出模式，原始数据输出
////    cfg.c_oflag &= ~OPOST;
//    tcflush(fd, TCIFLUSH);
//    if (tcsetattr(fd, TCSANOW, &cfg)) {
//        LOGE("tcsetattr() failed");
//        close(fd);
//        /* TODO: throw an exception */
//        return -1;
//    }
    return fd;

}

void SerialPortHelp::dev_close(int fd) {
    LOGD("close(fd = %d)", fd);
    close(fd);
}

int SerialPortHelp::dev_write(int device, unsigned char *OutputRepor, int len) {
    std::string s = ToolFun::bytesToHexstring(OutputRepor, len);
    LOGD("dev_write len=%d |OutputRepor=%s ", len, s.c_str());
//    //delete s;

    int data_len = 0;
    //tcflush(device, TCIOFLUSH);
    data_len = write(device, OutputRepor, len);
    if (len == data_len) {
        LOGD("dev_write data_len=%d", data_len);
        return data_len;
    } else {
        tcflush(device, TCOFLUSH);
        LOGE("dev_write 写入失败");
        return false;
    }
    //return write(device, OutputRepor, len);
}

int SerialPortHelp::dev_read(int device, unsigned char *InputBuffer, long timeout) {
    isCancel = false;
    // int Success = false;
    int len = 0;
    bool havedata = false;
    unsigned long start_time = ToolFun::GetTickCount();
    unsigned long end_time = ToolFun::GetTickCount();
    unsigned long Subtime = 0;
    int bRead = 0;
    unsigned char InputReport[2000] = {0};
    unsigned char tmpbuffer[4096] = {0};
    int tmplen = 0;
    int pos = 0;
    int reportlen = 0;
    while (true) {
        end_time = ToolFun::GetTickCount();
        Subtime = end_time - start_time;
        LOGD("end_time=%ld start_time=%ld", end_time, start_time);
        if (Subtime > timeout) {
            return -1;
        }
        if (isCancel)
            return -1;
        usleep(10);
        bRead = read(device, InputReport, 2000);
        // Success = libusb_interrupt_transfer((libusb_device_handle *)device, (0x81 | LIBUSB_ENDPOINT_IN), InputReport, 65, &bRead, 1000);
        //Success = ReadFile(device, InputReport, Capabilities.InputReportByteLength, &bRead, NULL);
        if (bRead == 0) {
            usleep(10);
            continue;
        }
        std::string s = ToolFun::bytesToHexstring(InputReport, bRead);
        LOGD("bRead=%d 返回信息:%s \nInputReport=%s", bRead, InputReport, s.c_str()
        );
        //delete s;;
        //elete[] s;
        ToolFun::FindStart(InputReport, 2000, &pos);
        LOGD("pos=%d", pos);
        if (InputReport[pos] == 0xff && InputReport[pos + 1] == 0x55 &&
            InputReport[pos + 4] == 0x60 && InputReport[pos + 5] == 0xb3) {
            LOGD("有效数据");
            //printf("bRead=%d InputReport=%s \n", bRead, ToolFun::bytesToHexstring((char *)InputReport, bRead));
            //cout << "bRead=" << bRead << " InputReport=" << ToolFun::bytesToHexstring((char *)InputReport, bRead) << endl;

            reportlen = (0xffffffff & (InputReport[pos + 2] << 8)) +
                        (0xffffffff & InputReport[pos + 3]);
            LOGD("reportlen=%d", reportlen);
            if (reportlen > 1) {
                havedata = true;
                memcpy(tmpbuffer, InputReport + pos + 5, reportlen - 1);
                tmplen = reportlen - 11;
                string s = ToolFun::bytesToHexstring(tmpbuffer, reportlen - 1);
                LOGD("tmplen=%d InputReport=%s", tmplen, s.c_str()
                );
                //delete s;
                break;
            }


        }

    }
    LOGD("*havedata=%d ", havedata);
    if (havedata) {
        len = (0xffffffff & (tmpbuffer[7] << 8)) +
              (0xffffffff & tmpbuffer[6]);
        LOGD("tmpbuffer[6]=%d,tmpbuffer[7]=%d", 0xffffffff & (tmpbuffer[7] << 8),
             0xffffffff & tmpbuffer[6]);
        LOGD("*len=%d  tmplen=%d", len, tmplen);
        while (true) {
            if (tmplen > len) {
                return -1;
            }
            if (tmplen == len) {
                memcpy(InputBuffer, tmpbuffer + 8, tmplen);
                return tmplen;
            }
            pos = 0;
            memset(InputReport, 0, 64);
            bRead = read(device, InputReport, 2000);
            //Success = libusb_interrupt_transfer((libusb_device_handle *)device, (0x81 | LIBUSB_ENDPOINT_IN), InputReport, 64, &bRead, 1000);
            //Success = ReadFile(device, InputReport, Capabilities.InputReportByteLength, &bRead, NULL);
            ToolFun::FindStart(InputReport, 64, &pos);
            if (InputReport[pos] == 0xff && InputReport[pos + 1] == 0x55 &&
                InputReport[pos + 4] == 0x60) {
                // printf("bRead=%d InputReport=%s \n", bRead, ToolFun::bytesToHexstring((char *)InputReport, bRead));
                reportlen = (0xffffffff & (InputReport[pos + 2] << 8)) +
                            (InputReport[pos + 3] & 0xffffffff);
                memcpy(tmpbuffer + tmplen, InputReport + pos + 5, reportlen - 1);
                tmplen += reportlen - 1;

            }
            end_time = ToolFun::GetTickCount();
            Subtime = end_time - start_time;
            if (Subtime > timeout) {
                LOGD("templen=%d, len=%d", tmplen, len);
                return -1;
            }
        }
    }
    return -1;
}

int SerialPortHelp::dev_read_all(int device, unsigned char *InputBuffer) {
    unsigned char InputReport[2000] = {0};
    int bRead = read(device, InputReport, 2000);
    string s = ToolFun::bytesToHexstring(InputReport, bRead);

    LOGD("bRead=%d 返回信息:%s \nInputReport=%s", bRead, InputReport, s.c_str()
    );
    //delete s;
    memcpy(InputBuffer, InputReport, bRead);
    return bRead;
}

int SerialPortHelp::set_opt(int fd, int nSpeed, int nBits, char nEvent, int nStop) {
    LOGD("fd=%d nSpeed=%d", fd, nSpeed);
    struct termios newtio;
    struct termios oldtio;
    if (tcgetattr(fd, &oldtio)) {

        LOGE("SetupSerial 1");
        return -1;
    }

    bzero(&newtio, sizeof(newtio));
    //使用原始模式(Raw Mode)方式来通讯

    newtio.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);  /*Input*/
    newtio.c_oflag &= ~OPOST;   /*Output*/

    newtio.c_cflag |= CLOCAL | CREAD;
    newtio.c_cflag &= ~CSIZE;

/***********数据位选择****************/
    switch (nBits) {
        case 7:
            newtio.c_cflag |= CS7;
            break;
        case 8:
            newtio.c_cflag |= CS8;
            break;
    }
/***********校验位选择****************/
    switch (nEvent) {
        case 'n':
        case 'N':
            newtio.c_cflag &= ~PARENB;   /* Clear parity enable */
            newtio.c_iflag &= ~INPCK;     /* Enable parity checking */
            break;
        case 'o':
        case 'O':
            newtio.c_cflag |= (PARODD | PARENB); /* 设置为奇效验*/
            newtio.c_iflag |= INPCK;             /* Disnable parity checking */
            break;
        case 'e':
        case 'E':
            newtio.c_cflag |= PARENB;     /* Enable parity */
            newtio.c_cflag &= ~PARODD;   /* 转换为偶效验*/
            newtio.c_iflag |= INPCK;       /* Disnable parity checking */
            break;
        case 'S':
        case 's':  /*as no parity*/
            newtio.c_cflag &= ~PARENB;
            newtio.c_cflag &= ~CSTOPB;
            break;
        default:
            close(fd);
            LOGE("Unsupported parity\n");
            return false;
    }
/***********波特率选择****************/
    speed_t sp = getBaudrate(nSpeed);
    if (sp == -1) {
        /* TODO: throw an exception */
        LOGE("Invalid baudrate");
        return -1;
    }
    cfsetispeed(&newtio, sp);
    cfsetospeed(&newtio, sp);
/***********停止位选择****************/
    if (nStop == 1) {
        newtio.c_cflag &= ~CSTOPB;
    } else if (nStop == 2) {
        newtio.c_cflag |= CSTOPB;
    }
    newtio.c_cc[VTIME] = 1;
    newtio.c_cc[VMIN] = (cc_t) FRAME_MAXSIZE;   //阻塞条件下有效


    tcflush(fd, TCIOFLUSH);
    if (tcsetattr(fd, TCSANOW, &newtio)) {

        LOGE("com set error..............");
        return -1;
    }
    LOGD("set done!\n");
    return 0;
}

void SerialPortHelp::flush(int fd) {
    tcflush(fd, TCIOFLUSH);
    unsigned char out[4096] = {0};
    while (true) {
        int iRet = SerialPortHelp::dev_read_all(fd, out);
        if (iRet <= 0)
            return;
    }
}

void SerialPortHelp::Cancel() {
    isCancel = true;
}