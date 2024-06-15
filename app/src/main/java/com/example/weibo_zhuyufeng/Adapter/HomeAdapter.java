package com.example.weibo_zhuyufeng.Adapter;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.weibo_zhuyufeng.Model.WeiboInfo;
import com.example.weibo_zhuyufeng.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeItemHolder>{

    private List<WeiboInfo> weiboInfoList = new ArrayList<>();
    private Handler handler = new Handler();

    public HomeAdapter(List<WeiboInfo> weiboInfoList) {
        this.weiboInfoList = weiboInfoList;
    }
    @NonNull
    @Override
    public HomeItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item,parent,false);
        return new HomeItemHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull HomeItemHolder holder, int position) {
        WeiboInfo weiboInfo = weiboInfoList.get(position);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    weiboInfoList.remove(position);
                    notifyItemRemoved(position);
                }
            }
        });
        holder.name.setText(weiboInfo.getUsername());
        holder.title.setText(weiboInfo.getTitle());
        RequestOptions options = new RequestOptions()
                .transform(new CircleCrop());
        Glide.with(holder.itemView.getContext())
                .load(weiboInfo.getAvatar())
                .apply(options)
                .into(holder.avatar);
        holder.likeText.setText(String.valueOf(weiboInfo.getLikeCount()));
        // 根据帖子类型设置内容可见性和加载数据
        if (weiboInfo.getVideoUrl() != null ) {
//            ViewGroup.LayoutParams layoutParams = holder.frameLayout.getLayoutParams();
//            float scale = holder.itemView.getResources().getDisplayMetrics().density;
//            int dpHeightInPx = (int) (200 * scale + 0.5f);
//            layoutParams.height = dpHeightInPx;
//            holder.frameLayout.setLayoutParams(layoutParams);
//
//            // 显示视频布局，隐藏其他
//            holder.videoLayout.setVisibility(View.VISIBLE);
//            holder.singleImage.setVisibility(View.GONE);
//            holder.gridRecyclerView.setVisibility(View.GONE);
//
//            // 加载视频封面图
//            Glide.with(holder.itemView.getContext()).load(weiboInfo.getPoster()).into(holder.videoPoster);
//
//            // 初始化MediaPlayer
//            if (holder.mediaPlayer == null) {
//                holder.mediaPlayer = new MediaPlayer();
//                holder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//                try {
//                    holder.mediaPlayer.setDataSource(holder.itemView.getContext(), Uri.parse(weiboInfo.getVideoUrl()));
//                    holder.mediaPlayer.prepareAsync();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    holder.videoProgress.setVisibility(View.GONE);
//                    holder.videoPoster.setVisibility(View.VISIBLE);
//                    holder.btnPlay.setVisibility(View.VISIBLE);
//                }
//
//                holder.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        holder.videoProgress.setVisibility(View.GONE);
//                        // 不自动播放，只显示封面和按钮
//                        holder.videoPoster.setVisibility(View.VISIBLE);
//                        holder.btnPlay.setVisibility(View.VISIBLE);
//                    }
//                });
//
//                holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        // 播放完成后的处理
//                    }
//                });
//
//                holder.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//                    @Override
//                    public boolean onError(MediaPlayer mp, int what, int extra) {
//                        // 错误处理
//                        holder.videoProgress.setVisibility(View.GONE);
//                        holder.videoPoster.setVisibility(View.VISIBLE);
//                        holder.btnPlay.setVisibility(View.VISIBLE);
//                        return true;
//                    }
//                });
//            }
//
//            // 点击播放按钮的逻辑
//            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (holder.mediaPlayer != null && holder.mediaPlayer.isPlaying()) {
//                        // 暂停播放
//                        holder.mediaPlayer.pause();
//                        holder.videoProgress.setVisibility(View.GONE);
//                        holder.videoPoster.setVisibility(View.VISIBLE);
//                        holder.btnPlay.setVisibility(View.VISIBLE);
//                    } else {
//                        // 开始播放
//                        holder.videoProgress.setVisibility(View.VISIBLE);
//                        holder.videoPoster.setVisibility(View.GONE);
//                        holder.btnPlay.setVisibility(View.GONE);
//
//                        if (holder.mediaPlayer != null) {
//                            holder.mediaPlayer.setDisplay(holder.videoSurface.getHolder());
//                            holder.mediaPlayer.start();
//                            startProgressUpdater(holder); // 开始更新进度条
//                        }
//                    }
//                }
//            });
            ViewGroup.LayoutParams layoutParams = holder.frameLayout.getLayoutParams();
            float scale = holder.itemView.getResources().getDisplayMetrics().density;
            int dpHeightInPx = (int) (200 * scale + 0.5f);
            layoutParams.height = dpHeightInPx;
            holder.frameLayout.setLayoutParams(layoutParams);

            holder.videoLayout.setVisibility(View.VISIBLE);
            holder.singleImage.setVisibility(View.GONE);
            holder.gridRecyclerView.setVisibility(View.GONE);

            Glide.with(holder.itemView.getContext()).load(weiboInfo.getPoster()).into(holder.videoPoster);

            if (holder.mediaPlayer == null) {
                holder.mediaPlayer = new MediaPlayer();
                holder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    holder.mediaPlayer.setDataSource(holder.itemView.getContext(), Uri.parse(weiboInfo.getVideoUrl()));
                    holder.mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                    holder.videoProgress.setVisibility(View.GONE);
                    holder.videoPoster.setVisibility(View.VISIBLE);
                    holder.btnPlay.setVisibility(View.VISIBLE);
                }

                holder.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        holder.videoProgress.setVisibility(View.GONE);
                        holder.videoPoster.setVisibility(View.VISIBLE);
                        holder.btnPlay.setVisibility(View.VISIBLE);
                    }
                });

                holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // 播放完成后的处理
                    }
                });

                holder.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        holder.videoProgress.setVisibility(View.GONE);
                        holder.videoPoster.setVisibility(View.VISIBLE);
                        holder.btnPlay.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
            }

            holder.btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mediaPlayer != null && holder.mediaPlayer.isPlaying()) {
                        holder.mediaPlayer.pause();
                        holder.videoProgress.setVisibility(View.GONE);
                        holder.videoPoster.setVisibility(View.VISIBLE);
                        holder.btnPlay.setVisibility(View.VISIBLE);
                    } else {
                        holder.videoProgress.setVisibility(View.VISIBLE);
                        holder.videoPoster.setVisibility(View.GONE);
                        holder.btnPlay.setVisibility(View.GONE);

                        if (holder.mediaPlayer != null) {
                            holder.mediaPlayer.setDisplay(holder.videoSurface.getHolder());
                            holder.mediaPlayer.start();
                            startProgressUpdater(holder);
                        }
                    }
                }
            });
        }
        else if (weiboInfo.getImages() != null && !weiboInfo.getImages().isEmpty()) {
            // 显示图片布局，隐藏其他
            holder.videoLayout.setVisibility(View.GONE);
            holder.singleImage.setVisibility(View.VISIBLE);
            holder.gridRecyclerView.setVisibility(View.GONE);
            // 判断是单张图片还是多张图片
            if (weiboInfo.getImages().size() == 1) {
                // 单张图片
                holder.singleImage.setVisibility(View.VISIBLE);
                holder.gridRecyclerView.setVisibility(View.GONE);
                // 获取单张图片的 URL
                String imageUrl = weiboInfo.getImages().get(0);
                // 定义 RequestOptions，设置图片的转换器
                RequestOptions options2 = new RequestOptions();
                // 使用 Glide 加载图片并应用 RequestOptions
                Glide.with(holder.itemView.getContext())
                        .load(imageUrl)
                        .apply(options2)
                        .into(new CustomTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                // 获取图片的宽高信息
                                int width = resource.getIntrinsicWidth();
                                int height = resource.getIntrinsicHeight();

                                // 根据宽高比例判断显示横图或竖图样式
                                if (width > height) {
                                    // 横图样式

                                } else {
                                    // 竖图样式
                                    holder.singleImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                }
                                // 将图片设置到 ImageView 中
                                holder.singleImage.setImageDrawable(resource);
                            }
                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                // 可选的清除加载时的处理
                            }
                        });
            } else {
                // 多张图片，设置九宫格图片适配器
                int x = (weiboInfo.getImages().size() / 3) + (weiboInfo.getImages().size() % 3 > 0 ? 1 : 0);
                ViewGroup.LayoutParams layoutParams = holder.frameLayout.getLayoutParams();
                float scale = holder.itemView.getResources().getDisplayMetrics().density;
                int dpHeightInPx = (int) (x * 135 * scale + 0.5f);
                layoutParams.height = dpHeightInPx;
                holder.frameLayout.setLayoutParams(layoutParams);
                holder.singleImage.setVisibility(View.GONE);
                holder.gridRecyclerView.setVisibility(View.VISIBLE);
                GridLayoutManager layoutManager = new GridLayoutManager(holder.itemView.getContext(), 3);
                holder.gridRecyclerView.setLayoutManager(layoutManager);
                GridImageAdapter gridAdapter = new GridImageAdapter(weiboInfo.getImages());
                holder.gridRecyclerView.setAdapter(gridAdapter);
            }
        }
        else {
            // 没有图片和视频时，隐藏所有内容
            holder.videoLayout.setVisibility(View.GONE);
            holder.singleImage.setVisibility(View.GONE);
            holder.gridRecyclerView.setVisibility(View.GONE);
        }
    }
    private void setupMediaPlayer(HomeItemHolder holder, WeiboInfo weiboInfo) {
        // 初始化MediaPlayer
        if (holder.mediaPlayer == null) {
            holder.mediaPlayer = new MediaPlayer();
            holder.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            try {
                holder.mediaPlayer.setDataSource(holder.itemView.getContext(), Uri.parse(weiboInfo.getVideoUrl()));
                holder.mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
                holder.videoProgress.setVisibility(View.GONE);
                holder.videoPoster.setVisibility(View.VISIBLE);
                holder.btnPlay.setVisibility(View.VISIBLE);
            }

            holder.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    holder.videoProgress.setVisibility(View.GONE);
                    // 不自动播放，只显示封面和按钮
                    holder.videoPoster.setVisibility(View.VISIBLE);
                    holder.btnPlay.setVisibility(View.VISIBLE);
                }
            });

            holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 播放完成后的处理
                }
            });

            holder.mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // 错误处理
                    holder.videoProgress.setVisibility(View.GONE);
                    holder.videoPoster.setVisibility(View.VISIBLE);
                    holder.btnPlay.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
    }
    @Override
    public void onViewRecycled(@NonNull HomeItemHolder holder) {
        super.onViewRecycled(holder);
        // 视频回收时停止播放和更新进度条
        if (holder.mediaPlayer != null) {
            holder.mediaPlayer.stop();
            holder.mediaPlayer.reset();
            holder.mediaPlayer.release();
            holder.mediaPlayer = null;
        }
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public int getItemCount() {
        return weiboInfoList.size();
    }
    private void startProgressUpdater(HomeItemHolder holder) {
        holder.videoProgress.setMax(holder.mediaPlayer.getDuration());

        Runnable updater = new Runnable() {
            @Override
            public void run() {
                if (holder.mediaPlayer != null && holder.mediaPlayer.isPlaying()) {
                    int currentPosition = holder.mediaPlayer.getCurrentPosition();
                    holder.videoProgress.setProgress(currentPosition);
                    handler.postDelayed(this, 100); // 100毫秒更新一次进度条
                }
            }
        };

        handler.post(updater);
    }

    public class HomeItemHolder extends RecyclerView.ViewHolder{
        ImageView avatar;
        TextView name;
        ImageView delete;
        TextView title;
        RelativeLayout videoLayout;
        ImageView videoPoster;
        ImageView btnPlay;
        ImageView singleImage;
        RecyclerView gridRecyclerView;
        ImageView like;
        TextView likeText;
        TextView commentText;
        FrameLayout frameLayout;

        SurfaceView videoSurface;
        ProgressBar videoProgress;
        MediaPlayer mediaPlayer;

        public HomeItemHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.item_avatar);
            name = itemView.findViewById(R.id.item_name);
            delete = itemView.findViewById(R.id.delete_item);
            title = itemView.findViewById(R.id.item_title);
            videoLayout = itemView.findViewById(R.id.item_video);
            videoPoster = itemView.findViewById(R.id.video_poster);
            singleImage = itemView.findViewById(R.id.single_image);
            gridRecyclerView = itemView.findViewById(R.id.grid_recycler_view);
            like = itemView.findViewById(R.id.item_like);
            likeText = itemView.findViewById(R.id.like_text);
            commentText = itemView.findViewById(R.id.comment_text);
            frameLayout = itemView.findViewById(R.id.content_frame);

            btnPlay = itemView.findViewById(R.id.btn_play);
            videoSurface = itemView.findViewById(R.id.video_surface);
            videoProgress = itemView.findViewById(R.id.video_progress);
        }
    }
}
