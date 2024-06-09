package com.example.weibo_zhuyufeng;

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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.annotations.NotNull;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LogActivity extends AppCompatActivity {

    private int countDown = 60;
    private boolean isCountingDown = false;
    private Handler handler;
    private TextView back;
    private TextView logText;
    private TextView getCode;

    private EditText phone;
    private EditText code;

    private static final String TAG = "777";

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
        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phone.getText().length() != 11) {
                    Toast.makeText(LogActivity.this, "请输入完整手机号", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isCountingDown) {
                        sendVerificationCode(phone.getText().toString());
                    }
                }
            }
        });

        logText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                login(phone.getText().toString(), code.getText().toString());
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
                    }
                }
                return true;
            }
        });
    }
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
                logText.setBackgroundResource(R.drawable.radius_full_gray); // Assume you have a grey background drawable
                logText.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

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
                    isCountingDown = true;
                    handler.sendEmptyMessage(0);
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

    private void login(String phoneNumber, String verificationCode) {
        OkHttpClient client = new OkHttpClient();

        // 构建 JSON 格式的请求体
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
                                    SharedPreferences sharedPreferences = getSharedPreferences("login_states", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putBoolean("isLogin", true);
                                    editor.putString("token", token);
                                    editor.apply();
                                    Log.d("yyy", token);
                                    finish();
                                } else {
                                    // 登录失败，显示错误信息
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
                } else {
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
}