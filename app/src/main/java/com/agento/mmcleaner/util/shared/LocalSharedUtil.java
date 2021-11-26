package com.agento.mmcleaner.util.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;

import java.util.Date;

public class LocalSharedUtil {
    public static final String SHARED_FIRST = "mmcleaner_first";
    public static final String SHARED_SECOND = "mmcleaner_second";
    public static final String SHARED_THIRD = "mmcleaner_third";
    private static final String SHARED_FIRST_MAIN = "mmcleaner_main_first";

    public static void setParameter(SharedData value, String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        editor.putString(key, json);
        editor.apply();
    }

    public static SharedData getParameter(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        return gson.fromJson(preferences.getString(key, ""), SharedData.class);
    }

    public static void setSharedFirstMain(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(SHARED_FIRST_MAIN, true);
        editor.apply();
    }

    public static boolean isFirstMainShared(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(SHARED_FIRST_MAIN, false);
    }

    public static boolean isStepOptimized(Context context, String sharedKey) {
        SharedData data = LocalSharedUtil.getParameter(sharedKey, context);

        if(data == null)
            return false;
        return (Long.parseLong(data.date) + 43_200_000) > new Date().getTime();
    }
}
