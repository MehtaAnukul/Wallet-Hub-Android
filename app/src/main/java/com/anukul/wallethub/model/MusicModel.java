package com.anukul.wallethub.model;

import android.net.Uri;

public class MusicModel {

    private String pushkey;
    private Uri audio;
    private String audioUrl;
    private String audioTitle;
    private String audioDuration;
    private byte[] audioThumbailUri;
    private String audioThumbailUrl;
    private String audioSize;
    private String audioType;
    private String labelPush;

    public String getAudioUrl() {
        return audioUrl;
    }


    public String getPushkey() {
        return pushkey;
    }

    public void setPushkey(String pushkey) {
        this.pushkey = pushkey;
    }

    public MusicModel(String pushkey, String audioUrl, String audioTitle, String audioDuration, String audioThumbailUrl, String audioSize, String audioType, String labelPush) {
        this.pushkey = pushkey;
        this.audioUrl = audioUrl;
        this.audioTitle = audioTitle;
        this.audioDuration = audioDuration;
        this.audioThumbailUrl = audioThumbailUrl;
        this.audioSize = audioSize;
        this.audioType = audioType;
        this.labelPush = labelPush;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public Uri getAudio() {
        return audio;
    }

    public void setAudio(Uri audio) {
        this.audio = audio;
    }

    public MusicModel(Uri audio, String audioTitle, String audioDuration, byte[] audioThumbailUri, String audioSize, String audioType) {

        this.audio = audio;
        this.audioTitle = audioTitle;
        this.audioDuration = audioDuration;
        this.audioThumbailUri = audioThumbailUri;
        this.audioSize = audioSize;
        this.audioType = audioType;
    }

    public MusicModel(String audioTitle, String audioDuration, byte[] audioThumbailUri, String audioThumbailUrl, String audioSize, String audioType, String labelPush) {
        this.audioTitle = audioTitle;
        this.audioDuration = audioDuration;
        this.audioThumbailUri = audioThumbailUri;
        this.audioThumbailUrl = audioThumbailUrl;
        this.audioSize = audioSize;
        this.audioType = audioType;
        this.labelPush = labelPush;
    }


    public MusicModel(String audioUrl, String audioTitle, String audioDuration, String audioThumbailUrl, String audioSize, String audioType, String labelPush) {
        this.audioUrl = audioUrl;
        this.audioTitle = audioTitle;
        this.audioDuration = audioDuration;
        this.audioThumbailUrl = audioThumbailUrl;
        this.audioSize = audioSize;
        this.audioType = audioType;
        this.labelPush = labelPush;
    }

    public MusicModel() {
    }

    public String getAudioTitle() {
        return audioTitle;
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }

    public String getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(String audioDuration) {
        this.audioDuration = audioDuration;
    }

    public byte[] getAudioThumbailUri() {
        return audioThumbailUri;
    }

    public void setAudioThumbailUri(byte[] audioThumbailUri) {
        this.audioThumbailUri = audioThumbailUri;
    }

    public String getAudioThumbailUrl() {
        return audioThumbailUrl;
    }

    public void setAudioThumbailUrl(String audioThumbailUrl) {
        this.audioThumbailUrl = audioThumbailUrl;
    }

    public String getAudioSize() {
        return audioSize;
    }

    public void setAudioSize(String audioSize) {
        this.audioSize = audioSize;
    }

    public String getAudioType() {
        return audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public String getLabelPush() {
        return labelPush;
    }

    public void setLabelPush(String labelPush) {
        this.labelPush = labelPush;
    }
}
