package com.example.weibo_zhuyufeng.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weibo_zhuyufeng.R;

public class SplashScreenActivity extends AppCompatActivity {
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        SharedPreferences sharedPreferences = getSharedPreferences("first_key", Context.MODE_PRIVATE);
        //读取缓存
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            showDialog(sharedPreferences);
        }else {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
    private void showDialog(SharedPreferences sharedPreferences) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.privacy_popup, null);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.radius2);
        //点击用户协议和隐私政策
        click(view);
        //点击不同意和同意
        view.findViewById(R.id.frame_6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
        view.findViewById(R.id.frame_7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstRun", false);
                editor.apply();
                dialog.dismiss();
                startActivity(new Intent(SplashScreenActivity.this, HomeActivity.class));
            }
        });
        dialog.show();
    }
    public void click(View view) {
        TextView textView = view.findViewById(R.id.frame_9);
        String textContent = "欢迎使用 iH微博，我们将严格遵守相关法律和隐私政策保护您的个人隐私，请您阅读并同意《用户协议》与《隐私政策》";
        SpannableString spannableString = new SpannableString(textContent);
        ClickableSpan click1 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SplashScreenActivity.this, "用户协议", Toast.LENGTH_SHORT).show();
            }
        };
        ClickableSpan click2 = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SplashScreenActivity.this, "隐私政策", Toast.LENGTH_SHORT).show();
            }
        };

        int start1 = textContent.indexOf("《用户协议》");
        int end1 = start1 + "《用户协议》".length();
        spannableString.setSpan(click1, start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), start1, end1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int start2 = textContent.indexOf("《隐私政策》");
        int end2 = start2 + "《隐私政策》".length();
        spannableString.setSpan(click2, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}