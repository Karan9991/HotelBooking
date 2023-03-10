package com.example.singlehotel.response;

import com.example.singlehotel.item.ReviewList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReviewRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("total_rate")
    private String total_rate;

    @SerializedName("rate_avg")
    private String rate_avg;

    @SerializedName("SINGLE_HOTEL_APP")
    private List<ReviewList> reviewLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getTotal_rate() {
        return total_rate;
    }

    public String getRate_avg() {
        return rate_avg;
    }

    public List<ReviewList> getReviewLists() {
        return reviewLists;
    }
}
