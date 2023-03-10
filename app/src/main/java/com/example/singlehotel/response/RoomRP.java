package com.example.singlehotel.response;

import com.example.singlehotel.item.RoomList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RoomRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("SINGLE_HOTEL_APP")
    private List<RoomList> roomLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<RoomList> getRoomLists() {
        return roomLists;
    }
}
