package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReviewList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("room_id")
    private String room_id;

    @SerializedName("user_name")
    private String user_name;

    @SerializedName("user_image")
    private String user_image;

    @SerializedName("rate")
    private String rate;

    @SerializedName("dt_rate")
    private String dt_rate;

    @SerializedName("message")
    private String message;

    public String getId() {
        return id;
    }

    public String getRoom_id() {
        return room_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_image() {
        return user_image;
    }

    public String getRate() {
        return rate;
    }

    public String getDt_rate() {
        return dt_rate;
    }

    public String getMessage() {
        return message;
    }
}
