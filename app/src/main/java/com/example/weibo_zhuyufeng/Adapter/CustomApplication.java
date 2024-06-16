package com.example.weibo_zhuyufeng.Adapter;

import android.app.Application;

public class CustomApplication extends Application {

    private static CustomApplication instance;
    private static final String LOGIN_STATUS = "login_status";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
    public static CustomApplication getInstance() {
        return instance;
    }
}
