package com.example.weibo_zhuyufeng;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class HomeActivity extends AppCompatActivity {

    private FragmentMy fragmentMy = new FragmentMy();
    private RecommendFragment recommendFragment = new RecommendFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        addFragment(recommendFragment);
        LinearLayout layout1 = findViewById(R.id.linearlayout1);
        LinearLayout layout2 = findViewById(R.id.linearlayout2);
        ImageView recommendImage = findViewById(R.id.recommend_image);
        ImageView myImage = findViewById(R.id.my_image);
        TextView recommendText = findViewById(R.id.recommend_text);
        TextView myText = findViewById(R.id.my_text);
        TextView toolText = findViewById(R.id.recommend_or_my);

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(recommendFragment);
                recommendImage.setImageResource(R.drawable.recommend_on);
                myImage.setImageResource(R.drawable.my_no);
                recommendText.setTextColor(getResources().getColor(R.color.blue));
                myText.setTextColor(getResources().getColor(R.color.black));
                toolText.setText("IH推荐");
                SharedPreferences sharedPreferences = getSharedPreferences("login_states", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("isLogin", false)) {

        }else {
            TextView logOut = findViewById(R.id.log_out);
            logOut.setText("退出登陆");
            logOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPreferences = getSharedPreferences("login_states", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("token"); // 删除token
                    editor.remove("isLogin"); // 删除登录状态
                    editor.apply();
                    logOut.setText("");
                }
            });
        }
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

    private void addFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view,fragment,null)
                .commit();
    }
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
}