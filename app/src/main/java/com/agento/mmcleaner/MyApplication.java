package com.agento.mmcleaner;

import android.app.Application;

/**
 * Created by mazhuang on 2016/12/8.
 */

public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static Application getInstance() {
        return sInstance;
    }

    public MyApplication() {
        sInstance = this;
    }

    public static MyApplication get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

    }
}
