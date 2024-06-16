package com.example.weibo_zhuyufeng.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.weibo_zhuyufeng.Model.LogMessage;
import com.example.weibo_zhuyufeng.Fragment.FragmentMy;
import com.example.weibo_zhuyufeng.Model.LogoutMessage;
import com.example.weibo_zhuyufeng.R;
import com.example.weibo_zhuyufeng.Fragment.RecommendFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends AppCompatActivity {

    private FragmentMy fragmentMy = new FragmentMy();
    private RecommendFragment recommendFragment = new RecommendFragment();
    private static String LOGIN_STATUS = "login_status";
    private TextView logOut;
    private TextView toolText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        EventBus.getDefault().register(this);
        addFragment(recommendFragment);
        LinearLayout layout1 = findViewById(R.id.linearlayout1);
        LinearLayout layout2 = findViewById(R.id.linearlayout2);
        ImageView recommendImage = findViewById(R.id.recommend_image);
        ImageView myImage = findViewById(R.id.my_image);
        TextView recommendText = findViewById(R.id.recommend_text);
        TextView myText = findViewById(R.id.my_text);
        toolText = findViewById(R.id.recommend_or_my);
        logOut = findViewById(R.id.log_out);
        SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        long expirationTime = sharedPreferences.getLong("expirationTime", 0);
        if (sharedPreferences.getBoolean("isLogin", false) && System.currentTimeMillis() < expirationTime) {
            logOut.setText("退出登陆");
        }else {
            logOut.setText("");
        }
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token"); // 删除token
                editor.remove("isLogin"); // 删除登录状态
                editor.apply();
                logOut.setText("");
                EventBus.getDefault().post(new LogoutMessage(true));
            }
        });
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(recommendFragment);
                recommendImage.setImageResource(R.drawable.recommend_on);
                myImage.setImageResource(R.drawable.my_no);
                recommendText.setTextColor(getResources().getColor(R.color.blue));
                myText.setTextColor(getResources().getColor(R.color.black));
                toolText.setText("IH推荐");
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(fragmentMy);
                recommendImage.setImageResource(R.drawable.recommend_no);
                myImage.setImageResource(R.drawable.my_on);
                recommendText.setTextColor(getResources().getColor(R.color.black));
                myText.setTextColor(getResources().getColor(R.color.blue));
                toolText.setText("我的");
            }
        });
    }
    //初始状态用来初始推荐页面
    private void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view,fragment,null)
                .commit();
    }

    //更换我的或推荐页面
    private void replaceFragment(Fragment fragment){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, fragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LogMessage event) {
        logOut.setText("退出登陆");
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("token"); // 删除token
                editor.remove("isLogin"); // 删除登录状态
                editor.apply();
                logOut.setText("");
                EventBus.getDefault().post(new LogoutMessage(true));
            }
        });
    }
}