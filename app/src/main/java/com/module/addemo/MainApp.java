package com.module.addemo;

import android.app.Application;

import com.module.ad.main.AdMain;

public class MainApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        AdMain.getInstance().init(this);
    }
}
