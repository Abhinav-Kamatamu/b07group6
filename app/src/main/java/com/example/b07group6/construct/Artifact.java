package com.example.b07group6.construct;

/** The representation of an Artifact stored in Firebase */
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

    /**
     * Constructs a new, empty {@code Artifact} instance.
     * Required as a no-argument constructor for Firebase deserialization.
     */
    public Artifact() { }

    /**
     * Returns the lot number of the artifact.
     * @return the lot number
     */
    public String getLotNumber() { return lotNumber; }

    /**
     * Sets the lot number of the artifact.
     * @param lotNumber the lot number to set
     */
    public void setLotNumber(String lotNumber) { this.lotNumber = lotNumber; }

    /**
     * Returns the name or title of the artifact.
     * @return the artifact name
     */
    public String getArtifactName() { return artifactName; }

    /**
     * Sets the name or title of the artifact.
     * @param artifactName the artifact name to set
     */
    public void setArtifactName(String artifactName) { this.artifactName = artifactName; }

    /**
     * Returns a detailed textual description of the artifact.
     * @return the description
     */
    public String getDescription() { return description; }

    /**
     * Sets a detailed textual description of the artifact.
     *
     * @param description the description to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns the category or classification of the artifact.
     * @return the category
     */
    public String getCategory() { return category; }

    /**
     * Sets the category or classification of the artifact.
     * @param category the category to set
     */
    public void setCategory(String category) { this.category = category; }

    /**
     * Returns the physical material(s) composing the artifact.
     * @return the material
     */
    public String getMaterial() { return material; }

    /**
     * Sets the physical material(s) composing the artifact.
     * @param material the material to set
     */
    public void setMaterial(String material) { this.material = material; }

    /**
     * Returns the historical dynasty or time period associated with the artifact.
     *
     * @return the dynasty or period
     */
    public String getDynastyPeriod() { return dynastyPeriod; }

    /**
     * Sets the historical dynasty or time period associated with the artifact.
     * @param dynastyPeriod the dynasty or period to set
     */
    public void setDynastyPeriod(String dynastyPeriod) { this.dynastyPeriod = dynastyPeriod; }

    /**
     * Returns the cultural origin or geographic region associated with the artifact.
     * @return the cultural origin
     */
    public String getCulturalOrigin() { return culturalOrigin; }

    /**
     * Sets the cultural origin or geographic region associated with the artifact.
     * @param culturalOrigin the cultural origin to set
     */
    public void setCulturalOrigin(String culturalOrigin) { this.culturalOrigin = culturalOrigin; }

    /**
     * Returns the physical dimensions of the artifact.
     * @return the dimensions
     */
    public String getDimensions() { return dimensions; }

    /**
     * Sets the physical dimensions of the artifact.
     * @param dimensions the dimensions to set
     */
    public void setDimensions(String dimensions) { this.dimensions = dimensions; }

    /**
     * Returns the condition report detailing the current physical state of the artifact.
     * @return the condition report
     */
    public String getConditionReport() { return conditionReport; }

    /**
     * Sets the condition report detailing the current physical state of the artifact.
     * @param conditionReport the condition report to set
     */
    public void setConditionReport(String conditionReport) { this.conditionReport = conditionReport; }

    /**
     * Returns the current physical location or housing of the artifact.
     * @return the current location
     */
    public String getCurrentLocation() { return currentLocation; }

    /**
     * Sets the current physical location or housing of the artifact.
     * @param currentLocation the current location to set
     */
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    /**
     * Returns the method by which the artifact was acquired.
     * @return the acquisition method
     */
    public String getAcquisitionMethod() { return acquisitionMethod; }

    /**
     * Sets the method by which the artifact was acquired.
     * @param acquisitionMethod the acquisition method to set
     */
    public void setAcquisitionMethod(String acquisitionMethod) { this.acquisitionMethod = acquisitionMethod; }

    /**
     * Returns the chronological history of the ownership or location of the artifact (provenance).
     * @return the provenance history
     */
    public String getProvenance() { return provenance; }

    /**
     * Sets the chronological history of the ownership or location of the artifact (provenance).
     * @param provenance the provenance to set
     */
    public void setProvenance(String provenance) { this.provenance = provenance; }

    /**
     * Returns the official museum or library accession number.
     * @return the accession number
     */
    public String getAccessionNumber() { return accessionNumber; }

    /**
     * Sets the official museum or library accession number.
     * @param accessionNumber the accession number to set
     */
    public void setAccessionNumber(String accessionNumber) { this.accessionNumber = accessionNumber; }

    /**
     * Returns any supplementary notes related to the artifact.
     * @return the notes
     */
    public String getNotes() { return notes; }

    /**
     * Sets any supplementary notes related to the artifact.
     * @param notes the notes to set
     */
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Returns the network URL pointing to an image of the artifact.
     * @return the image URL
     */
    public String getImageUrl() { return imageUrl; }

    /**
     * Sets the network URL pointing to an image of the artifact.
     * @param imageUrl the image URL to set
     */
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}