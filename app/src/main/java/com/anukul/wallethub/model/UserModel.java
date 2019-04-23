package com.anukul.wallethub.model;

public class UserModel {
    private String userName;
    private String userPhoneNo;
    private String userEmail;
    private String userPassword;
    private String profileUrl;

    public UserModel(String userName, String userPhoneNo, String userEmail, String userPassword, String profileUrl) {
        this.userName = userName;
        this.userPhoneNo = userPhoneNo;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.profileUrl = profileUrl;
    }

    public UserModel() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
