package com.anukul.wallethub.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anukul.wallethub.R;
import com.anukul.wallethub.listener.CategoryOnItemClickListener;
import com.anukul.wallethub.model.CategoryModel;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{

    private ArrayList<CategoryModel> categoryModelArrayList;
    private CategoryOnItemClickListener categoryOnItemClickListener;

    public CategoryAdapter(ArrayList<CategoryModel> categoryModelArrayList, CategoryOnItemClickListener categoryOnItemClickListener) {
        this.categoryModelArrayList = categoryModelArrayList;
        this.categoryOnItemClickListener = categoryOnItemClickListener;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView categoryImgName;
        ImageView categoryImgRes;
        CategoryModel categoryModel;
        public CategoryViewHolder(View itemView) {
            super(itemView);

            categoryImgName = itemView.findViewById(R.id.category_custom_layout_imageName);
            categoryImgRes = itemView.findViewById(R.id.category_custom_layout_imageView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if(categoryOnItemClickListener != null){
                categoryOnItemClickListener.categoryOnItemClick(categoryModel);
            }
        }

        public void setData(CategoryModel data) {
            this.categoryModel = data;

            categoryImgName.setText(categoryModel.getCategoryImgName());
            categoryImgRes.setImageResource(categoryModel.getCategoryImgRes());

        }
    }
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_custom_layout,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
    CategoryModel categoryModel = categoryModelArrayList.get(position);

    holder.setData(categoryModel);

    }

    @Override
    public int getItemCount() {
        return categoryModelArrayList.size();
    }


}
