package com.example.smartrecycle.model;

import com.google.gson.annotations.SerializedName;

public class WasteResult {
    @SerializedName("id")
    private Long id;
    
    @SerializedName("wasteIcon")
    private String wasteIcon;
    
    @SerializedName("wasteType")
    private String wasteType;
    
    @SerializedName("wasteCategory")
    private String wasteCategory;
    
    @SerializedName("wasteDate")
    private String wasteDate;
    
    @SerializedName("wastePoints")
    private Integer wastePoints;
    
    @SerializedName("timeAgo")
    private String timeAgo;
    
    @SerializedName("objectDescription")
    private String objectDescription;
    
    @SerializedName("instructions")
    private String instructions;
    
    @SerializedName("confidence")
    private Double confidence;
    
    @SerializedName("environmentalImpact")
    private String environmentalImpact;

    @SerializedName("imageUrl")
    private String imageUrl;

    // Getters
    public Long getId() {
        return id;
    }

    public String getWasteIcon() {
        return wasteIcon;
    }

    public String getWasteType() {
        return wasteType;
    }

    public String getWasteCategory() {
        return wasteCategory;
    }

    public String getWasteDate() {
        return wasteDate;
    }

    public Integer getWastePoints() {
        return wastePoints;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public String getObjectDescription() {
        return objectDescription;
    }

    public String getInstructions() {
        return instructions;
    }

    public Double getConfidence() {
        return confidence;
    }

    public String getEnvironmentalImpact() {
        return environmentalImpact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setWasteIcon(String wasteIcon) {
        this.wasteIcon = wasteIcon;
    }

    public void setWasteType(String wasteType) {
        this.wasteType = wasteType;
    }

    public void setWasteCategory(String wasteCategory) {
        this.wasteCategory = wasteCategory;
    }

    public void setWasteDate(String wasteDate) {
        this.wasteDate = wasteDate;
    }

    public void setWastePoints(Integer wastePoints) {
        this.wastePoints = wastePoints;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public void setObjectDescription(String objectDescription) {
        this.objectDescription = objectDescription;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public void setEnvironmentalImpact(String environmentalImpact) {
        this.environmentalImpact = environmentalImpact;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}