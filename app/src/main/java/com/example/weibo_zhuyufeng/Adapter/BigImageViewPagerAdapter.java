package com.example.weibo_zhuyufeng.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weibo_zhuyufeng.R;

import java.util.List;

public class BigImageViewPagerAdapter extends PagerAdapter {
    private Context context;
    private List<String> imageUrls;

    public BigImageViewPagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }
    @Override
    public int getCount() {
        return imageUrls.size();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_image, container, false);
        ImageView imageView = view.findViewById(R.id.imageView);
        Glide.with(context)
                .load(imageUrls.get(position))
                .apply(new RequestOptions().fitCenter())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int width = resource.getIntrinsicWidth();
                        int height = resource.getIntrinsicHeight();
                        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
                        int screenHeight = context.getResources().getDisplayMetrics().heightPixels;

                        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                        layoutParams.width = screenWidth;

                        if (height * screenWidth / width <= screenHeight) {
                            layoutParams.height = height * screenWidth / width;
                            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        } else {
                            layoutParams.height = screenHeight;
                            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }

                        imageView.setLayoutParams(layoutParams);
                        imageView.setImageDrawable(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
