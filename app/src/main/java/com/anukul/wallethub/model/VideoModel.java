package com.anukul.wallethub.model;

import android.net.Uri;

public class VideoModel {


    private String pushkey;
    private Uri video;
    private String videoUrl;
    private String videoTitle;
    private String videoDuration;
    private byte[] videoThumbailByte;
    private String videoThumbailUrl;
    private String videoSize;
    private String videoType;
    private String videoLabelPush;


    public VideoModel() {
    }

    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public VideoModel(String pushkey, String videoUrl, String videoTitle, String videoDuration, String videoThumbailUrl, String videoSize, String videoType, String videoLabelPush) {
        this.pushkey = pushkey;
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
        this.videoThumbailUrl = videoThumbailUrl;
        this.videoSize = videoSize;
        this.videoType = videoType;
        this.videoLabelPush = videoLabelPush;
    }

    public VideoModel(Uri video, String videoTitle, String videoDuration, byte[] videoThumbailByte, String videoSize, String videoType) {
        this.video = video;
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
        this.videoThumbailByte = videoThumbailByte;
        this.videoSize = videoSize;
        this.videoType = videoType;
    }

    public VideoModel(String videoUrl, String videoTitle, String videoDuration, String videoThumbailUrl, String videoSize, String videoType, String videoLabelPush) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
        this.videoDuration = videoDuration;
        this.videoThumbailUrl = videoThumbailUrl;
        this.videoSize = videoSize;
        this.videoType = videoType;
        this.videoLabelPush = videoLabelPush;
    }

    public Uri getVideo() {
        return video;
    }

    public void setVideo(Uri video) {
        this.video = video;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public byte[] getVideoThumbailByte() {
        return videoThumbailByte;
    }

    public void setVideoThumbailByte(byte[] videoThumbailByte) {
        this.videoThumbailByte = videoThumbailByte;
    }

    public String getVideoThumbailUrl() {
        return videoThumbailUrl;
    }

    public void setVideoThumbailUrl(String videoThumbailUrl) {
        this.videoThumbailUrl = videoThumbailUrl;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoLabelPush() {
        return videoLabelPush;
    }

    public void setVideoLabelPush(String videoLabelPush) {
        this.videoLabelPush = videoLabelPush;
    }
}
