package com.example.weibo_zhuyufeng.Adapter;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.weibo_zhuyufeng.Model.WeiboInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WeiboDataManager {
    private static final String PREF_WEIBO_LIST = "weibo_list";
    private static final String LOGIN_STATUS = "login_status";

    // 保存微博列表到SharedPreferences
    public static void saveWeiboList(Context context, List<WeiboInfo> weiboList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(weiboList);
        editor.putString(PREF_WEIBO_LIST, json);
        editor.apply();
    }

    // 从SharedPreferences加载微博列表
    public static List<WeiboInfo> loadWeiboList(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(PREF_WEIBO_LIST, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<WeiboInfo>>() {}.getType();
            return gson.fromJson(json, type);
        }
        return new ArrayList<>();
    }
}
