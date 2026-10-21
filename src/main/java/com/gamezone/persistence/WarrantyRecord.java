package com.gamezone.persistence;

import java.time.LocalDate;

/**
 * Raw data of a stored warranty, as read from the warranties file. It keeps
 * only identifiers for the sale and the product, so the persistence layer
 * does not need to resolve them; the service turns it into a Warranty.
 *
 * @author jahdiel
 */
public class WarrantyRecord {

    private final String type;
    private final String warrantyId;
    private final String saleId;
    private final String productId;
    private final LocalDate startDate;

    /**
     * Creates a new warranty record.
     *
     * @param type       the warranty type: "BASIC" or "EXTENDED"
     * @param warrantyId the identifier of the warranty
     * @param saleId     the identifier of the sale
     * @param productId  the identifier of the product
     * @param startDate  the first day of coverage
     */
    public WarrantyRecord(String type, String warrantyId, String saleId, String productId, LocalDate startDate) {
        this.type = type;
        this.warrantyId = warrantyId;
        this.saleId = saleId;
        this.productId = productId;
        this.startDate = startDate;
    }

    /** @return the warranty type: "BASIC" or "EXTENDED" */
    public String getType() {
        return type;
    }

    /** @return the identifier of the warranty */
    public String getWarrantyId() {
        return warrantyId;
    }

    /** @return the identifier of the sale */
    public String getSaleId() {
        return saleId;
    }

    /** @return the identifier of the product */
    public String getProductId() {
        return productId;
    }

    /** @return the first day of coverage */
    public LocalDate getStartDate() {
        return startDate;
    }
}
