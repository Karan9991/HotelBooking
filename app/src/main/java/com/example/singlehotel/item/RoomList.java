package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("room_name")
    private String room_name;

    @SerializedName("room_image")
    private String room_image;

    @SerializedName("room_image_thumb")
    private String room_image_thumb;

    @SerializedName("room_price")
    private String room_price;

    @SerializedName("total_rate")
    private String total_rate;

    @SerializedName("rate_avg")
    private String rate_avg;

    public String getId() {
        return id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public String getRoom_image() {
        return room_image;
    }

    public String getRoom_image_thumb() {
        return room_image_thumb;
    }

    public String getRoom_price() {
        return room_price;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public String getRate_avg() {
        return rate_avg;
    }
}
