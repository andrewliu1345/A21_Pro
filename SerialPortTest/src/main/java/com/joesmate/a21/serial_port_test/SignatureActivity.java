package com.joesmate.a21.serial_port_test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.joesmate.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;

public class SignatureActivity extends AppCompatActivity {

    Button btn_clear;
    Bitmap sinBitmap;

    SignaturePad signaturePad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

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


        btn_clear = (Button) findViewById(R.id.btnClear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });
    }

    public void onExit(View v)
    {
        setResult(-1);
        finish();
    }

    public void onSave(View v) {
        if (!signaturePad.isEmpty()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Bitmap bitmap = signaturePad.getSignatureBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] jpgbuff = baos.toByteArray();
            Intent intent = new Intent();
            intent.putExtra("jpgbuff", jpgbuff);
            intent.putExtra("bitmap", bitmap);
            setResult(0, intent);
        } else {
            this.setResult(-1);
        }
        this.finish();
    }

}
