package com.joesmate.a21.serial_port_test;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.joesmate.a21.sdk.PBOC;

public class PBOCActivity extends AppCompatActivity {

    int fd = -1;
    TextView txt_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pboc);
        fd = App.getInstance().m_360fd;
        txt_info = (TextView) findViewById(R.id.txtInfo);
        txt_info.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void onGetICCInfo(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "PBOC", "正在读取IC卡客户信息，请稍后...");
        new Thread() {
            @Override
            public void run() {
                String[] argRets = PBOC.getInstance().GetICCInfo(fd, 2, "", "ABCDEFGHIJKL".toUpperCase(), 30);
                if (argRets == null) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取失败\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = String.format("获取IC卡客户信息成功：%s--%s \n", argRets[0], argRets[1]);
                    myhandler.sendMessage(ms);

                }
                dialog.cancel();
            }
        }.start();
    }

    public void onGetARQC(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "PBOC", "正在获取ARQC，请稍后...");
        new Thread() {
            @Override
            public void run() {
                String[] argRets = PBOC.getInstance().GetICCArqc(fd, 2, "P012000000010000Q012000000000000R003156S006111202T00201U006102550V000", "A000000333".toUpperCase(), 30);
                if (argRets == null) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取失败\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = String.format("获取ARQC成功：%s--%s \n", argRets[0], argRets[1]);
                    myhandler.sendMessage(ms);

                }
                dialog.cancel();
            }
        }.start();
    }

    public void onARPCExeScript(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "PBOC", "正在向IC卡发送ARPC及写卡脚本，请稍后...");
        new Thread() {
            @Override
            public void run() {
                //String[] argRets = PBOC.getInstance().GetICCArqc(fd, 2, "A000000333", "ABCDEFGHIJKL".toUpperCase(), 30);
                Message ms = myhandler.obtainMessage();
                ms.what = 0;
                ms.obj = "需要银行联网数据";//argRets[0] + argRets[1];
                myhandler.sendMessage(ms);
                dialog.cancel();
            }
        }.start();
    }

    public void onGetTrDetail(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "PBOC", "正在获取IC卡交易日志，请稍后...");
        new Thread() {
            @Override
            public void run() {
                String[] argRets = PBOC.getInstance().GetTrDetail(fd, 2, 0, 30);
                if (argRets == null) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取失败\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = String.format("获取IC卡交易日志成功：%s--%s \n", argRets[0], argRets[1]);
                    myhandler.sendMessage(ms);

                }
                dialog.cancel();
            }
        }.start();
    }

    public void onGetLoadLog(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "PBOC", "正在读取IC卡圈存明细，请稍后...");
        new Thread() {
            @Override
            public void run() {
                String[] argRets = PBOC.getInstance().GetLoadLog(fd, 2, 0, "A000000333", 30);
                if (argRets == null) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取失败\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = String.format("获取IC卡圈存明细成功：%s--%s \n", argRets[0], argRets[1]);
                    myhandler.sendMessage(ms);

                }
                dialog.cancel();
            }
        }.start();
    }

    public void onGetICAndARQCInfo(View v) {
        final ProgressDialog dialog = ProgressDialog.show(this, "PBOC", "正在读取IC卡客户信息及ARQC，请稍后...");
        new Thread() {
            @Override
            public void run() {
                String[] argRets = PBOC.getInstance().GetICAndARQCInfo(fd, 2, "A000000333", "ABCDEFGHIJKL".toUpperCase(), "P012000000010000Q012000000000000R003156S006111202T00201U006102550V000", 30);
                if (argRets == null) {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = "获取失败\n";
                    myhandler.sendMessage(ms);
                } else {
                    Message ms = myhandler.obtainMessage();
                    ms.what = 0;
                    ms.obj = String.format("获取成功：%s--IC卡客户信息:%s ARQC:%s\n", argRets[0], argRets[1], argRets[2]);
                    myhandler.sendMessage(ms);

                }
                dialog.cancel();
            }
        }.start();
    }

    public void onClear(View v) {
        clearAlarmView(txt_info);
    }

    private void refreshAlarmView(TextView textView, String msg) {
        textView.append(msg);
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > (textView.getHeight() - textView.getLineHeight() - 20)) {
            textView.scrollTo(0, offset - textView.getHeight() + textView.getLineHeight() + 20);
        }
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
                }
                default:
                    break;
            }
        }
    };


    private void clearAlarmView(TextView textView) {
        textView.setText("");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > (textView.getHeight() - textView.getLineHeight() - 20)) {
            textView.scrollTo(0, offset - textView.getHeight() + textView.getLineHeight() + 20);
        }
    }
}
