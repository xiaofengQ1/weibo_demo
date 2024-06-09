package com.example.weibo_zhuyufeng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FragmentMy extends Fragment {

    private SharedPreferences sharedPreferences;

    private View view;

    private final Gson gson = new Gson();
    private UserInfo userInfo;
    private TextView name;
    private TextView fans;
    private TextView my;
    private ImageView avatar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_my, container, false);
        avatar = view.findViewById(R.id.avatar);
        name = view.findViewById(R.id.name);
        fans = view.findViewById(R.id.fans);
        my = view.findViewById(R.id.my_text);
        sharedPreferences = requireActivity().getSharedPreferences("login_states", Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLogin",false);
        if (!isLoggedIn) {
            name.setText("请先登录");
            fans.setText("点击头像登陆");
            RequestOptions options = new RequestOptions()
                    .transform(new CircleCrop());
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.default_avatar)
                    .apply(options)
                    .into(avatar);
        } else {
            String token = sharedPreferences.getString("token", "null");
            if (token.equals("null")) {
                name.setText("请先登录");
                fans.setText("点击头像登陆");
                RequestOptions options = new RequestOptions()
                        .transform(new CircleCrop());
                Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.default_avatar)
                        .apply(options)
                        .into(avatar);
            } else {
//                fetchData(token);
                RequestOptions options = new RequestOptions()
                        .transform(new CircleCrop());
                Glide.with(this)
                        .asBitmap()
                        .load(R.drawable.me)
                        .apply(options)
                        .into(avatar);
                name.setText("祝宇锋大王");
                my.setText("你没有新的动态");
                fans.setText("我超级多粉丝");
            }
        }
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LogActivity.class));
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LogActivity.class));
            }
        });
        return view;
    }

    private void fetchData(String token) {
        OkHttpClient client = new OkHttpClient();
        // 构建请求
        Request request = new Request.Builder()
                .url("https://example.com/api/data") // 替换为您的API端点
                .header("Authorization", "Bearer " + token) // 设置token
                .build();
        // 发起异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 请求失败处理逻辑
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 在主线程中显示错误消息
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String responseData = response.body().string();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                String jsonData = response.body().string();
                                GetResponse getResponse = gson.fromJson(jsonData, GetResponse.class);
                                userInfo = getResponse.getUserInfo();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });
                } else {
                    // 响应不成功，处理错误情况
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        }
                    });
                }
            }
        });
}
}