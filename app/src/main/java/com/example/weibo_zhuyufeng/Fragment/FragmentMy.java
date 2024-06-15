package com.example.weibo_zhuyufeng.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.weibo_zhuyufeng.Activity.LogActivity;
import com.example.weibo_zhuyufeng.Model.LogMessage;
import com.example.weibo_zhuyufeng.Model.GetResponseBody;
import com.example.weibo_zhuyufeng.Model.LogoutMessage;
import com.example.weibo_zhuyufeng.R;
import com.example.weibo_zhuyufeng.Model.UserInfo;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
public class FragmentMy extends Fragment {

    private SharedPreferences sharedPreferences;
    private View view;
    private final Gson gson = new Gson();
    private TextView name;
    private TextView fans;
    private TextView myBefore;
    private TextView myAfter;
    private ImageView avatar;
    private static final String LOGIN_STATUS = "login_status";
    private Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_my, container, false);
        avatar = view.findViewById(R.id.avatar);
        name = view.findViewById(R.id.name);
        fans = view.findViewById(R.id.fans);
        myBefore = view.findViewById(R.id.my_content);
        myAfter = view.findViewById(R.id.my_after_content);
        sharedPreferences = requireActivity().getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        load();
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                afterLog();
                return true;
            }
        });
        return view;
    }
    public void load() {
        boolean isLoggedIn = sharedPreferences.getBoolean("isLogin",false);
        long expirationTime = sharedPreferences.getLong("expirationTime", 0);
        if (!isLoggedIn || System.currentTimeMillis() > expirationTime) {
            beforeLog();
            name.setFocusable(true);
            avatar.setFocusable(true);
        } else {
            String token = sharedPreferences.getString("token", null);
            if (token == null) {
                beforeLog();
                name.setFocusable(true);
                avatar.setFocusable(true);
            }else {
                Log.d("yyy", token);
                afterLog();
                name.setFocusable(false);
                avatar.setFocusable(false);
            }
        }
    }

    private void get(String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                // 构建请求对象
                Request request = new Request.Builder()
                        .url("https://hotfix-service-prod.g.mi.com/weibo/api/user/info")
                        .header("Authorization", "Bearer " + token)
                        .build();
                try {
                    // 同步发送请求
                    Response response = client.newCall(request).execute();
                    // 判断请求是否成功
                    if (response.isSuccessful()) {
                        sharedPreferences = getContext().getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        // 打印响应体
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        GetResponseBody getResponseBody = gson.fromJson(responseBody, GetResponseBody.class);
                        UserInfo userInfo = getResponseBody.getData();
                        int code = getResponseBody.getCode();
                        if (code == 403) {
                            editor.remove("token");
                            editor.apply();
                        } else {
                            editor.putString("avatar", userInfo.getAvatar());
                            editor.putString("name", userInfo.getUsername());
                            editor.apply();
                            // 使用Handler发送消息更新UI
                            handler.sendEmptyMessage(1);
                        }
                    } else {
                        System.out.println("请求失败：" + response.message());
                    }
                } catch (IOException e) {
                    // 发生异常，打印错误信息
                    e.printStackTrace();
                }
            }
        }).start(); // 启动线程
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LogMessage event) {
        String token = sharedPreferences.getString("token", null);
        get(token);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogoutEvent(LogoutMessage event) {
        beforeLog();
    }
    public void beforeLog() {
        name.setText("请先登录");
        fans.setText("点击头像登陆");
        RequestOptions options = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(this)
                .asBitmap()
                .load(R.drawable.default_avatar)
                .apply(options)
                .into(avatar);
        myBefore.setVisibility(View.VISIBLE);
        myAfter.setVisibility(View.GONE);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LogActivity.class));
            }
        });
        //点击头像登陆
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LogActivity.class));
            }
        });
    }
    public void afterLog() {
        String tokenAvatar = sharedPreferences.getString("avatar", null);
        String tokenName = sharedPreferences.getString("name", "祝宇锋大王");
        RequestOptions options = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(this)
                .asBitmap()
                .load(tokenAvatar)
                .apply(options)
                .into(avatar);
        fans.setText("粉丝:yjm");
        myBefore.setVisibility(View.GONE);
        myAfter.setVisibility(View.VISIBLE);
        name.setText(tokenName);
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
