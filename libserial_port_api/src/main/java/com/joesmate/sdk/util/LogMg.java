package com.joesmate.sdk.util;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Date;
import java.text.SimpleDateFormat;


public class LogMg {
    public static final String TAG = "LogMg";
    public static boolean mShowLog = true;

    public static boolean mshowTcardlog = true;


    @SuppressLint("SimpleDateFormat")
    private static void writeLogInFileSystem(String content) {
        synchronized (LogMg.class) {
            String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String dirPath = sdcardPath + "/LogMg/";
            File file = null;
            RandomAccessFile randomAccessFile = null;

            if (!mshowTcardlog) {
                return;
            }

            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            content = sFormat.format(new Date(System.currentTimeMillis())) + "  " + content;


            try {
                file = new File(dirPath);


                if (!file.exists()) {
                    file.mkdir();
                }

                String filePath = dirPath + "/log.txt";

                file = new File(filePath);
                long currSize = file.length();

                randomAccessFile = new RandomAccessFile(filePath, "rwd");
                randomAccessFile.seek(currSize);

                byte[] buffer = content.getBytes();
                randomAccessFile.write(buffer);
                randomAccessFile.write(10);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void writeImageInFileSystem(byte[] content) {
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirPath = sdcardPath + "/Log/";
        File file = null;
        RandomAccessFile randomAccessFile = null;

        if (!mshowTcardlog) {
            return;
        }

        try {
            file = new File(dirPath);


            if (!file.exists()) {
                file.mkdir();
            }

            String filePath = dirPath + "/image";

            file = new File(filePath);
            long currSize = file.length();

            randomAccessFile = new RandomAccessFile(filePath, "rwd");
            randomAccessFile.seek(currSize);

            randomAccessFile.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (randomAccessFile != null) {
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void v(String tag, String msg) {
        if (mShowLog) {
            Log.v("LogMg", tag + ": " + msg);
        }
        if (mshowTcardlog)
            writeLogInFileSystem(tag + ": " + msg);
    }

    //------------------------------------------I-------------------------------------
    public static void i(String tag, String msg) {
        if (mShowLog) {
            Log.i("LogMg", tag + ": " + msg);
        }
        if (mshowTcardlog)
            writeLogInFileSystem(tag + ": " + msg);
    }
    public static void i(String tag, String msg,Object... args) {
        if (mShowLog) {
            Log.i("LogMg", tag + ": " + String.format(msg, args));
        }
        if (mshowTcardlog)
            writeLogInFileSystem(tag + ": " + String.format(msg, args));
    }
//-----------------err------------------------------
    public static void e(String tag, String msg) {
        if (mShowLog) {
            Log.e("LogMg", tag + ": " + msg);
        }
        if (mshowTcardlog)
            writeLogInFileSystem(tag + ": " + msg);
    }

    public static void e(String tag, String msg, Object... args) {
        if (mShowLog) {
            Log.e("LogMg", tag + ": " + String.format(msg, args));
        }
        if (mshowTcardlog)
            writeLogInFileSystem(tag + ": " + String.format(msg, args));
    }

    // -------------------------------------------Dubug

    public static void d(String tag, String msg) {
        if (mShowLog) {
            Log.d("LogMg", tag + ": " + msg);
        }
        if (mshowTcardlog) {
            writeLogInFileSystem(tag + ": " + msg);
        }
    }

    public static void d(String tag, String msg, Object... args) {
        if (mShowLog) {
            Log.d("LogMg", tag + ": " + String.format(msg, args));
        }
        if (mshowTcardlog) {
            writeLogInFileSystem(tag + ": " + String.format(msg, args));
        }
    }

}


