package com.joesmate;

import android.content.Context;

/**
 * Created by andre on 2017/9/17 .
 */

public interface BaesTextToSpeech {

    /**
     * 发声
     *
     * @param text
     */
    public abstract void speak(final String text);

    /**
     * 设置context
     *
     * @param context
     */
    public abstract void setContext(Context context);

    /**
     * 关闭
     */
    public abstract void Close();

}
