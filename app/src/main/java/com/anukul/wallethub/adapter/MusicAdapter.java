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
import com.anukul.wallethub.listener.MusicOnItemClickListener;
import com.anukul.wallethub.model.MusicModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private ArrayList<MusicModel> musicModelArrayList;
    private MusicOnItemClickListener musicOnItemClickListener;
    private Context context;


    public MusicAdapter(ArrayList<MusicModel> musicModelArrayList, MusicOnItemClickListener musicOnItemClickListener, Context context) {
        this.musicModelArrayList = musicModelArrayList;
        this.musicOnItemClickListener = musicOnItemClickListener;
        this.context = context;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView duration;
        TextView size;
        TextView type;
        TextView label;
        ImageView thumb;
        MusicModel musicModel;

        public MusicViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.musicview_custom_layout_title);
            duration = itemView.findViewById(R.id.musicview_custom_layout_duration);
            size = itemView.findViewById(R.id.musicview_custom_layout_size);
            type = itemView.findViewById(R.id.musicview_custom_layout_type);
            label = itemView.findViewById(R.id.musicview_custom_layout_label);
            thumb = itemView.findViewById(R.id.musicview_custom_layout_thumb);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (musicOnItemClickListener != null) {
                musicOnItemClickListener.musicOnItemClick(musicModel, v);
            }
        }

        public void setData(MusicModel data) {
            this.musicModel = data;

            title.setText(musicModel.getAudioTitle());
            duration.setText(musicModel.getAudioDuration());
            type.setText(musicModel.getAudioType());
            size.setText(musicModel.getAudioSize());
            type.setText(musicModel.getAudioType());
            label.setText(musicModel.getLabelPush());
            Glide.with(context)
                    .load(musicModel.getAudioThumbailUrl())
                    .into(thumb);

        }
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.musicview_custom_layout, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicModel musicModel = musicModelArrayList.get(position);

        holder.setData(musicModel);
    }

    @Override
    public int getItemCount() {
        return musicModelArrayList.size();
    }


}
