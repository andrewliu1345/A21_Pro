package com.joesmate.a21.serial_port_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.joesmate.a21.sdk.BtStaDev;
import com.joesmate.a21.serial_port_api.libserialport_api;

public class BtStaActivity extends AppCompatActivity {
    final String m_btpath = "/dev/ttyMT3";//蓝牙地址
    int bt_fd = libserialport_api.device_open(m_btpath, 115200);
    EditText txtBtName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_sta);
        txtBtName = findViewById(R.id.txt_bt_name);
    }

    public void onChangeBtName(View v) {
        String name = txtBtName.getText().toString().trim();
        BtStaDev.getInstance().ChangeBtName(bt_fd, name);
    }

    public void onOpenBt(View v) {
        BtStaDev.getInstance().BtPowerOn();
    }

    public void onCloseBt(View v) {
        BtStaDev.getInstance().BtPowerOff();
    }
}
