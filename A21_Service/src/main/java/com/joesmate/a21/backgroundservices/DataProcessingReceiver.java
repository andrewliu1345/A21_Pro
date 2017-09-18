package com.joesmate.a21.backgroundservices;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DataProcessingReceiver extends BroadcastReceiver {

    final static String TAG = DataProcessingReceiver.class.toString();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, action);

        if (action.equals(AppAction.ACTION_BT_DATA)) {
            byte[] in = intent.getByteArrayExtra("BT_IN_DATA");
            Log.d(TAG, String.format("DataProcessingReceiver :=%s", new String(in).replace("\0", "")));
            Intent service_intent = new Intent(context, DataProcessingService.class);
            service_intent.putExtra("BT_IN_DATA", in);
            context.startService(service_intent);
        }

    }
}
