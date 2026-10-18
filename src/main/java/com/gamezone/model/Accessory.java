package com.gamezone.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all video game accessories sold by GameZone
 * Unicesar. Extends Product to reuse common sellable-item behavior and
 * adds console compatibility.
 *
 * @author jahdiel
 */
public abstract class Accessory extends Product {

    private List<String> compatibleConsoleIds;

    /**
     * Creates a new Accessory.
     *
     * @param productId the unique ID for the accessory
     * @param title the name of the accessory
     * @param price the selling price
     * @param stock the amount available in inventory
     */
    public Accessory(String productId, String title, double price, int stock) {
        super(productId, title, price, stock);
        this.compatibleConsoleIds = new ArrayList<>();
    }

    /**
     * Returns the console IDs this accessory is compatible with. The list is
     * read-only; use {@link #addCompatibleConsole(String)} to modify it.
     *
     * @return an unmodifiable view of the compatible console IDs
     */
    public List<String> getCompatibleConsoleIds() {
        return Collections.unmodifiableList(compatibleConsoleIds);
    }

    /**
     * Registers a console as compatible with this accessory.
     *
     * @param consoleId the ID of the compatible console
     */
    public void addCompatibleConsole(String consoleId) {
        if (!compatibleConsoleIds.contains(consoleId)) {
            compatibleConsoleIds.add(consoleId);
        }
    }

    /**
     * Checks whether this accessory is compatible with the given console.
     *
     * @param consoleId the ID of the console to check
     * @return true if the accessory is compatible with the console
     */
    public boolean isCompatibleWith(String consoleId) {
        return compatibleConsoleIds.contains(consoleId);
    }

    @Override
    public String getDescription() {
        return getTitle() + " - $" + getPrice() + " (Stock: " + getStock()
                + ") | Compatible con: " + compatibleConsoleIds;
    }
}