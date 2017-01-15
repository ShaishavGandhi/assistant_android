package com.example.shaishavgandhi.assistant.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.shaishavgandhi.assistant.network.APIManager;

import org.json.JSONObject;

/**
 * Created by shaishav.gandhi on 1/7/17.
 */

public class PreferenceSource {

    private SharedPreferences mPreferences;
    private static PreferenceSource mPreferenceSource;
    private SharedPreferences.Editor editor;
    public static final String KEY = "com.assistant.app";
    public static final String IP = KEY + "ip";
    public static final String TEMP_UNIT = KEY + "temp_unit";

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

    public String getTemperaturePreference() {
        return mPreferences.getString(TEMP_UNIT, "fahrenheit");
    }

    public void setTemperaturePreference(String unit) {
        editor.putString(TEMP_UNIT, unit);
        editor.commit();
    }

    public String constructPreferenceString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("temperature", getTemperaturePreference());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

}
