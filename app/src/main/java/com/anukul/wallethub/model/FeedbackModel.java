package com.anukul.wallethub.model;

public class FeedbackModel {
    private String title;
    private String message;
    private String rating;

    public FeedbackModel(String title, String message, String rating) {
        this.title = title;
        this.message = message;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
