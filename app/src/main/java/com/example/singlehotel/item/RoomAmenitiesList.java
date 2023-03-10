package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomAmenitiesList implements Serializable {

    @SerializedName("room_amenities")
    private String room_amenities;

    public String getRoom_amenities() {
        return room_amenities;
    }

}
