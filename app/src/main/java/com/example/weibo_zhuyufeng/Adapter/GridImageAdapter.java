package com.example.weibo_zhuyufeng.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.weibo_zhuyufeng.Activity.ImageActivity;
import com.example.weibo_zhuyufeng.Model.WeiboInfo;
import com.example.weibo_zhuyufeng.R;

import java.util.ArrayList;
import java.util.List;

public class GridImageAdapter extends RecyclerView.Adapter<GridImageAdapter.ImageViewHolder> {

    private List<String> images;
    private WeiboInfo weiboInfo;

    public GridImageAdapter(List<String> images, WeiboInfo weiboInfo) {
        this.images = images;
        this.weiboInfo = weiboInfo;
    }

    @NonNull
    @Override
    public GridImageAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridImageAdapter.ImageViewHolder holder, int position) {
        String imageUrl = images.get(position);

        Glide.with(holder.itemView.getContext()).load(imageUrl).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ImageActivity.class);
                intent.putExtra("nine", true);
                intent.putExtra("name", weiboInfo.getUsername());
                intent.putExtra("avatar", weiboInfo.getAvatar());
                intent.putStringArrayListExtra("urls", new ArrayList<>(weiboInfo.getImages()));
                intent.putExtra("position", position);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.grid_image);
        }
    }
}
