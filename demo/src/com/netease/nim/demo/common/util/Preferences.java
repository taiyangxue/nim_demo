package com.netease.nim.demo.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.netease.nim.demo.DemoCache;


/**
 * Created by hzxuwen on 2015/4/13.
 */
public class Preferences {
    private static final String KEY_RTS_RECORD = "key_rts_record";
    private static final String KEY_AVCHAT_PIN = "key_avchat_pip";
    private static final String KEY_AUDIO_EFFECT = "key_audio_effect";

    public static void saveAudioEffectMode(String mode) {
        saveString(KEY_AUDIO_EFFECT, mode);
    }

    public static String getAudioEffectMode() {
        return getString(KEY_AUDIO_EFFECT);
    }

    public static void saveAVChatPin(int model) {
        saveInt(KEY_AVCHAT_PIN, model);
    }

    public static int getAVChatPip() {
        return getInt(KEY_AVCHAT_PIN, 2);
    }

    public static void saveRTSRecord(boolean isOpen) {
        saveBoolean(KEY_RTS_RECORD, isOpen);
    }

    public static boolean getRTSRecord() {
        return getBoolean(KEY_RTS_RECORD, false);
    }

    private static boolean getBoolean(String key, boolean value) {
        return getSharedPreferences().getBoolean(key, value);
    }

    private static void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private static void saveString(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static String getString(String key) {
        return getSharedPreferences().getString(key, null);
    }

    private static void saveInt(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private static int getInt(String key, int value) {
        return getSharedPreferences().getInt(key, value);
    }

    static SharedPreferences getSharedPreferences() {
        return DemoCache.getContext().getSharedPreferences("Demo", Context.MODE_PRIVATE);
    }
}
