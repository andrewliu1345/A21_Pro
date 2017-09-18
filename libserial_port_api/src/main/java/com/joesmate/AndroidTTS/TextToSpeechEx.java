package com.joesmate.AndroidTTS;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import com.joesmate.BaesTextToSpeech;

import java.util.Locale;

/**
 * Created by andre on 2017/9/17 .
 */

public class TextToSpeechEx implements BaesTextToSpeech {
    Context m_context;
    private TextToSpeech tts;

    private TextToSpeechEx() {
    }

    public TextToSpeechEx(Context context) {
        m_context = context;

        initialTts();
    }

    @Override
    public void speak(String text) {


        if (Build.VERSION.SDK_INT > 21) {

            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speech");

        } else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }


    }

    @Override
    public void setContext(Context context) {
        m_context = context;
    }

    @Override
    public void Close() {
        tts.shutdown();
    }

    private void initialTts() {
        tts = new TextToSpeech(m_context, TTS_InitListener);
    }

    TextToSpeech.OnInitListener TTS_InitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                //  设置中文朗读
                int result = tts.setLanguage(Locale.CHINA);
                //  若不支持所设置的语言
                if (result != TextToSpeech.LANG_AVAILABLE &&
                        result != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                }
            }
        }
    };
}
