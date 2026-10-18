package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that applies a percentage discount to the whole sale when it
 * includes at least a minimum number of products.
 *
 * @author jahdiel
 */
public class BulkPurchaseDiscount extends Promotion {

    private int minimumQuantity;
    private double percentage;

    /**
     * Creates a new BulkPurchaseDiscount.
     *
     * @param promotionId the unique ID of the promotion
     * @param name the name of the promotion
     * @param startDate the first day the promotion is valid
     * @param endDate the last day the promotion is valid
     * @param minimumQuantity the minimum number of products needed
     * @param percentage the discount percentage (between 0 and 100)
     */
    public BulkPurchaseDiscount(String promotionId, String name, LocalDate startDate, LocalDate endDate,
            int minimumQuantity, double percentage) {
        super(promotionId, name, startDate, endDate);
        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
    }

    /** @return the minimum number of products needed */
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    /** @return the discount percentage */
    public double getPercentage() {
        return percentage;
    }

    /** @param minimumQuantity the minimum quantity to set */
    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    /** @param percentage the discount percentage to set */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * Applies the percentage to the sale subtotal if the sale has at least
     * the minimum number of products; otherwise returns zero.
     *
     * @param sale the sale to evaluate
     * @return the discount amount
     */
    @Override
    public double calculateDiscount(Sale sale) {
        int totalProducts = 0;
        for (Sale.AmountOfProduct item : sale.getProductTrack()) {
            totalProducts += item.getProductAmount();
        }
        if (totalProducts >= minimumQuantity) {
            return getSaleSubtotal(sale) * percentage / 100;
        }
        return 0;
    }
}
