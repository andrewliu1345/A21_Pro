package com.joesmate.idcreader;

/**
 * Created by AndrewLiu on 2016/5/10 .
 */
public class HandImage {
    public static  native  int DecWlt2Bmp(byte [] wltbuffer,byte[] bmpbuffer);
    static {
        System.loadLibrary("IDCReader");
    }
}
