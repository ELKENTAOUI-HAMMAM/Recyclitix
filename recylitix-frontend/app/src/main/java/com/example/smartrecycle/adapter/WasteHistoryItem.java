package com.example.smartrecycle.adapter;

import java.io.Serializable;

public class WasteHistoryItem implements Serializable {
    private String wasteType;
    private String date;
    private int points;
    private String imagePath; // Chemin vers l'image stockée

    public WasteHistoryItem(String wasteType, String date, int points, String imagePath) {
        this.wasteType = wasteType;
        this.date = date;
        this.points = points;
        this.imagePath = imagePath;
    }

    public String getWasteType() {
        return wasteType;
    }

    public String getDate() {
        return date;
    }

    public int getPoints() {
        return points;
    }

    public String getImagePath() {
        return imagePath;
    }
}
