package com.example.smartrecycle.model;

import java.util.List;

public class RecyclingPoint {
    public long id;
    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public String type; // "PLASTIC", "GLASS", "PAPER", etc.
    public List<String> acceptedMaterials;
    public String hours; // optionnel
    public String contact; // optionnel
} 