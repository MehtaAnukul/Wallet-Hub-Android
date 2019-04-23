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
import com.anukul.wallethub.listener.DocumentOnItemClickListener;
import com.anukul.wallethub.listener.MusicOnItemClickListener;
import com.anukul.wallethub.model.DocumentModel;
import com.anukul.wallethub.model.MusicModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.MusicViewHolder> {

    private ArrayList<DocumentModel> documentModelArrayList;
    private DocumentOnItemClickListener documentOnItemClickListener;
    private Context context;

    public DocumentAdapter(ArrayList<DocumentModel> documentModelArrayList, DocumentOnItemClickListener documentOnItemClickListener, Context context) {
        this.documentModelArrayList = documentModelArrayList;
        this.documentOnItemClickListener = documentOnItemClickListener;
        this.context = context;
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView file;
        TextView size;
        TextView label;
        ImageView icon;
        DocumentModel documentModel;

        public MusicViewHolder(View itemView) {
            super(itemView);
            file = itemView.findViewById(R.id.documentview_custom_layout_filename);
            size = itemView.findViewById(R.id.documentview_custom_layout_file_size);
            label = itemView.findViewById(R.id.documentview_custom_layout_label);
            icon = itemView.findViewById(R.id.documentview_custom_layout_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (documentOnItemClickListener != null) {
                documentOnItemClickListener.onDocumentOnItemClick(documentModel, v);
            }
        }

        public void setData(DocumentModel data) {
            this.documentModel = data;

            file.setText(documentModel.getFileName());
            size.setText(documentModel.getFileSize());
            label.setText(documentModel.getLabelPush());

            switch (data.getFileType()) {
                case "pdf":
                    icon.setImageResource(R.drawable.pdf);
                    break;
                case "docx":
                    icon.setImageResource(R.drawable.word);
                    break;
                case "xlsx":
                    icon.setImageResource(R.drawable.excel);
                    break;
                default:
                    icon.setImageResource(R.drawable.document);
                    break;
            }


        }
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.documentview_custom_layout, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        DocumentModel documentModel = documentModelArrayList.get(position);

        holder.setData(documentModel);
    }

    @Override
    public int getItemCount() {
        return documentModelArrayList.size();
    }


}
