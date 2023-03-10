package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookingList implements Serializable {

    @SerializedName("room_id")
    private String room_id;

    @SerializedName("room_name")
    private String room_name;

    @SerializedName("adults_allowed")
    private String adults_allowed;

    @SerializedName("children_allowed")
    private String children_allowed;

    @SerializedName("gateway")
    private String gateway;

    @SerializedName("payment_amount")
    private String payment_amount;

    @SerializedName("payment_id")
    private String payment_id;

    @SerializedName("date")
    private String date;

     @SerializedName("check_in_date")
    private String check_in_date;

    @SerializedName("check_out_date")
    private String check_out_date;


    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public String getAdults_allowed() {
        return adults_allowed;
    }

    public void setAdults_allowed(String adults_allowed) {
        this.adults_allowed = adults_allowed;
    }

    public String getChildren_allowed() {
        return children_allowed;
    }

    public void setChildren_allowed(String children_allowed) {
        this.children_allowed = children_allowed;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getPayment_amount() {
        return payment_amount;
    }

    public void setPayment_amount(String payment_amount) {
        this.payment_amount = payment_amount;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public void setPayment_id(String payment_id) {
        this.payment_id = payment_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCheck_in_date() {
        return check_in_date;
    }

    public void setCheck_in_date(String check_in_date) {
        this.check_in_date = check_in_date;
    }

    public String getCheck_out_date() {
        return check_out_date;
    }

    public void setCheck_out_date(String check_out_date) {
        this.check_out_date = check_out_date;
    }
}
