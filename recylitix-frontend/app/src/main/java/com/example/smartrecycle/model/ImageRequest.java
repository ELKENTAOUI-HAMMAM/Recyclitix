package com.example.smartrecycle.model;

import com.google.gson.annotations.SerializedName;

public class ImageRequest {
    @SerializedName("imageData")
    private String imageData;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Constructeur par défaut pour Retrofit
    public ImageRequest() {
    }

    public ImageRequest(String imageData) {
        this.imageData = imageData;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
