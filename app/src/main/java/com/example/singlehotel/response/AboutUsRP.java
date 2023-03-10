package com.example.singlehotel.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AboutUsRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("app_name")
    private String app_name;

    @SerializedName("app_logo")
    private String app_logo;

    @SerializedName("app_version")
    private String app_version;

    @SerializedName("app_author")
    private String app_author;

    @SerializedName("app_contact")
    private String app_contact;

    @SerializedName("app_email")
    private String app_email;

    @SerializedName("app_website")
    private String app_website;

    @SerializedName("app_description")
    private String app_description;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getApp_name() {
        return app_name;
    }

    public String getApp_logo() {
        return app_logo;
    }

    public String getApp_version() {
        return app_version;
    }

    public String getApp_author() {
        return app_author;
    }

    public String getApp_contact() {
        return app_contact;
    }

    public String getApp_email() {
        return app_email;
    }

    public String getApp_website() {
        return app_website;
    }

    public String getApp_description() {
        return app_description;
    }
}
