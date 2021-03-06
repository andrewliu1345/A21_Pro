package com.joesmate.a21.serial_port_test;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.sdk.CMD;
import com.joesmate.a21.sdk.FingerDev;
import com.joesmate.a21.sdk.ICCardDev;
import com.joesmate.a21.sdk.PBOC;
import com.joesmate.a21.sdk.PICCardDev;
import com.joesmate.a21.sdk.ReaderDev;
import com.joesmate.a21.sdk.WlFingerDev;
import com.joesmate.a21.sdk.WlForUsbFingerDev;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.idcreader.HandImage;
import com.joesmate.sdk.util.LogMg;
import com.joesmate.sdk.util.ToolFun;

import java.io.File;
import java.io.UnsupportedEncodingException;


public class MainActivity extends AppCompatActivity {

    static final String TAG = MainActivity.class.toString();
    Button btn_close;//关闭
    Button btn_ICCard;//IC卡
    Button btn_KB;//密码键盘
    ToggleButton btn_otg;//OTG开关
    Button btn_signa;//签名
    // Button btn_Rest;//复位

    TextView txt_info;
    ImageView imageHand;
    static final byte[] fpdata = {(byte) 0x02, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x34,
            (byte) 0x30, (byte) 0x3c, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,
            (byte) 0x30, (byte) 0x30, (byte) 0x38, (byte) 0x03};


    // static final byte[] fpdata = {(byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0xC6, (byte) 0x02, (byte) 0x01, (byte) 0xC5, (byte) 0x03};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn_close = (Button) findViewById(R.id.btnClose);
        btn_close.setOnClickListener(closeListener);


        btn_ICCard = (Button) findViewById(R.id.btnIC);
        btn_ICCard.setOnClickListener(icListener);


        btn_KB = (Button) findViewById(R.id.btnIDCard);

        btn_KB = (Button) findViewById(R.id.btnKB);
        btn_KB = (Button) findViewById(R.id.btnKB);

        txt_info = (TextView) findViewById(R.id.txtInfo);
        txt_info.setMovementMethod(ScrollingMovementMethod.getInstance());

        btn_otg = (ToggleButton) findViewById(R.id.btnOTG);
        btn_otg.setOnCheckedChangeListener(otgCheck);

