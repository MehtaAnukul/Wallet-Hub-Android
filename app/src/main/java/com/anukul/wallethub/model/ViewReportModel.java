package com.anukul.wallethub.model;

public class ViewReportModel {
    private String mediaCount;
    private String mediaName;
    private String labelName;

    public ViewReportModel(String mediaCount, String mediaName, String labelName) {
        this.mediaCount = mediaCount;
        this.mediaName = mediaName;
        this.labelName = labelName;
    }

    public ViewReportModel() {
    }

    public String getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(String mediaCount) {
        this.mediaCount = mediaCount;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }
}
