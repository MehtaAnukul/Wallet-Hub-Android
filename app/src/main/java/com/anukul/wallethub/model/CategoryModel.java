package com.anukul.wallethub.model;

public class CategoryModel {
    private String categoryImgName;
    private int categoryImgRes;

    public CategoryModel(String categoryImgName, int categoryImgRes) {
        this.categoryImgName = categoryImgName;
        this.categoryImgRes = categoryImgRes;
    }

    public CategoryModel() {

    }

    public String getCategoryImgName() {
        return categoryImgName;
    }

    public void setCategoryImgName(String categoryImgName) {
        this.categoryImgName = categoryImgName;
    }

    public int getCategoryImgRes() {
        return categoryImgRes;
    }

    public void setCategoryImgRes(int categoryImgRes) {
        this.categoryImgRes = categoryImgRes;
    }
}
