package com.example.singlehotel.response;

import com.example.singlehotel.item.BookingList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class BookingHisRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("SINGLE_HOTEL_APP")
    private List<BookingList> bookingLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }


    public List<BookingList> getBookingLists() {
        return bookingLists;
    }

    public void setBookingLists(List<BookingList> bookingLists) {
        this.bookingLists = bookingLists;
    }
}
