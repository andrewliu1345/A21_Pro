package com.joesmate.a21.backgroundservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import com.joesmate.sdk.util.LogMg;

import java.io.ByteArrayOutputStream;

public class Main2Activity extends AppCompatActivity {

    private WebView webView;
    private Intent _intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _intent = getIntent();
        setContentView(R.layout.activity_main2);
        webView = findViewById(R.id.wMyWebView);
        Loadhtml();

        IntentFilter filter = new IntentFilter();
        filter.addAction("action.view");
        registerReceiver(broadcastReceiver, filter);

    }

    private void Loadhtml() {
        int tag = _intent.getIntExtra("action", 0);
        switch (tag) {
            case 1: {
                ShowFinger();
            }
            break;
            case 2: {
                ShowIDCard();
            }
            default: {
            }
            break;
        }
    }

    private void ShowFinger() {
        webView.loadUrl("file:///android_asset/Finger.html");
    }

    private void ShowIDCard() {
        webView.loadUrl("file:///android_asset/IDCard.html");
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int tag = intent.getIntExtra("action", -1);
            switch (tag) {
//                case 0://返回数据
//                {
//                    Message msg = handler.obtainMessage();
//                    msg.what = 0;
//                    msg.obj = intent;
//                    handler.sendMessage(msg);
//                }
//                break;
                case 1://取消
                {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    msg.obj = intent;
                    handler.sendMessage(msg);
                }
                break;
                default: {
                    LogMg.e("页面显示", "广播数据不存在");
                }
//                case 2: {
//                    Message msg = handler.obtainMessage();
//                    msg.what = 2;
//                    msg.obj = intent;
//                    handler.sendMessage(msg);
//                }
//                break;
            }
        }
    };
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0: {
                    finish();
                    break;
                }
                case 1: {//关闭界面

                    finish();
                    break;
                }


            }
        }
    };
}
