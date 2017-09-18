package com.joesmate.a21.serial_port_test;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.joesmate.a21.sdk.KeyboardDev;
import com.joesmate.sdk.util.ToolFun;

import org.w3c.dom.Text;

public class KeyboardActivity extends AppCompatActivity {

    int fd = 0;
    TextView txt_info;
    EditText txt_Mkey;
    EditText txt_Wkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        txt_info = (TextView) findViewById(R.id.txtInfo);
        txt_Mkey = (EditText) findViewById(R.id.txtZMK);
        txt_Wkey = (EditText) findViewById(R.id.txtZWK);
        fd = App.getInstance().m_360fd;
    }

    public void onRestDefaultKey(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "密码", "请恢复出厂密码...");
        new Thread() {
            @Override
            public void run() {
                int iRet = KeyboardDev.getInstance().RestDefaultKey(fd);
                if (iRet == 0) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "密钥恢复出厂成功！\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "密钥恢复出厂失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    public void onGetEnPw(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "输入密码", "请输入密码...");
        new Thread() {
            @Override
            public void run() {
                String pin = ToolFun.printHexString(KeyboardDev.getInstance().getEnablePassword(fd, 6, 1, 15000l));
                if (pin != null) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = String.format("获取密码成功：%s\n", pin);
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取密码失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    public void onGetCryPw(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "输入密码", "请输入密码...");
        new Thread() {
            @Override
            public void run() {
                byte[] num = KeyboardDev.getInstance().getCryptPassword(fd, 6, 0, 15000l);
                String pin = ToolFun.printHexString(num);
                if (!pin.equals("")) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = String.format("获取密码成功：%s \n", pin);
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取密码失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    public void onActiveWKey(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "密钥激活", "请输密钥激活...");
        new Thread() {
            @Override
            public void run() {
                int iRet = KeyboardDev.getInstance().ActiveWKey(fd, 0, 0);
                if (iRet == 0) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "密钥激活成功！\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "密钥激活失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    public void onDownMkey(View v) {
        final byte[] mkey = ToolFun.hexStringToBytes(txt_Mkey.getText().toString());
        final ProgressDialog dialog = ProgressDialog.show(this, "下载主密钥", "请输下载主密钥...");
        new Thread() {
            @Override
            public void run() {
                int iRet = KeyboardDev.getInstance().DownMkey(fd, 0, 2, mkey);
                if (iRet == 0) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "下载主密钥成功！\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "下载主密钥失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    public void onDownWkey(View v) {
        final byte[] wkey = ToolFun.hexStringToBytes(txt_Wkey.getText().toString());
        final ProgressDialog dialog = ProgressDialog.show(this, "下载工作密钥", "请输下载工作密钥...");
        new Thread() {
            @Override
            public void run() {
                int iRet = KeyboardDev.getInstance().DownWkey(fd, 0, 0, 2, wkey);
                if (iRet == 0) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "下载工作成功！\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "下载工作失败！\n";
                    myhandler.sendMessage(ms);
                }
                dialog.cancel();
            }
        }.start();
    }

    final Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    String str = (String) msg.obj;
                    txt_info.append(str);
                    break;
                }
                case 1: {
//                    Bundle bundle = msg.getData();
//                    String str = bundle.getString("CHMsg");
//                    Bitmap bm = bundle.getParcelable("Handimage");
//                    txt_info.append(str);
//                    imageHand.setImageBitmap(bm);
                }
                default:
                    break;
            }
        }
    };
}
