package com.anukul.wallethub.model;

public class ImageModel {
    private String labelName;
    private String imgUrl;
    private String pushkey;


    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public ImageModel(String labelName, String imgUrl, String pushkey) {
        this.labelName = labelName;
        this.imgUrl = imgUrl;
        this.pushkey = pushkey;
    }

    public ImageModel(String labelName, String imgUrl) {
        this.labelName = labelName;
        this.imgUrl = imgUrl;
    }

    public ImageModel() {
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
