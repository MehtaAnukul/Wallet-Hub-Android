package com.anukul.wallethub.model;

public class LabelModel {
    private String labelName;
    private String pushKey;

    public LabelModel(String labelName, String pushKey) {
        this.labelName = labelName;
        this.pushKey = pushKey;
    }

    public LabelModel(String labelName) {
        this.labelName = labelName;
    }

    public LabelModel() {
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
