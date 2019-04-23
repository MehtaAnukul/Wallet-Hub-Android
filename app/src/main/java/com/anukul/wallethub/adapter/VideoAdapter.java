package com.anukul.wallethub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anukul.wallethub.R;
import com.anukul.wallethub.listener.VideoOnItemClickListener;
import com.anukul.wallethub.model.VideoModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{
    private ArrayList<VideoModel> videoModelArrayList;
    private VideoOnItemClickListener videoOnItemClickListener;
    private Context context;

    public VideoAdapter(ArrayList<VideoModel> videoModelArrayList, VideoOnItemClickListener videoOnItemClickListener, Context context) {
        this.videoModelArrayList = videoModelArrayList;
        this.videoOnItemClickListener = videoOnItemClickListener;
        this.context = context;
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView duration;
        TextView size;
        TextView type;
        TextView label;
        ImageView thumb;
        VideoModel videoModel;
        public VideoViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.videoview_custom_layout_title);
            duration = itemView.findViewById(R.id.videoview_custom_layout_duration);
            size = itemView.findViewById(R.id.videoview_custom_layout_size);
            type = itemView.findViewById(R.id.videoview_custom_layout_type);
            label = itemView.findViewById(R.id.videoview_custom_layout_label);
            thumb = itemView.findViewById(R.id.videoview_custom_layout_thumb);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (videoOnItemClickListener != null) {
                videoOnItemClickListener.videoOnItemClick(videoModel, v);
            }
        }

        public void setData(VideoModel data) {
            this.videoModel = data;

            title.setText(videoModel.getVideoTitle());
            duration.setText(videoModel.getVideoDuration());
            type.setText(videoModel.getVideoType());
            size.setText(videoModel.getVideoSize());
            label.setText(videoModel.getVideoLabelPush());
            Glide.with(context)
                    .load(videoModel.getVideoThumbailUrl())
                    .into(thumb);
        }
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.videoview_custom_layout, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel videoModel = videoModelArrayList.get(position);

        holder.setData(videoModel);
    }

    @Override
    public int getItemCount() {
        return videoModelArrayList.size();
    }


}
