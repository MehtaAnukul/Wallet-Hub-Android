package com.anukul.wallethub.model;

public class NoteModel {

    private String pushKey;
    private String title;
    private String category;
    private String details;


    public NoteModel() {
    }

    public NoteModel(String title, String category, String details) {
        this.title = title;
        this.category = category;
        this.details = details;
    }

    public NoteModel(String pushKey, String title, String category, String details) {
        this.pushKey = pushKey;
        this.title = title;
        this.category = category;
        this.details = details;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
