package com.example.singlehotel.response;

import com.example.singlehotel.item.HomeBanner;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class HomeRP implements Serializable {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("home_banner")
    private List<HomeBanner> homeBanners;

    @SerializedName("hotel_name")
    private String hotel_name;

    @SerializedName("hotel_address")
    private String hotel_address;

    @SerializedName("hotel_email")
    private String hotel_email;

    @SerializedName("hotel_phone")
    private String hotel_phone;

    @SerializedName("facebook_url")
    private String facebook_url;

    @SerializedName("instagram_url")
    private String instagram_url;

    @SerializedName("twitter_url")
    private String twitter_url;

    @SerializedName("whatsapp_url")
    private String whatsapp_url;

    @SerializedName("youtube_url")
    private String youtube_url;

    @SerializedName("website_url")
    private String website_url;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<HomeBanner> getHomeBanners() {
        return homeBanners;
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

    public String getFacebook_url() {
        return facebook_url;
    }

    public String getInstagram_url() {
        return instagram_url;
    }

    public String getTwitter_url() {
        return twitter_url;
    }

    public String getWhatsapp_url() {
        return whatsapp_url;
    }

    public String getYoutube_url() {
        return youtube_url;
    }

    public String getWebsite_url() {
        return website_url;
    }
}
