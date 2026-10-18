package com.gamezone.model;

/**
 * Represents a cable accessory, characterized by its length and connector
 * type
 * @author jahdiel
 */
public class Cable extends Accessory {

    private double lengthInMeters;
    private String connectorType;
    /**
     * Creates a new Cable.
     * 
     * @param productId the unique ID for the cable
     * @param title the name of the cable
     * @param price the selling price
     * @param stock the amount available in inventory
     * @param lengthInMeters the length of the cable, in meters
     * @param connectorType the connector type (like, "HDMI", "USB", "Óptico")
     */
    public Cable(String productId, String title, double price, int stock, double lengthInMeters, String connectorType) {
        super(productId, title, price, stock);
        this.lengthInMeters = lengthInMeters;
        this.connectorType = connectorType;
    }

    /** @return the length of the cable, in meters */
    public double getLengthInMeters() {
        return lengthInMeters;
    }

    /** @param lengthInMeters the length to set, in meters */
    public void setLengthInMeters(double lengthInMeters) {
        this.lengthInMeters = lengthInMeters;
    }

    /** @return the connector type */
    public String getConnectorType() {
        return connectorType;
    }

    /** @param connectorType the connector type to set */
    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " | Longitud: " + lengthInMeters + "m | Conector: " + connectorType;
    }
    /**
     * Converts this cable into a text line for file persistence.
     * @return a text representation of this cable
     */
    @Override
    public String toText() {
        return super.toText() + "|" + lengthInMeters + "|" + connectorType;
    }
}