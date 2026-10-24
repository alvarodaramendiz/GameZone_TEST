package com.gamezone.model;

import java.time.LocalDate;

/**
 * Basic warranty: covers only factory defects for 6 months from the sale
 * date and has no additional cost.
 *
 * @author jahdiel
 */
public class BasicWarranty extends Warranty {

    /**
     * Creates a new BasicWarranty.
     *
     * @param warrantyId the unique ID of the warranty
     * @param product the product covered by the warranty
     * @param sale the sale in which the product was bought
     * @param startDate the date the warranty starts (the sale date)
     */
    public BasicWarranty(String warrantyId, Product product, Sale sale, LocalDate startDate) {
        super(warrantyId, product, sale, startDate);
    }

    /** @return 6, the duration of the basic warranty in months */
    @Override
    public int getDurationInMonths() {
        return 6;
    }

    /** @return the name of the warranty type */
    @Override
    public String getWarrantyType() {
        return "Garantía Básica";
    }

    /** @return 0.0, the basic warranty has no additional cost */
    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}
