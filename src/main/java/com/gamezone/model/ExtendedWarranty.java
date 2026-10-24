package com.gamezone.model;

import java.time.LocalDate;

/**
 * Extended warranty: covers factory defects and accidental damage for
 * 12 months from the sale date and costs 10% of the product price.
 *
 * @author jahdiel
 */
public class ExtendedWarranty extends Warranty {

    /**
     * Creates a new ExtendedWarranty.
     *
     * @param warrantyId the unique ID of the warranty
     * @param product the product covered by the warranty
     * @param sale the sale in which the product was bought
     * @param startDate the date the warranty starts (the sale date)
     */
    public ExtendedWarranty(String warrantyId, Product product, Sale sale, LocalDate startDate) {
        super(warrantyId, product, sale, startDate);
    }

    /** @return 12, the duration of the extended warranty in months */
    @Override
    public int getDurationInMonths() {
        return 12;
    }

    /** @return the name of the warranty type */
    @Override
    public String getWarrantyType() {
        return "Garantía Extendida";
    }

    /** @return 10% of the product price */
    @Override
    public double getAdditionalCost() {
        return getProduct().getPrice() * 0.10;
    }
}
