package com.example.shaishavgandhi.assistant.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.shaishavgandhi.assistant.network.APIManager;

/**
 * Created by shaishav.gandhi on 1/7/17.
 */

public class PreferenceSource {

    private SharedPreferences mPreferences;
    private static PreferenceSource mPreferenceSource;
    private SharedPreferences.Editor editor;
    private static final String KEY = "com.assistant.app";
    private static final String IP = KEY + "ip";

    private PreferenceSource(Context context) {
        mPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    public static PreferenceSource getInstance(Context context) {
        if (mPreferenceSource == null) {
            mPreferenceSource = new PreferenceSource(context);
        }

        return mPreferenceSource;
    }

    public String getIp() {
        return mPreferences.getString(IP, "");
    }

    public void setIp(String ip) {
        editor.putString(IP, ip);
        editor.commit();
        APIManager.reset();
    }

}
