package com.jostmate.IListen;

import android.content.Intent;

public interface OnReturnListen {
    void onSuess(Intent intent);

    void onErr(int code);
}
