package com.anukul.wallethub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anukul.wallethub.R;
import com.anukul.wallethub.listener.ItemClickListener;
import com.anukul.wallethub.model.LabelModel;

import java.util.ArrayList;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {


    private ArrayList<LabelModel> labelModelArrayList;
    private ItemClickListener itemClickListener;

    public LabelAdapter(ArrayList<LabelModel> labelModelArrayList, ItemClickListener itemClickListener) {
        this.labelModelArrayList = labelModelArrayList;
        this.itemClickListener = itemClickListener;
    }

    public class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView labelNameTv;
        ImageView labelCustomMenu;

        LabelModel labelModel;
        int position;

        public LabelViewHolder(View itemView) {
            super(itemView);

            labelNameTv = itemView.findViewById(R.id.label_customLayout_labelNameTv);
            labelCustomMenu = itemView.findViewById(R.id.label_customLayout_optionMenu);

            itemView.setOnClickListener(this);
            labelCustomMenu.setOnClickListener(this);
        }
        public void setData(LabelModel labelModel, int position) {
            this.labelModel = labelModel;
            this.position = position;

            labelNameTv.setText(labelModel.getLabelName());
        }

        @Override
        public void onClick(View v) {
            if(itemClickListener != null){
                itemClickListener.onItemClickLabel(labelModel,v,position);
            }
        }
    }
    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_custom_layout,parent,false);

        return new LabelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, int position) {
        LabelModel labelModel = labelModelArrayList.get(position);

       // holder.labelName.setText(labelModel.getLabelPush());
        holder.setData(labelModel,position);
    }

    @Override
    public int getItemCount() {
        return labelModelArrayList.size();
    }


}
