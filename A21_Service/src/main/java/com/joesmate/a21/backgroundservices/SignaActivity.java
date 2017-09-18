package com.joesmate.a21.backgroundservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.joesmate.a21.backgroundservices.bin.Signature;
import com.joesmate.a21.io.GPIO;
import com.joesmate.sdk.util.LogMg;
import com.joesmate.sdk.util.ToolFun;
import com.joesmate.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class SignaActivity extends AppCompatActivity {

    final static String TAG = SignaActivity.class.toString();
    SignaturePad signaturePad;
    App.OnReturnListen mlisten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signa);
        IntentFilter filter = new IntentFilter();
        filter.addAction("action.signature");
        registerReceiver(broadcastReceiver, filter);
        GPIO gpiosign = new GPIO(86, 0);
        gpiosign.Up(1);
        mlisten = App.getInstance().getOnReturnListen();
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
                Log.i("开始", "开始签名");
            }

            @Override
            public void onSigned() {
                Log.i("结束", "签名结束");
            }

            @Override
            public void onClear() {
                Log.i("清除", "清除签名");
            }
        });
    }

    public void ClearClick(View v) {
        signaturePad.clear();
    }


    public void SaveClick(View v) {
        if (!signaturePad.isEmpty()) {
//            List<List<float[]>> posdata = signaturePad.getSignatureData();
//
//            int height = signaturePad.getMeasuredHeight();
//            int weight = signaturePad.getMeasuredWidth();
            new Thread() {
                @Override
                public void run() {
//                    String str = "";
//                    str += String.format("%d|%d|(", height, weight);
//                    for (List<float[]> x :
//                            posdata) {
//                        for (float[] y :
//                                x) {
//                            str += String.format("%1f,%1f,%1f;", y[0], y[1], y[2]);
//                        }
//                    }
//                    str += ")";
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    Bitmap bitmap = signaturePad.getSignatureBitmap();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 12, baos);
                    byte[] imgbuff = baos.toByteArray();
//                    byte[] testzip = new byte[0];
//                    try {
//                        testzip = ToolFun.compress(imgbuff);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    //LogMg.d(TAG, "imgbuff.length=%d==压缩==>testzip.length=%d==解压==>testunzip.length=%d", imgbuff.length, testzip.length);
                    Intent intent = new Intent();
                    intent.putExtra("imgbuff", imgbuff);
                    // intent.putExtra("posbuff", str.getBytes());
                    intent.putExtra("bitmap", bitmap);
                    mlisten.onSuess(intent);
                }
            }.start();
        } else {
            mlisten.onErr(-1);
        }
        finish();
    }

    public void ExitClick(View v) {
        mlisten.onErr(-1);
        finish();
    }

    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int tag = intent.getIntExtra("action", -1);
            switch (tag) {
                case 0://返回数据
                    if (!signaturePad.isEmpty()) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Bitmap bitmap = signaturePad.getSignatureBitmap();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 15, baos);
                        byte[] jpg_buff = baos.toByteArray();
                        Intent msg = new Intent();
                        msg.putExtra("jpgbuff", jpg_buff);
                        mlisten.onSuess(intent);
                    } else {
                        mlisten.onErr(-1);
                    }
                    finish();
                    break;
                case 1://取消
                    SignaActivity.this.setResult(-1);
                    SignaActivity.this.finish();
                    break;
                case 2:
                    SignaActivity.this.signaturePad.clear();
                    break;
            }
        }
    };
}
