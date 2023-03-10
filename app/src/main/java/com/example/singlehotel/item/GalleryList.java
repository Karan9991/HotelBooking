package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GalleryList implements Serializable {

    @SerializedName("cid")
    private String cid;

    @SerializedName("category_name")
    private String category_name;

    @SerializedName("category_image")
    private String category_image;

    @SerializedName("category_image_thumb")
    private String category_image_thumb;

    @SerializedName("total_wallpaper")
    private String total_wallpaper;

    public String getCid() {
        return cid;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image() {
        return category_image;
    }

    public String getCategory_image_thumb() {
        return category_image_thumb;
    }

    public String getTotal_wallpaper() {
        return total_wallpaper;
    }
}
