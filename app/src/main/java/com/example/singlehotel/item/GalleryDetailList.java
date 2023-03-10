package com.example.singlehotel.item;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class GalleryDetailList implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("cat_id")
    private String cat_id;

    @SerializedName("wallpaper_image")
    private String wallpaper_image;

    @SerializedName("wallpaper_image_thumb")
    private String wallpaper_image_thumb;

    public String getId() {
        return id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public String getWallpaper_image() {
        return wallpaper_image;
    }

    public String getWallpaper_image_thumb() {
        return wallpaper_image_thumb;
    }
}
