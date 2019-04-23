package com.anukul.wallethub.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anukul.wallethub.R;
import com.anukul.wallethub.listener.ImageOnItemClickListener;
import com.anukul.wallethub.model.ImageModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder> {

    private ArrayList<ImageModel> imageModelArrayList;
    private ImageOnItemClickListener imageOnItemClickListener;
    private Context context;

    public ImageViewAdapter(ArrayList<ImageModel> imageModelArrayList, ImageOnItemClickListener imageOnItemClickListener, Context context) {
        this.imageModelArrayList = imageModelArrayList;
        this.imageOnItemClickListener = imageOnItemClickListener;
        this.context = context;
        Log.e("HOM", imageModelArrayList.size() + "");
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView labelName;
        ImageView imgRes;
        ImageModel imageModel;

        public ImageViewHolder(View itemView) {
            super(itemView);
            labelName = itemView.findViewById(R.id.imageview_custom_layout_labelName);
            imgRes = itemView.findViewById(R.id.imageview_custom_layout_imageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (imageOnItemClickListener != null) {
                imageOnItemClickListener.imageOnItemClick(imageModel, v);
            }
        }

        public void setData(ImageModel data) {
            this.imageModel = data;

            labelName.setText(imageModel.getLabelName());
            Glide.with(context)
                    .load(imageModel.getImgUrl())
                    .into(imgRes);

            Log.e("HOM", labelName.getText() + "");

        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_custom_layout, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel imageModel = imageModelArrayList.get(position);

        holder.setData(imageModel);
    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }


}
