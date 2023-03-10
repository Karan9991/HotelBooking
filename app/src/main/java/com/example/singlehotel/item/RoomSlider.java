package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomSlider implements Serializable {

    @SerializedName("image_name")
    private String image_name;

    public String getImage_name() {
        return image_name;
    }
}
