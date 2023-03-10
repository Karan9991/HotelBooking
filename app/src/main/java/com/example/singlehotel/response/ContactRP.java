package com.example.singlehotel.response;

import com.example.singlehotel.item.ContactList;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ContactRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("hotel_name")
    private String hotel_name;

    @SerializedName("hotel_address")
    private String hotel_address;

    @SerializedName("hotel_email")
    private String hotel_email;

    @SerializedName("hotel_phone")
    private String hotel_phone;

    @SerializedName("SINGLE_HOTEL_APP")
    private List<ContactList> contactLists;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getHotel_name() {
        return hotel_name;
    }

    public String getHotel_address() {
        return hotel_address;
    }

    public String getHotel_email() {
        return hotel_email;
    }

    public String getHotel_phone() {
        return hotel_phone;
    }

    public List<ContactList> getContactLists() {
        return contactLists;
    }
}
