package com.joesmate.a21.backgroundservices.bin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.joesmate.a21.backgroundservices.App;
import com.joesmate.a21.backgroundservices.SignaActivity;

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

    public void Start(Context context, App.OnReturnListen listen) {
        App.getInstance().setOnReturnListen(listen);
        mbuffer = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(context, SignaActivity.class);
        context.startActivity(intent);
    }

    public void Exit() {
        Intent intent = new Intent("action.signature");
        intent.putExtra("action", 1);
        App.getInstance().sendBroadcast(intent);
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
