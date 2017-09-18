package com.joesmate.a21.serial_port_test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.joesmate.a21.io.GPIO;

public class GPIOActivity extends AppCompatActivity {

    EditText txt_io;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpio);
        txt_io = (EditText) findViewById(R.id.txt_IO);
    }

    public void Up(View v) {
        String str = txt_io.getText().toString();
        if (str != null || str != "") {

            int io = Integer.parseInt(str);  //Integer.getInteger(str);
            try {
                GPIO gpio = new GPIO(io, 0);
                gpio.Up(1);
                Toast.makeText(this, String.format("GPIO: %d,上电成功", io), Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(this, String.format("GPIO: %d,上电失败", io), Toast.LENGTH_LONG).show();
            }
            return;
        }
        Toast.makeText(this, String.format("GPIO: %s参数错误", str), Toast.LENGTH_LONG).show();
    }

    public void Down(View v) {
        String str = txt_io.getText().toString();
        if (str != null || str != "") {
            int io = Integer.parseInt(str);// Integer.getInteger(str);
            try {
                GPIO gpio = new GPIO(io, 0);
                gpio.Down(1);
                Toast.makeText(this, String.format("GPIO: %d,下电成功", io), Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                Toast.makeText(this, String.format("GPIO: %d,下电失败", io), Toast.LENGTH_LONG).show();
            }
            return;
        }
        Toast.makeText(this, String.format("GPIO: %s参数错误", str), Toast.LENGTH_LONG).show();
    }
}
