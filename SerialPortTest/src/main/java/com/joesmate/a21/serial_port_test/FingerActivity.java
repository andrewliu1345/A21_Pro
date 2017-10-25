package com.joesmate.a21.serial_port_test;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joesmate.a21.sdk.FingerDev;
import com.joesmate.a21.sdk.TcFingerDev;
import com.joesmate.a21.sdk.WlFingerDev;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import java.io.File;

public class FingerActivity extends AppCompatActivity {
    TextView txt_info;
    ImageView imagefinger;
    int fd2 = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger);
        File f2 = new File("/dev/ttyMT2");
        String path2 = f2.getAbsolutePath();
        fd2 = libserialport_api.device_open(path2, 9600);
        txt_info = (TextView) findViewById(R.id.finger_info);
        txt_info.setMovementMethod(ScrollingMovementMethod.getInstance());
        imagefinger = (ImageView) findViewById(R.id.imageFinger);
    }

    public void OnFingerClick(View v) {
        final ProgressDialog dialog = ProgressDialog.show(FingerActivity.this, "指纹", "请放手指...");
        new Thread() {
            @Override
            public void run() {
                int errcode = -1;
                FingerDev fpdev = new TcFingerDev();
                fpdev.setDevFd(fd2);
                byte[] fpdata = fpdev.sampFingerPrint(30000);
                if (fpdata != null && fpdata.length > 0) {
                    Log.d("收到数据", Funstion.byte2HexStr(fpdata, fpdata.length));
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    try {
                        ms.obj = ToolFun.printHexString(fpdata) + "\n";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    myhandler.sendMessage(ms);
                    ToolFun.Dalpey(500);
                    byte[] imgbuff = new byte[0];
                    try {
                        imgbuff = fpdev.imgFingerPrint(30);
//                        byte[] testzip = ToolFun.compress(imgbuff);
//                        byte[] testunzip = ToolFun.uncompress(testzip);
//                        LogMg.d(TAG, "imgbuff.length=%d==压缩==>testzip.length=%d==解压==>testunzip.length=%d", imgbuff.length, testzip.length, testunzip.length);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    if (imgbuff != null && imgbuff.length > 0) {
//
//                        Bitmap bm = BitmapFactory.decodeByteArray(imgbuff, 0, imgbuff.length);
//                        if (bm != null) {
//                            ToolFun.saveBitmap("/sdcard/finger.png", bm, 30, Bitmap.CompressFormat.PNG);
//                            ms = myhandler.obtainMessage();
//                            ms.what = 2;
//                            Bundle bundle = new Bundle();
//                            bundle.putParcelable("Image", bm);
//                            ms.setData(bundle);
//                            myhandler.sendMessage(ms);
//                        }
//                    } else {
//                        ms = myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = "获取图像失败！\n";
//                        myhandler.sendMessage(ms);
//                    }
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取特征失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }

//                long start = System.currentTimeMillis();
//                long sut = 0;
//                int i = ReaderDev.getInstance().FpPowerOn();
//
//                libserialport_api.flush(fd2);
//                int iRet = libserialport_api.device_write(fd2, fpdata, fpdata.length);
//                if (iRet < 0) {
//                    Message ms = myhandler.obtainMessage();
//                    ms.what = 0;
//                    ms.obj = "发送失败！";
//                    myhandler.sendMessage(ms);
//                    dialog.cancel();
//                    return;
//                }
//
//
//                byte[] in = new byte[4096];
//                sut = System.currentTimeMillis() - start;
//                while (sut < 15000) {
//                    int dRead = libserialport_api.device_read_all(fd2, in);
//                    if (dRead > 0) {
//                        Log.d("收到数据", Funstion.byte2HexStr(in, dRead));
//                        String str = "";
//                        try {
//                            str = Funstion.ProcessingData2String(in, dRead);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                        Message ms = myhandler.obtainMessage();
//                        ms.what = 0;
//                        ms.obj = str;
//
//                        myhandler.sendMessage(ms);
//                        break;
//                    }
//
//                    sut = System.currentTimeMillis() - start;
//                }


        }.start();
    }

    public void OnClearClick(View v) {
        imagefinger.setImageResource(R.drawable.fingerprint4);
        clearAlarmView(txt_info);
    }

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
//                    Bundle bundle = msg.getData();
//                    String str = bundle.getString("CHMsg");
//                    Bitmap bm = bundle.getParcelable("Handimage");
//                    refreshAlarmView(txt_info, str);
//                    //imageHand.setImageBitmap(bm);
//                    ToolFun.saveBitmap("/sdcard/photo.png", bm, 100, Bitmap.CompressFormat.PNG);
//                    Bitmap bm2 = BitmapFactory.decodeFile("/sdcard/photo.png");
//                    imageHand.setImageBitmap(bm2);
                    break;
                }
                case 2: {
                    Bundle bundle = msg.getData();
                    Bitmap bm = bundle.getParcelable("Image");
                    imagefinger.setImageBitmap(bm);
                    break;
                }
                default:
                    break;
            }
        }
    };

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
