package com.example.weibo_zhuyufeng.Adapter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.weibo_zhuyufeng.Model.LogoutMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomOkHttpClient {
    private static OkHttpClient client;
    private static final String LOGIN_STATUS = "login_status";
    private static CustomApplication instance;

    public static OkHttpClient getClient() {
        if (client == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();

            builder.addInterceptor((Interceptor) new TokenInterceptor());

            client = builder.build();
        }
        return client;
    }

    private static class TokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Response response = chain.proceed(originalRequest);

            if (response.code() == 403) {

                handleForbiddenResponse();
            }

            return response;
        }

        private void handleForbiddenResponse() {
            SharedPreferences sharedPreferences = CustomApplication.getInstance().getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("token"); // 删除token
            editor.remove("isLogin"); // 删除登录状态
            editor.apply();
            EventBus.getDefault().post(new LogoutMessage(true));
        }
    }
}
