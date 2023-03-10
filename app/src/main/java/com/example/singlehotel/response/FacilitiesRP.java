package com.example.singlehotel.response;

import com.example.singlehotel.item.FacilitiesList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class FacilitiesRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("hotel_info")
    private String hotel_info;

    @SerializedName("SINGLE_HOTEL_APP")
    private List<FacilitiesList> facilitiesLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getHotel_info() {
        return hotel_info;
    }

    public List<FacilitiesList> getFacilitiesLists() {
        return facilitiesLists;
    }
}
