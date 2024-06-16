package com.example.weibo_zhuyufeng.Activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weibo_zhuyufeng.Adapter.BigImageViewPagerAdapter;
import com.example.weibo_zhuyufeng.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private TextView text1;
    private TextView text2;
    private ImageView avatar;
    private TextView name;
    private List<String> imageUrls;
    private ViewPager viewPager;
    private TextView xiazai;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private String userName;
    private String avatarUrl;
    private int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_big_image);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        name = findViewById(R.id.big_name);
        text2 = findViewById(R.id.big_text2);
        text1 = findViewById(R.id.big_text1);
        avatar = findViewById(R.id.big_avatar);
        viewPager = findViewById(R.id.view_pager);
        xiazai = findViewById(R.id.xiazai);
        Intent intent = getIntent();
        if (intent != null) {
            userName = intent.getStringExtra("name");
            avatarUrl = intent.getStringExtra("avatar");
            imageUrls = intent.getStringArrayListExtra("urls");
            boolean nine = intent.getBooleanExtra("nine", false);
            position = intent.getIntExtra("position", 100);
            if (nine) {
                much();
            }
            else {
                single();
            }
        }
        xiazai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_STORAGE);
                } else {
                    downloadImage();
                }
            }
        });
        View mainView = findViewById(R.id.main);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 关闭当前Activity
            }
        });
    }
    private void downloadImage() {
        int currentItem = viewPager.getCurrentItem();
        String imageUrl = imageUrls.get(currentItem);

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @NonNull Transition<? super Bitmap> transition) {
                        saveImageToGallery(resource);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
    private void saveImageToGallery(Bitmap bitmap) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Weibo");
                values.put(MediaStore.Images.Media.IS_PENDING, true);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fos = getContentResolver().openOutputStream(uri);
                values.put(MediaStore.Images.Media.IS_PENDING, false);
                getContentResolver().update(uri, values, null, null);
            } else {
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Weibo");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                File file = new File(directory, System.currentTimeMillis() + ".jpg");
                fos = new FileOutputStream(file);
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (fos != null) {
                fos.close();
            }
            Toast.makeText(this, "图片下载完成，请到相册查看", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "图片下载失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadImage();
            } else {
                Toast.makeText(this, "需要权限才能下载图片", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void single() {
        if (userName != null) {
            name.setText(userName);
        }
        if (avatarUrl != null) {
            RequestOptions options = new RequestOptions()
                    .transform(new CircleCrop());
            Glide.with(this)
                    .asBitmap()
                    .load(avatarUrl)
                    .apply(options)
                    .into(avatar);
        }
        if (imageUrls != null) {
            text2.setText(imageUrls.size()+"");
            BigImageViewPagerAdapter adapter = new BigImageViewPagerAdapter(ImageActivity.this, imageUrls);
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // Do nothing
                }
                @Override
                public void onPageSelected(int position) {
                    text1.setText(position+1+"");
                }
                @Override
                public void onPageScrollStateChanged(int state) {
                    // Do nothing
                }
            });
        }
    }
    public void much() {
        if (userName != null) {
            name.setText(userName);
        }
        if (avatarUrl != null) {
            RequestOptions options = new RequestOptions()
                    .transform(new CircleCrop());
            Glide.with(this)
                    .asBitmap()
                    .load(avatarUrl)
                    .apply(options)
                    .into(avatar);
        }

        if (position != 100) {
            text1.setText(position + 1 + "");
            if (imageUrls != null) {
                text2.setText(imageUrls.size()+"");
                BigImageViewPagerAdapter adapter = new BigImageViewPagerAdapter(ImageActivity.this, imageUrls);
                viewPager.setAdapter(adapter);
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        // Do nothing
                    }
                    @Override
                    public void onPageSelected(int position) {
                        text1.setText(position+1+"");
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
            viewPager.setCurrentItem(position);
        }
    }
}