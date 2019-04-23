package com.anukul.wallethub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anukul.wallethub.R;
import com.anukul.wallethub.listener.ViewReportOnItemClickListener;
import com.anukul.wallethub.model.ViewReportModel;

import java.util.ArrayList;

public class ViewReportAdapter extends RecyclerView.Adapter<ViewReportAdapter.ViewReportViewHolder>{

    private ArrayList<ViewReportModel> viewReportModelArrayList;
    private ViewReportOnItemClickListener viewReportOnItemClickListener;

    public ViewReportAdapter(ArrayList<ViewReportModel> viewReportModelArrayList, ViewReportOnItemClickListener viewReportOnItemClickListener) {
        this.viewReportModelArrayList = viewReportModelArrayList;
        this.viewReportOnItemClickListener = viewReportOnItemClickListener;
    }

    public class ViewReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mediaCountNo;
        TextView mediaName;
        TextView labelName;
        ViewReportModel viewReportModel;

        public ViewReportViewHolder(View itemView) {
            super(itemView);

            mediaCountNo = itemView.findViewById(R.id.view_reportCustom_mediaCountNoTv);
            mediaName = itemView.findViewById(R.id.view_reportCustom_mediaNameTv);
            labelName = itemView.findViewById(R.id.view_reportCustom_labelNameTv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(viewReportOnItemClickListener != null){
                viewReportOnItemClickListener.viewReportOnItemClick(viewReportModel);
            }
        }

        public void setData(ViewReportModel data) {
            this.viewReportModel = data;

            mediaCountNo.setText(viewReportModel.getMediaCount());
            mediaName.setText(viewReportModel.getMediaName());
            labelName.setText(viewReportModel.getLabelName());

        }
    }

    @NonNull
    @Override
    public ViewReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_report_custom_layout,parent,false);
        return new ViewReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewReportViewHolder holder, int position) {

        ViewReportModel viewReportModel = viewReportModelArrayList.get(position);

        holder.setData(viewReportModel);
    }

    @Override
    public int getItemCount() {
        return viewReportModelArrayList.size();
    }


}
