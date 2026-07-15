package com.example.b07group6.construct;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Artifact implements Serializable {

    private String lotNumber;
    private String name;
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
    private String lastEditedBy;
    private long lastEditedAt;

    public Artifact(
            String lotNumber, String name, String description, String category,
            String material, String dynastyPeriod, String culturalOrigin,
            String dimensions, String conditionReport, String currentLocation,
            String acquisitionMethod, String provenance, String accessionNumber,
            String notes, String imageUrl, String lastEditedBy, long lastEditedAt
    ) {
        this.lotNumber = validateMandatory(lotNumber, "lotNumber");
        this.name = validateMandatory(name, "name");
        this.description = validateMandatory(description, "description");
        this.category = validateMandatory(category, "category");
        this.material = validateMandatory(material, "material");
        this.dynastyPeriod = validateMandatory(dynastyPeriod, "dynastyPeriod");
        this.culturalOrigin = culturalOrigin;
        this.dimensions = dimensions;
        this.conditionReport = conditionReport;
        this.currentLocation = currentLocation;
        this.acquisitionMethod = acquisitionMethod;
        this.provenance = provenance;
        this.accessionNumber = accessionNumber;
        this.notes = notes;
        this.imageUrl = imageUrl;
        this.lastEditedBy = lastEditedBy;
        this.lastEditedAt = lastEditedAt;
    }

    public Artifact(Map<String, Object> map, String lotNumber) {
        this.lotNumber = lotNumber;
        this.name = getStringFromMap(map, "name", false);
        this.description = getStringFromMap(map, "description", false);
        this.category = getStringFromMap(map, "category", false);
        this.material = getStringFromMap(map, "material", false);
        this.dynastyPeriod = getStringFromMap(map, "dynastyPeriod", false);
        this.culturalOrigin = getStringFromMap(map, "culturalOrigin", true);
        this.dimensions = getStringFromMap(map, "dimensions", true);
        this.conditionReport = getStringFromMap(map, "conditionReport", true);
        this.currentLocation = getStringFromMap(map, "currentLocation", true);
        this.acquisitionMethod = getStringFromMap(map, "acquisitionMethod", true);
        this.provenance = getStringFromMap(map, "provenance", true);
        this.accessionNumber = getStringFromMap(map, "accessionNumber", true);
        this.notes = getStringFromMap(map, "notes", true);
        this.imageUrl = getStringFromMap(map, "imageUrl", true);
        this.lastEditedBy = getStringFromMap(map, "lastEditedBy", true);
        this.lastEditedAt = getLong(map, "lastEditedAt");
    }

    public Artifact(Artifact other) {
        this.lotNumber = other.lotNumber;
        this.name = other.name;
        this.description = other.description;
        this.category = other.category;
        this.material = other.material;
        this.dynastyPeriod = other.dynastyPeriod;
        this.culturalOrigin = other.culturalOrigin;
        this.dimensions = other.dimensions;
        this.conditionReport = other.conditionReport;
        this.currentLocation = other.currentLocation;
        this.acquisitionMethod = other.acquisitionMethod;
        this.provenance = other.provenance;
        this.accessionNumber = other.accessionNumber;
        this.notes = other.notes;
        this.imageUrl = other.imageUrl;
        this.lastEditedBy = other.lastEditedBy;
        this.lastEditedAt = other.lastEditedAt;
    }

    private static String validateMandatory(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required and cannot be blank");
        }
        return value;
    }

    private static String getStringFromMap(Map<String, Object> map, String key, boolean isOptional) {
        Object raw = map.get(key);
        if (raw == null) {
            if (isOptional) return null;
            throw new IllegalArgumentException("Missing required field: " + key);
        }
        if (!(raw instanceof String)) {
            throw new IllegalArgumentException(
                "Field \"" + key + "\" expected a String but was " + raw.getClass().getSimpleName()
            );
        }
        String value = (String) raw;
        if (value.isBlank()) {
            if (isOptional) return null;
            throw new IllegalArgumentException("Field '" + key + "' is required and cannot be blank");
        }
        return value;
    }


    private static long getLong(Map<String, Object> map, String key) {
        Object raw = map.get(key);
        if (raw == null) {
            throw new IllegalArgumentException("Missing required field: " + key);
        }
        if (!(raw instanceof Number)) {
            throw new IllegalArgumentException(
                    "Field '" + key + "' expected a Number but was " + raw.getClass().getSimpleName());
        }
        return ((Number) raw).longValue();
    }

    public String getLotNumber() { return lotNumber; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getMaterial() { return material; }
    public String getDynastyPeriod() { return dynastyPeriod; }
    public String getCulturalOrigin() { return culturalOrigin; }
    public String getDimensions() { return dimensions; }
    public String getConditionReport() { return conditionReport; }
    public String getCurrentLocation() { return currentLocation; }
    public String getAcquisitionMethod() { return acquisitionMethod; }
    public String getProvenance() { return provenance; }
    public String getAccessionNumber() { return accessionNumber; }
    public String getNotes() { return notes; }
    public String getImageUrl() { return imageUrl; }
    public String getLastEditedBy() { return lastEditedBy; }
    public long getLastEditedAt() { return lastEditedAt; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("category", category);
        map.put("material", material);
        map.put("dynastyPeriod", dynastyPeriod);
        map.put("culturalOrigin", culturalOrigin);
        map.put("dimensions", dimensions);
        map.put("conditionReport", conditionReport);
        map.put("currentLocation", currentLocation);
        map.put("acquisitionMethod", acquisitionMethod);
        map.put("provenance", provenance);
        map.put("accessionNumber", accessionNumber);
        map.put("notes", notes);
        map.put("imageUrl", imageUrl);
        map.put("lastEditedBy", lastEditedBy);
        map.put("lastEditedAt", lastEditedAt);
        return map;
    }
}