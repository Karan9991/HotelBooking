package com.example.singlehotel.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TermsConditionsRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("app_terms_conditions")
    private String app_terms_conditions;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getApp_terms_conditions() {
        return app_terms_conditions;
    }
}
