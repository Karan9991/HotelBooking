package com.example.singlehotel.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LocationRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("hotel_lat")
    private String hotel_lat;

    @SerializedName("hotel_long")
    private String hotel_long;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getHotel_lat() {
        return hotel_lat;
    }

    public String getHotel_long() {
        return hotel_long;
    }
}
