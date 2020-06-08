package com.smartimagefinder.base;

import android.app.Application;

import com.smartimagefinder.repository.local.SearchLocal;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    public static synchronized MyApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        SearchLocal.getInstance().getTotalRowsFromDB();
    }
}
