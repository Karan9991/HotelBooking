package com.example.singlehotel.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentCheckOutRP implements Serializable {

    @SerializedName("success")
    private String success;

    @SerializedName("msg")
    private String msg;

    @SerializedName("paypal_payment_id")
    private String paypal_payment_id;

    @SerializedName("order_id")
    private String razorpay_order_id;

    @SerializedName("id")
    private String stripe_id;

    @SerializedName("stripe_payment_token")
    private String stripe_payment_token;

    @SerializedName("ephemeralKey")
    private String stripe_ephemeralKey;

    @SerializedName("customer")
    private String stripe_customer;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public String getPaypal_payment_id() {
        return paypal_payment_id;
    }

    public void setPaypal_payment_id(String paypal_payment_id) {
        this.paypal_payment_id = paypal_payment_id;
    }

    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public void setRazorpay_order_id(String razorpay_order_id) {
        this.razorpay_order_id = razorpay_order_id;
    }

    public String getStripe_id() {
        return stripe_id;
    }

    public void setStripe_id(String stripe_id) {
        this.stripe_id = stripe_id;
    }

    public String getStripe_payment_token() {
        return stripe_payment_token;
    }

    public void setStripe_payment_token(String stripe_payment_token) {
        this.stripe_payment_token = stripe_payment_token;
    }

    public String getStripe_ephemeralKey() {
        return stripe_ephemeralKey;
    }

    public void setStripe_ephemeralKey(String stripe_ephemeralKey) {
        this.stripe_ephemeralKey = stripe_ephemeralKey;
    }

    public String getStripe_customer() {
        return stripe_customer;
    }

    public void setStripe_customer(String stripe_customer) {
        this.stripe_customer = stripe_customer;
    }
}
