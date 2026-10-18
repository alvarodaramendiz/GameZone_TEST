package com.gamezone.model;

/**
 * Represents a storage memory accessory, characterized by its capacity and
 * memory type.
 * @author jahdiel
 */
public class Memory extends Accessory {

    private int capacityInGigabytes;
    private String memoryType;

    /**
     * Creates a new Memory accessory.
     * @param productId the unique ID for the memory
     * @param title the name of the memory
     * @param price the selling price
     * @param stock the amount available in inventory
     * @param capacityInGigabytes the storage capacity, in gigabytes
     * @param memoryType the memory type (like, "SD", "microSD", "Tarjeta interna")
     */
    public Memory(String productId, String title, double price, int stock, int capacityInGigabytes, String memoryType) {
        super(productId, title, price, stock);
        this.capacityInGigabytes = capacityInGigabytes;
        this.memoryType = memoryType;
    }

    /** @return the storage capacity, in gigabytes */
    public int getCapacityInGigabytes() {
        return capacityInGigabytes;
    }

    /** @param capacityInGigabytes the capacity to set, in gigabytes */
    public void setCapacityInGigabytes(int capacityInGigabytes) {
        this.capacityInGigabytes = capacityInGigabytes;
    }

    /** @return the memory type */
    public String getMemoryType() {
        return memoryType;
    }

    /** @param memoryType the memory type to set */
    public void setMemoryType(String memoryType) {
        this.memoryType = memoryType;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " | Capacidad: " + capacityInGigabytes + "GB | Tipo: " + memoryType;
    }
     /**
     * Converts this memory into a text line for file persistence.
     * @return a text representation of this memory
     */
    @Override
    public String toText() {
        return super.toText() + "|" + capacityInGigabytes + "|" + memoryType;
    }
}