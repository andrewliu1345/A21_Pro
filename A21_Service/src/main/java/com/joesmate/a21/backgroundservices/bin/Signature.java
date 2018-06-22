package com.joesmate.a21.backgroundservices.bin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.backgroundservices.SignaActivity;
import com.jostmate.IListen.OnReturnListen;

/**
 * Created by andre on 2017/7/25 .
 */

public class Signature {
    private static final Signature mInstance = new Signature();
    Intent intent = new Intent();
    byte[] mbuffer = null;


    private Signature() {

    }

    public static Signature getInstance() {
        return mInstance;
    }

    /**
     * 打开签名
     *
     * @param context 上下文
     * @param listen  返回回调
     */
    public void Start(Context context, OnReturnListen listen) {//打开签名
        App.getInstance().setOnReturnListen(listen);
        mbuffer = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, SignaActivity.class);
        context.startActivity(intent);

    }

    /**
     * 打开签名
     *
     * @param context 上下文
     * @param height  高
     * @param width   宽
     * @param listen  返回回调
     */
    public void Start(Context context, int height, int width, OnReturnListen listen) {//打开签名
        App.getInstance().setOnReturnListen(listen);
        mbuffer = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, SignaActivity.class);
        intent.putExtra("height", height);
        intent.putExtra("width", width);
        context.startActivity(intent);
    }

    public void Exit() {//退出
        Intent intent = new Intent("action.signature");
        intent.putExtra("action", 1);
        App.getInstance().sendBroadcast(intent);
    }

    public void Clear() {//清屏
        Intent intent = new Intent("action.signature");
        intent.putExtra("action", 2);
        App.getInstance().sendBroadcast(intent);
    }

    public void setBuffer(byte[] buffer) {
        mbuffer = buffer;
    }

    public byte[] getBuffer() {
        return mbuffer;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
