package sccbasdk.usbfingertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.joesmate.USB.UsbApi;
import com.joesmate.sdk.util.ToolFun;

public class MainActivity extends AppCompatActivity {

    UsbApi usbApi = null;
    TextView info = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onOpenClick(View view) {
        usbApi = new UsbApi(0x8195,  0x2796, this.getApplicationContext());
    }

    public void onGetFingerInfo(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] temp = new byte[7];
                byte[] fpdata = new byte[64];
                int i = 0;
                temp[i++] = 0;
                temp[i++] = 4;
                temp[i++] = 0x01;
                temp[i++] = 0;//(byte) (outTime & 0xff);
                temp[i++] = 0;
                temp[i++] = 0;
                temp[i++] = (byte) ToolFun.cr_bcc(6, temp);

                fpdata[0] = 2;
                System.arraycopy(temp, 0, fpdata, 1, 7);
                fpdata[8] = 3;
                int ret = usbApi.write(fpdata, 30, fpdata.length);

                byte[]in=new byte[64];
                ret=usbApi.read(in,30,64);
            }
        }).start();

    }
    public void onGetFinger(View view)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] temp = new byte[7];
                byte[] fpdata = new byte[64];
                int i = 0;
                temp[i++] = 0;
                temp[i++] = 4;
                temp[i++] = 0x09;
                temp[i++] = 0;//(byte) (outTime & 0xff);
                temp[i++] = 0;
                temp[i++] = 0;
                temp[i++] = (byte) ToolFun.cr_bcc(6, temp);

                fpdata[0] = 2;
                System.arraycopy(temp, 0, fpdata, 1, 7);
                fpdata[8] = 3;
                int ret = usbApi.write(fpdata, 30, fpdata.length);
                byte[]in=new byte[64];
                ret=usbApi.read(in,30,64);
            }
        }).start();
    }
}
