package com.anukul.wallethub.listener;

import android.view.View;

import com.anukul.wallethub.model.LabelModel;
import com.anukul.wallethub.model.NoteModel;


public interface ItemClickListener {
    public void onItemClick(NoteModel notesModel, View view, int position);
    public void  onItemClickLabel(LabelModel labelModel,View view,int position);
}