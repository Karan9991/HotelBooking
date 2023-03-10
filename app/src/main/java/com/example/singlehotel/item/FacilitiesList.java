package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FacilitiesList implements Serializable {

    @SerializedName("facilities")
    private String facilities;

    public String getFacilities() {
        return facilities;
    }
}
