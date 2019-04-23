package com.anukul.wallethub.model;

import android.net.Uri;

public class DocumentModel {


    private String pushKey;
    private String fileUrl;
    private Uri fileUri;
    private String fileSize;
    private String fileType;
    private String fileName;
    private String labelPush;

    public DocumentModel() {
    }


    public String getPushKey() {
        return pushKey;
    }


    public DocumentModel(String pushKey, String fileUrl,  String fileSize, String fileType, String fileName, String labelPush) {
        this.pushKey = pushKey;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileName = fileName;
        this.labelPush = labelPush;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getLabelPush() {
        return labelPush;
    }

    public void setLabelPush(String labelPush) {
        this.labelPush = labelPush;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public DocumentModel(Uri fileUri, String fileSize, String fileType, String fileName) {
        this.fileUri = fileUri;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileName = fileName;
    }


    public DocumentModel(String fileUrl, String fileSize, String fileType, String fileName, String labelPush) {
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.fileName = fileName;
        this.labelPush = labelPush;

    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }


    public void setFileType(String fileType) {
        this.fileType = fileType;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
