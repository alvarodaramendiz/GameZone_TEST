package com.gamezone.model;

/**
 Represents a game controller accessory, characterized by its connection
 type (wired or wireless).

 @author jahdiel
 */
public class Controller extends Accessory {

    private String connectionType;

    /**
     * Creates a new Controller.
     * @param productId the unique ID for the controller
     * @param title the name of the controller
     * @param price the selling price
     * @param stock the amount available in inventory
     * @param connectionType the connection type ("Inalámbrico" or "Alámbrico")
     */
    public Controller(String productId, String title, double price, int stock, String connectionType) {
        super(productId, title, price, stock);
        this.connectionType = connectionType;
    }

    /** @return the connection type of the controller */
    public String getConnectionType() {
        return connectionType;
    }

    /** @param connectionType the connection type to set */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " | Tipo de conexión: " + connectionType;
    }
    
     /**
     Converts this controller into a text line for file persistence.
     @return a text representation of this controller
     */
    @Override
    public String toText() {
        return super.toText() + "|" + connectionType;
    }
}