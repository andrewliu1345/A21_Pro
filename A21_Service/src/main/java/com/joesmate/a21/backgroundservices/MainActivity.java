package com.joesmate.a21.backgroundservices;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.joesmate.a21.io.GPIO;
import com.joesmate.a21.sdk.BtStaDev;
import com.joesmate.a21.sdk.ReaderDev;
import com.joesmate.a21.serial_port_api.libserialport_api;
import com.joesmate.sdk.util.ToolFun;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private Intent intent;
    TextView txt_ver;
    TextView txt_info;

    AudioManager mAudioManager;
    GPIO gpio4 = new GPIO(4, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        txt_ver = (TextView) findViewById(R.id.txtVer);
        intent = new Intent(MainActivity.this, BTSerialPortService.class);
        txt_info = (TextView) findViewById(R.id.txtInfo);
        txt_info.setMovementMethod(ScrollingMovementMethod.getInstance());
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);


        ReaderDev.getInstance().FpPowerOn();
        ReaderDev.getInstance().CCorePowerOn();
        BtStaDev.getInstance().BtPowerOn();
        startService(intent);
        libserialport_api.RF_Control(App.getInstance().m_360fd, (byte) 0);
        gpio4.Down(1);
        txt_ver.setText(String.format("当前版本：%s", ToolFun.getApplicationVersionName(this)));

        App.getInstance().tts.speak(this.getString(R.string.wellcom));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
        ReaderDev.getInstance().FpPowerOff();
        // ReaderDev.getInstance().CCorePowerOff();
        BtStaDev.getInstance().BtPowerOff();
    }

//    public void SendMsg(View v) {
//        Intent m_intent = new Intent(AppAction.ACTION_BT_DATA);
//        m_intent.putExtra("BT_IN_DATA", "发送~~！".getBytes());
//        sendBroadcast(m_intent);
//    }

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

    public void AudioUp(View v) {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,
                AudioManager.FX_FOCUS_NAVIGATION_UP);
        new Thread() {
            @Override
            public void run() {
                App.getInstance().tts.speak("音量增加");
            }
        }.start();

    }

    public void AudioDown(View v) {
        mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,
                AudioManager.FX_FOCUS_NAVIGATION_UP);
        new Thread() {
            @Override
            public void run() {
                App.getInstance().tts.speak("音量减小");
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

                default:
                    break;
            }
        }
    };


}
