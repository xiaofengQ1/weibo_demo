package com.example.weibo_zhuyufeng.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weibo_zhuyufeng.Adapter.CustomOkHttpClient;
import com.example.weibo_zhuyufeng.Model.LogMessage;
import com.example.weibo_zhuyufeng.Model.LoginResponse;
import com.example.weibo_zhuyufeng.R;
import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogActivity extends AppCompatActivity {

    //处理一天发送20条验证码逻辑
    private static final int MAX_SEND_COUNT = 20;
    private static final String PREFS_NAME = "verification_code_prefs";
    private static final String KEY_SEND_COUNT = "send_count";
    private static final String KEY_LAST_SEND_DATE = "last_send_date";

    private int countDown = 60;
    private boolean isCountingDown = false;
    private Handler handler;
    private TextView back;
    private TextView logText;
    private TextView getCode;
    private EditText phone;
    private EditText code;
    private static final String TAG = "777";
    private static String LOGIN_STATUS = "login_status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log);

        back = findViewById(R.id.back);
        logText = findViewById(R.id.confirm_log);
        getCode = findViewById(R.id.getCode);
        phone = findViewById(R.id.phone);
        code = findViewById(R.id.verification_code);
        phone.addTextChangedListener(textWatcher);
        code.addTextChangedListener(textWatcher);
        //点击获取验证码
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().length() != 11) {
                    Toast.makeText(LogActivity.this, "请输入完整手机号", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isCountingDown) {
                        if (canSendVerificationCode()) {
                            // 立即开始倒计时
                            isCountingDown = true;
                            getCode.setEnabled(false);
                            getCode.setText("获取验证码(" + countDown + ")");
                            handler.sendEmptyMessage(0);
                            sendVerificationCode(phone.getText().toString());
                            incrementSendCount();
                        } else {
                            Toast.makeText(LogActivity.this, "今天发送验证码次数已达上限", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        //点击登陆
        logText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(phone.getText().toString(), code.getText().toString());
            }
        });
        //返回主页
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //实时倒计时
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NotNull Message msg) {
                if (msg.what == 0) {
                    // 更新按钮文本
                    getCode.setText("获取验证码(" + countDown + ")");
                    // 继续倒计时
                    if (countDown > 0) {
                        handler.sendEmptyMessageDelayed(0, 1000);
                        countDown--;
                    } else {
                        // 倒计时结束
                        getCode.setText("获取验证码");
                        isCountingDown = false;
                        countDown = 60;
                        getCode.setEnabled(true);
                    }
                }
                return true;
            }
        });
    }
    //登陆状态监控
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TextView logText = findViewById(R.id.confirm_log);
            EditText phone = findViewById(R.id.phone);
            EditText code = findViewById(R.id.verification_code);

            if (phone.getText().length() == 11 && code.getText().length() == 6) {
                logText.setBackgroundResource(R.drawable.radius_full_blue);
                logText.setEnabled(true);
            } else {
                logText.setBackgroundResource(R.drawable.radius_full_gray);
                //手机号码或者验证码不全不能登陆
                logText.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    //发送验证码
    private void sendVerificationCode(String phoneNumber) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"phone\": \"" + phoneNumber + "\"}";
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("https://hotfix-service-prod.g.mi.com/weibo/api/auth/sendCode")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LogActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LogActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //登陆
    private void login(String phoneNumber, String verificationCode) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = "{\"phone\": \"" + phoneNumber + "\", \"smsCode\": \"" + verificationCode + "\"}";
        RequestBody requestBody = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("https://hotfix-service-prod.g.mi.com/weibo/api/auth/login")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LogActivity.this, "网络错误，请稍后再试", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String responseData = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Gson gson = new Gson();
                                LoginResponse loginResponse = gson.fromJson(responseData, LoginResponse.class);
                                if (loginResponse.getCode() == 200) {
                                    String token = loginResponse.getData();
                                    SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    Toast.makeText(LogActivity.this, "登录成功：你好" + token, Toast.LENGTH_SHORT).show();
                                    long expirationTime = System.currentTimeMillis() + (12 * 60 * 60 * 1000); // 12 小时后的时间戳
                                    editor.putLong("expirationTime", expirationTime);
                                    editor.putBoolean("isLogin", true);
                                    editor.putString("token", token);
                                    editor.apply();
                                    EventBus.getDefault().post(new LogMessage(true));
                                    finish();
                                    Log.d("yyy", token);
                                }
                                else if (loginResponse.getCode() == 403){
                                    // 登录失败，显示错误信息
                                    SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove("token");
                                    editor.apply();
                                    String errorMessage = loginResponse.getMessage();
                                    Toast.makeText(LogActivity.this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                                }else {
                                    String errorMessage = loginResponse.getMessage();
                                    Toast.makeText(LogActivity.this, "登录失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JsonSyntaxException e) {
                                e.printStackTrace();
                                // JSON 解析出错，显示错误消息
                                Toast.makeText(LogActivity.this, "解析响应数据失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //网络错误
                else {
                    // 响应不成功，处理错误情况
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LogActivity.this, "网络错误或响应为空", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
    private void incrementSendCount() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int sendCount = preferences.getInt(KEY_SEND_COUNT, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_SEND_COUNT, ++sendCount);
        editor.apply();
    }
    private boolean canSendVerificationCode() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int sendCount = preferences.getInt(KEY_SEND_COUNT, 0);
        String lastSendDate = preferences.getString(KEY_LAST_SEND_DATE, "");
        // 获取当前日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (!currentDate.equals(lastSendDate)) {
            // 如果当前日期与最后一次发送日期不一致，重置发送次数
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(KEY_SEND_COUNT, 0);
            editor.putString(KEY_LAST_SEND_DATE, currentDate);
            editor.apply();
            sendCount = 0;
        }
        return sendCount < MAX_SEND_COUNT;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}