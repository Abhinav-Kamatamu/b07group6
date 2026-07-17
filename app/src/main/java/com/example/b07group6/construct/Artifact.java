package com.example.b07group6.construct;

public class Artifact {
    private String lotNumber;       // set manually after reading, not stored inside the value
    private String artifactName;
    private String description;
    private String category;
    private String material;
    private String dynastyPeriod;
    private String culturalOrigin;
    private String dimensions;
    private String conditionReport;
    private String currentLocation;
    private String acquisitionMethod;
    private String provenance;
    private String accessionNumber;
    private String notes;
    private String imageUrl;

    public Artifact() {
        // required no-arg constructor for Firebase deserialization
    }

    public String getLotNumber() { return lotNumber; }
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }

    public String getArtifactName() { return artifactName; }
    public void setArtifactName(String artifactName) { this.artifactName = artifactName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getDynastyPeriod() { return dynastyPeriod; }
    public void setDynastyPeriod(String dynastyPeriod) { this.dynastyPeriod = dynastyPeriod; }

    public String getCulturalOrigin() { return culturalOrigin; }
    public void setCulturalOrigin(String culturalOrigin) { this.culturalOrigin = culturalOrigin; }

    public String getDimensions() { return dimensions; }
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    public String getConditionReport() { return conditionReport; }
    public void setConditionReport(String conditionReport) { this.conditionReport = conditionReport; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getAcquisitionMethod() { return acquisitionMethod; }
    public void setAcquisitionMethod(String acquisitionMethod) { this.acquisitionMethod = acquisitionMethod; }

    public String getProvenance() { return provenance; }
    public void setProvenance(String provenance) { this.provenance = provenance; }

    public String getAccessionNumber() { return accessionNumber; }
    public void setAccessionNumber(String accessionNumber) { this.accessionNumber = accessionNumber; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}