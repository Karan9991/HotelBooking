package com.example.singlehotel.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class BookingRoomRP implements Serializable {

     public class SingleHotelApp {
        @SerializedName("message")
        private String message;

        @SerializedName("success")
        private String success;

        @SerializedName("msg")
        private String msg;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

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
    }

    @SerializedName("SINGLE_HOTEL_APP")
    public SingleHotelApp  singleHotelApp;

    public SingleHotelApp getSingleHotelApp() {
        return singleHotelApp;
    }

    public void setSingleHotelApp(SingleHotelApp singleHotelApp) {
        this.singleHotelApp = singleHotelApp;
    }

}