        btn_signa = (Button) findViewById(R.id.btnSigna);
        btn_signa.setOnClickListener(SignaListener);

//        btn_Rest = (Button) findViewById(R.id.btnRest);
//        btn_Rest.setOnClickListener(RestListener);
        imageHand = (ImageView) findViewById(R.id.imageView);
        txt_info.append(String.format("当前版本:%s \n", ToolFun.getApplicationVersionName(this)));
        txt_info.append(String.format("设备序列号：%s \n", ReaderDev.getInstance().getSnr(App.getInstance())));
    }

    static boolean isclose = true;
    static boolean listenRuning = false;
    static int fd = -1;
    static int fd2 = -1;

    public void OpenOnClick(View view) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "打开设备", "正在打开设备，请稍后...");
        new Thread() {
            @Override
            public void run() {
                super.run();
                //String path = libserialport_api.GetTtyPath();
                if (fd > 0) {
                    libserialport_api.device_close(fd);
                    fd = -1;
                }
                ReaderDev.getInstance().CCorePowerOn();
                gpio4.Down(1);
                File fl = new File("/dev/ttyMT1");
                File f2 = new File("/dev/ttyMT2");
                String path = fl.getAbsolutePath();
                String path2 = f2.getAbsolutePath();
                if (!fl.canRead() || !fl.canWrite() || !f2.canRead() || !f2.canWrite())
//                        try {
//                /* Missing read/write permission, trying to chmod the file */
//                            Process su;
//                            su = Runtime.getRuntime().exec("sh");
//                            String cmd = "su\n chmod -R 666 " + fl.getAbsolutePath() + "/ttyMT**" + "\n"
//                                    + "exit\n";
//                            su.getOutputStream().write(cmd.getBytes());
//                            if ((su.waitFor() != 0) || !fl.canRead()
//                                    || !fl.canWrite()) {
//                                Message ms =myhandler.obtainMessage();
//                                ms.what = 0;
//                                ms.obj = String.format("连接失败 path=%s,获取权限失败 \n", path);
//                                myhandler.sendMessage(ms);
//                                return;
//                            }
//                        } catch (Exception e) {
//
//                            Message ms =myhandler.obtainMessage();
//                            ms.what = 0;
//                            ms.obj = String.format("连接失败 path=%s,%s \n", path, e.getMessage());
//                            myhandler.sendMessage(ms);
//                            return;
//                        }
//                    }


                    if (!Funstion.GetPermission(path) || !Funstion.GetPermission(path2)) {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = String.format("连接失败 path=%s,path2=%s,获取权限失败 \n", path, path2);
                        myhandler.sendMessage(ms);
                        dialog.cancel();
                        return;
                    }

                fd = libserialport_api.device_open(path, 115200);
                fd2 = libserialport_api.device_open(path2, 9600);
                Message ms = myhandler.obtainMessage();
                ms.what = 0;
                if (fd > 0) {
                    App.getInstance().m_360fd = fd;
//                    byte[] send = ToolFun.toPackData((byte) 0x60, CMD.readSnr);
//                    libserialport_api.device_write(fd, send, send.length);
                    int iRet = libserialport_api.RF_Control(fd, (byte) 0);
                    LogMg.d("控制NFC", "iRet=%d", iRet);
                    ms.obj = String.format("连接成功 path=%s,fd=%d \n", path, fd);
                    isclose = false;
                } else {
                    ms.obj = String.format("连接失败 path=%s,fd=%d \n", path, fd);
                    isclose = true;
                }
                myhandler.sendMessage(ms);
                dialog.cancel();
            }
        }.start();
    }

    View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    isclose = true;
                    ReaderDev.getInstance().CCorePowerOff();
                    libserialport_api.device_close(fd);
                    libserialport_api.device_close(fd2);
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "设备关闭成功 \n";
                    myhandler.sendMessage(ms);
                }
            }.start();
        }
    };

    public void listenOnClick(View view) {
        if (listenRuning) {
            ((Button) view).setText("监听接口");
            listenRuning = false;
            return;
        } else {
            ((Button) view).setText("关闭监听");
            listenRuning = true;
        }
        new Thread() {
            @Override
            public void run() {
                int dRead = 0;
                int dRead2 = 0;
                byte[] in = new byte[4096];
                byte[] in2 = new byte[512];
                libserialport_api.flush(fd);
                libserialport_api.flush(fd2);
                while (true) {
                    Dalpay(10);
                    if (!listenRuning)
                        break;
                    if (isclose) {
                        continue;
                    }

                    dRead = libserialport_api.device_read_all(fd, in);
                    dRead2 = libserialport_api.device_read_all(fd2, in2);
                    if (dRead > 0) {
                        Log.d("收到数据", Funstion.byte2HexStr(in, dRead));
                        String str = "";
                        try {
                            str = Funstion.ProcessingData2String(in, dRead);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = str;

                        myhandler.sendMessage(ms);
                    }
                    if (dRead2 > 0) {
                        Log.d("收到数据", Funstion.byte2HexStr(in2, dRead2));
                        String str = "";
                        try {
                            str = Funstion.ProcessingData2String(in2, dRead2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = str;

                        myhandler.sendMessage(ms);
                    }
                }
            }
        }.start();
    }

    public void clearonClick(View v) {
        imageHand.setImageResource(R.drawable.photo);
        clearAlarmView(txt_info);
    }


    public void fpOnClick(View view) {
        imageHand.setImageResource(R.drawable.photo);
        Intent intent = new Intent(this, FingerActivity.class);
        startActivity(intent);
    }


    public void SnrOnClick(View view) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "获取模块版本号", "正在获取模块版本号，请稍后...");
        new Thread() {
            @Override
            public void run() {
                byte[] send = ToolFun.toPackData((byte) 0x60, CMD.readSnr);
                int iRet = libserialport_api.device_write(fd, send, send.length);
                if (iRet > 0) {
                    byte[] in = new byte[4096];
                    iRet = libserialport_api.device_read(fd, in, 100);
                    if (iRet > 0) {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = new String(in) + "\n";
                        myhandler.sendMessage(ms);
                    } else {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = "读取失败！\n";
                        myhandler.sendMessage(ms);
                    }
//                        Message ms =myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = "发送成功！\n";
//                        myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "发送失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();

    }

    public void nfcOnClick(View view) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "读取NFC", "正在读取NFC卡号，请稍后...");
        new Thread() {
            @Override
            public void run() {
                //libserialport_api.RF_Control(fd, (byte) 3);
//                String[] argRets = PBOC.getInstance().GetICCInfo(fd, 1, "", "A".toUpperCase(), 30);
//
//                if (argRets == null || argRets.length < 2 || argRets[1] == null || argRets[1].length() < 4) {
                PICCardDev ic = new PICCardDev(fd);
                String num = ic.ReadCardNum();
                if (!num.equals("")) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "卡号：" + num + "\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "读取失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
                // libserialport_api.RF_Control(fd, (byte) 0);

                // libserialport_api.RF_Control(fd, (byte) 0);
            }
        }.start();
    }


    View.OnClickListener icListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "读取IC", "正在读取IC卡，请稍后...");
            new Thread() {
                @Override
                public void run() {

//                    String[] argRets = PBOC.getInstance().GetICCInfo(fd, 0, "", "A".toUpperCase(), 30);
//
//                    if (argRets == null || argRets.length < 2 || argRets[1] == null || argRets[1].length() < 4) {
//                        Message ms = myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = "读取失败！\n";
//                        myhandler.sendMessage(ms);
//                    } else {
//                        String num = argRets[1].substring(4);
//                        Message ms = myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = "卡号：" + num + "\n";
//                        myhandler.sendMessage(ms);
//
//                    }
                    ICCardDev ic = new ICCardDev(fd);
                    String num = ic.ReadCardNum();
                    if (!num.equals("")) {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = "卡号：" + num + "\n";
                        myhandler.sendMessage(ms);
                    } else {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = "读取失败！\n";
                        myhandler.sendMessage(ms);
                    }
//                    byte[] send = ToolFun.toPackData((byte) 0x60, readic);
//                    int iRet = libserialport_api.device_write(fd, send, send.length);
//                    if (iRet > 0) {
//                        byte[] in = new byte[4096];
//                        iRet = libserialport_api.device_read(fd, in, 15000);
//                        if (iRet > 0) {
//                            Message ms =myhandler.obtainMessage();
//                            ms.what = 0;
//                            ms.obj = Funstion.byte2HexStr(in, iRet);
//                            myhandler.sendMessage(ms);
//                        } else {
//                            Message ms =myhandler.obtainMessage();
//                            ms.what = 0;
//                            ms.obj = "读取失败！\n";
//                            myhandler.sendMessage(ms);
//                        }
//                    } else {
//                        Message ms =myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = "发送失败！\n";
//                        myhandler.sendMessage(ms);
//                    }

                    dialog.cancel();
                }

            }.start();

        }
    };
    GPIO gpio4 = new GPIO(4, 0);

    public void IDonClick(View view) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "身份证", "正在获取身份证信息，请稍后...");
        imageHand.setImageResource(R.drawable.photo);
        new Thread() {
            @Override
            public void run() {
                // byte[] send = ToolFun.toPackData((byte) 0x60, CMD.activenfc);
                // int iRet = libserialport_api.device_write(fd, send, send.length);
                int iRet = -1;
                // if (iRet > 0) {//激活A卡

                // Log.d("激活B卡", ToolFun.hex_split(InputBuffer, iRet));
//                        byte[] in = new byte[4096];
//                        iRet = libserialport_api.device_read(fd, in, 1000);
//                        if (iRet > 0) {
                byte[] pucCHMsg = new byte[258], pucPHMsg = new byte[1024];
                libserialport_api.RF_Control(fd, (byte) 0);
                gpio4.Up(1);
                ToolFun.Dalpey(200);
                iRet = libserialport_api.device_ReadBaseMsg(fd, pucCHMsg, pucPHMsg, 10000);
                //  gpio4.Down(1);
                if (iRet != 0) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "读身份证失败！\n";
                    myhandler.sendMessage(ms);
                } else {

                    String CHMsg = null;
                    try {
                        CHMsg = new String(pucCHMsg, "UTF-16") + "\n";
                    } catch (UnsupportedEncodingException e) {
                    }
                    byte[] bmpdata = new byte[38862];
                    iRet = HandImage.DecWlt2Bmp(pucPHMsg, bmpdata);
                    Bitmap _image = BitmapFactory.decodeByteArray(bmpdata, 0, 38862);

                    Message ms = myhandler.obtainMessage();
                    ms.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("CHMsg", CHMsg);
                    bundle.putParcelable("Handimage", _image);
                    ms.setData(bundle);
                    myhandler.sendMessage(ms);
                }


//                } else {
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = "读身份证失败！\n";
//                    myhandler.sendMessage(ms);
//                }
//
//                } else {
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = "读身份证失败！\n";
//                    myhandler.sendMessage(ms);
//                }
                dialog.cancel();
            }
        }.start();
    }

    public void MConClick(View v) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "磁条", "请刷磁条卡...");
        new Thread() {
            @Override
            public void run() {

                byte[] send = ToolFun.toPackData((byte) 0x60, CMD.readmc);
                libserialport_api.flush(fd);
                int iRet = libserialport_api.device_write(fd, send, send.length);
                if (iRet > 0) {
                    byte[] in = new byte[4096];
                    iRet = libserialport_api.device_read(fd, in, 15000);
                    if (iRet > 0) {
                        // byte [] f1=new byte[255];
                        int len1 = in[1];
                        int len2 = in[len1 + 3];
                        int len3 = 0;
                        String s1 = "";
                        String s2 = "";
                        String s3 = "";
                        if (in[0] == (byte) 0xF1) {
                            len1 = in[1];
                            if (len1 + 3 < iRet)
                                len2 = in[len1 + 3];
                            if (len1 + len3 + 5 < iRet)
                                len3 = in[len1 + len2 + 5];

                        } else if (in[0] == (byte) 0xF2) {
                            len2 = in[1];
                            if (len2 + 3 < iRet)
                                len3 = in[len2 + 3];
                        } else if (in[0] == (byte) 0xF3) {
                            len3 = in[1];
                        }

                        int len = len1 + len2 + len3 + 3;
                        byte[] f1 = new byte[len1];
                        byte[] f2 = new byte[len2];
                        byte[] f3 = new byte[len3];

                        if (len1 > 0) {
                            System.arraycopy(in, 2, f1, 0, len1);
                            System.arraycopy(in, len1 + 4, f2, 0, len2);
                            System.arraycopy(in, len1 + len2 + 6, f3, 0, len3);
                        } else if (len2 > 0) {
                            System.arraycopy(in, 2, f2, 0, len2);
                            System.arraycopy(in, len2 + 4, f3, 0, len3);
                        } else if (len3 > 0) {
                            System.arraycopy(in, 2, f3, 0, len3);
                        }

                        s1 = new String(f1);
                        s2 = new String(f2);
                        s3 = new String(f3);
                        String str = String.format("T1:%s  T2:%s  T3:%s \n", s1, s2, s3);
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = str;//Funstion.byte2HexStr(in, iRet);
                        myhandler.sendMessage(ms);
                    } else {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = "读取失败！\n";
                        myhandler.sendMessage(ms);
                    }
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = "发送成功！\n";
//                    myhandler.sendMessage(ms);ms
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "发送失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }


    public void KBonClick(View v) {
        Intent intent = new Intent(this.getApplicationContext(), KeyboardActivity.class);
        startActivity(intent);
//        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "输入密码", "请输入密码...");
//        new Thread() {
//            @Override
//            public void run() {
//                byte[] InputBuffer = new byte[1024];
//                byte[] send = ToolFun.toPackData((byte) 0x60, CMD.readkb);
//                int iRet = libserialport_api.device_write(fd, send, send.length);
//                iRet = libserialport_api.device_read(fd, InputBuffer, 30000);
//                if (iRet > 0) {
//                    int len = 15; //InputBuffer[0]/2;
//                    byte[] mun = new byte[len];
//                    System.arraycopy(InputBuffer, 0, mun, 0, len);
//                    String str = ToolFun.printHexString(mun); //new String(InputBuffer, "GBK");
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = String.format("获取密码成功：%s", str);
//                    myhandler.sendMessage(ms);
//
//                } else {
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = "发送失败！\n";
//                    myhandler.sendMessage(ms);
//                }
//                dialog.cancel();
//            }
//        }.start();
    }

    CompoundButton.OnCheckedChangeListener otgCheck = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {


            new Thread() {
                boolean bl = isChecked;

                @Override
                public void run() {
                    if (bl) {
                        int i = ReaderDev.getInstance().FpPowerOn();
                        if (i == 0) {
                            Message ms = myhandler.obtainMessage();
                            ms.what = 0;
                            ms.obj = "OTG 打开成功！\n";
                            myhandler.sendMessage(ms);
                        } else {
                            Message ms = myhandler.obtainMessage();
                            ms.what = 0;
                            ms.obj = "OTG 打开失败！\n";
                            myhandler.sendMessage(ms);
                        }
                    } else {
                        int i = ReaderDev.getInstance().FpPowerOff();
                        if (i == 0) {
                            Message ms = myhandler.obtainMessage();
                            ms.what = 0;
                            ms.obj = "OTG 关闭成功！\n";
                            myhandler.sendMessage(ms);
                        } else {
                            Message ms = myhandler.obtainMessage();
                            ms.what = 0;
                            ms.obj = "OTG 关闭失败！\n";
                            myhandler.sendMessage(ms);
                        }
                    }
                }
            }.start();

        }
    };
    View.OnClickListener SignaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SignatureActivity.class);
            startActivityForResult(intent, 1);


        }
    };

    public void RestOnClick(View view) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "复位", "正在复位，请稍后...");
        new Thread() {
            @Override
            public void run() {
                try {
                    ReaderDev.getInstance().CCoreRest();
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "复位成功！\n";
                    myhandler.sendMessage(ms);
                } catch (Exception ex) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "复位失败！\n";
                    myhandler.sendMessage(ms);
                }

                dialog.cancel();
            }
        }.start();
    }

