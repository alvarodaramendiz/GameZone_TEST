package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that applies a percentage discount to the whole sale.
 *
 * @author jahdiel
 */
public class PercentageDiscount extends Promotion {

    private double percentage;

    /**
     * Creates a new PercentageDiscount.
     *
     * @param promotionId the unique ID of the promotion
     * @param name the name of the promotion
     * @param startDate the first day the promotion is valid
     * @param endDate the last day the promotion is valid
     * @param percentage the discount percentage (between 0 and 100)
     */
    public PercentageDiscount(String promotionId, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        super(promotionId, name, startDate, endDate);
        this.percentage = percentage;
    }

    /** @return the discount percentage */
    public double getPercentage() {
        return percentage;
    }

    /** @param percentage the discount percentage to set */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * Applies the percentage to the sale subtotal.
     *
     * @param sale the sale to evaluate
     * @return the discount amount
     */
    @Override
    public double calculateDiscount(Sale sale) {
        return getSaleSubtotal(sale) * percentage / 100;
    }
}