//    View.OnClickListener RestListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "复位", "正在复位，请稍后...");
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        ReaderDev.getInstance().CCoreRest();
//                        Message ms =myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = "复位成功！\n";
//                        myhandler.sendMessage(ms);
//                    } catch (Exception ex) {
//                        Message ms =myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = "复位失败！\n";
//                        myhandler.sendMessage(ms);
//                    }
//
//                    dialog.cancel();
//                }
//            }.start();
//        }
//    };

    final Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    String str = (String) msg.obj;
                    refreshAlarmView(txt_info, str);
                    break;
                }
                case 1: {
                    Bundle bundle = msg.getData();
                    String str = bundle.getString("CHMsg");
                    Bitmap bm = bundle.getParcelable("Handimage");
                    refreshAlarmView(txt_info, str);
                    //imageHand.setImageBitmap(bm);
                    ToolFun.saveBitmap("/sdcard/photo.png", bm, 100, Bitmap.CompressFormat.PNG);
                    Bitmap bm2 = BitmapFactory.decodeFile("/sdcard/photo.png");
                    imageHand.setImageBitmap(bm2);
                    break;
                }
                case 2: {
                    Bundle bundle = msg.getData();
                    Bitmap bm = bundle.getParcelable("Image");
                    imageHand.setImageBitmap(bm);
                    break;
                }
                default:
                    break;
            }
        }
    };

    public void ClearOnClick(View v) {
        final EditText inputServer = new EditText(this);//输入框
        inputServer.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清除固件，请输入密码");//设置标题
        builder.setView(inputServer);
        //builder.setIcon(R);//设置图标
        builder.setMessage("确认清除固件");//设置内容
        /*添加对话框中确定按钮和点击事件*/
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                String inputName = inputServer.getText().toString();
                if (!inputName.equals("123456")) {
                    Toast.makeText(MainActivity.this, "密码输入错误", Toast.LENGTH_LONG).show();
                    return;
                }
                final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "擦除固件", "正在擦除固件，请稍后...");
                new Thread() {
                    @Override
                    public void run() {
                        byte[] out = {(byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
                                (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa};
                        libserialport_api.device_write(fd, out, out.length);
                        ToolFun.Dalpey(100);
                        byte[] in = new byte[2000];
                        int iRet = libserialport_api.device_read_all(fd, in);
                        if (iRet > 0) {
                            try {
                                ReaderDev.getInstance().CCoreRest();
                                Message ms = myhandler.obtainMessage();
                                ms.what = 0;
                                ms.obj = String.format("固件擦除成功:[%s]！\n", new String(in));
                                myhandler.sendMessage(ms);
                            } catch (Exception ex) {
                                Message ms = myhandler.obtainMessage();
                                ms.what = 0;
                                ms.obj = "模块复位失败！\n";
                                myhandler.sendMessage(ms);
                            }
                        } else {
                            Message ms = myhandler.obtainMessage();
                            ms.what = 0;
                            ms.obj = "固件擦除失败！\n";
                            myhandler.sendMessage(ms);
                        }

                        dialog.cancel();
                    }
                }.start();
            }
        });
        /*添加对话框中取消按钮和点击事件*/
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(MainActivity.this, "点击了取消按钮", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog dialog = builder.create();//获取dialog
        dialog.show();//显示对话框


    }

    public void ADPU_IC_OnClick(View v) {
//        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "发送ADPU", "正在发送ADPU，请稍后...");
//        new Thread() {
//            @Override
//            public void run() {
//                ICCardDev ic = new ICCardDev(fd);
//                String str = ic.sendAdpu();
//                if (!str.equals("")) {
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = "返回：" + str + "\n";
//                    myhandler.sendMessage(ms);
//                } else {
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = "发送ADPU失败！\n";
//                    myhandler.sendMessage(ms);
//                }
//                dialog.cancel();
//            }
//        }.start();
        Intent intent = new Intent(this, PBOCActivity.class);
        startActivity(intent);
    }

    public void ADPU_NFC_OnClick(View v) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "发送ADPU", "正在发送ADPU，请稍后...");
        new Thread() {
            @Override
            public void run() {
                PICCardDev ic = new PICCardDev(fd);
                String str = ic.sendAdpu();
                if (!str.equals("")) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "返回：" + str + "\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "发送ADPU失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1: {
                if (resultCode == 0) {
                    Bitmap bp = data.getParcelableExtra("bitmap");
                    imageHand.setImageBitmap(bp);
                }
                break;
            }

            default:
                break;
        }

    }

    public void DeviceSleep(View v) {
        new Thread() {
            @Override
            public void run() {
                GPIO g = new GPIO(1, 0);
                g.Down(1);
                byte[] send = ToolFun.toPackData((byte) 0x60, CMD.device_sleep);
                int iRet = libserialport_api.device_write(fd, send, send.length);
            }
        }.start();
    }

    public void Awaken(View v) {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, "唤醒设备", "正在获取唤醒设备，请稍后...");
        new Thread() {
            @Override
            public void run() {
                GPIO gpio = new GPIO(1, 0);
                gpio.Up(1);
                byte[] send = ToolFun.toPackData((byte) 0x60, CMD.readSnr);
                int iRet = libserialport_api.device_write(fd, send, send.length);
                if (iRet > 0) {
                    byte[] in = new byte[4096];
                    iRet = libserialport_api.device_read(fd, in, 100);
                    if (iRet > 0) {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = new String(in) + "\n";
                        myhandler.sendMessage(ms);
                    } else {
                        Message ms = myhandler.obtainMessage();
                        ms.what = 0;
                        ms.obj = "唤醒失败！\n";
                        myhandler.sendMessage(ms);
                    }

                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "唤醒失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    public void GPIOdo(View v) {
        Intent intent = new Intent(this, GPIOActivity.class);
        startActivity(intent);
    }

    public void BtClick(View v) {
        Intent intent = new Intent(this, BtStaActivity.class);
        startActivity(intent);
    }

    private void Dalpay(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    private void refreshAlarmView(TextView textView, String msg) {
        textView.append(msg);
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > (textView.getHeight() - textView.getLineHeight() - 20)) {
            textView.scrollTo(0, offset - textView.getHeight() + textView.getLineHeight() + 20);
        }
    }

    private void clearAlarmView(TextView textView) {
        textView.setText("");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > (textView.getHeight() - textView.getLineHeight() - 20)) {
            textView.scrollTo(0, offset - textView.getHeight() + textView.getLineHeight() + 20);
        }
    }

}

