package com.gamezone.model;

import java.time.LocalDate;

/**
 * Base class for all promotions in the GameZone Unicesar store.
 * Each promotion has a validity period and its own way of calculating
 * the discount it gives to a sale.
 *
 * @author jahdiel
 */
public abstract class Promotion {

    private String promotionId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new Promotion.
     *
     * @param promotionId the unique ID of the promotion
     * @param name the name of the promotion
     * @param startDate the first day the promotion is valid
     * @param endDate the last day the promotion is valid
     */
    public Promotion(String promotionId, String name, LocalDate startDate, LocalDate endDate) {
        this.promotionId = promotionId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /** @return the unique ID of the promotion */
    public String getPromotionId() {
        return promotionId;
    }

    /** @return the name of the promotion */
    public String getName() {
        return name;
    }

    /** @return the first day the promotion is valid */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** @return the last day the promotion is valid */
    public LocalDate getEndDate() {
        return endDate;
    }

    /** @param promotionId the promotion ID to set */
    public void setPromotionId(String promotionId) {
        this.promotionId = promotionId;
    }

    /** @param name the promotion name to set */
    public void setName(String name) {
        this.name = name;
    }

    /** @param startDate the start date to set */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /** @param endDate the end date to set */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Checks whether the promotion is valid on the given date, including
     * the start and end days.
     *
     * @param date the date to check
     * @return true if the date is within the validity period
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calculates the discount, in pesos, that this promotion would give
     * to the given sale.
     *
     * @param sale the sale to evaluate
     * @return the discount amount
     */
    public abstract double calculateDiscount(Sale sale);

    /**
     * Adds up the price of every product in the sale multiplied by its
     * quantity. Used by the subclasses to get the sale subtotal.
     *
     * @param sale the sale to evaluate
     * @return the sale subtotal
     */
    protected double getSaleSubtotal(Sale sale) {
        double subtotal = 0;
        for (Sale.AmountOfProduct item : sale.getProductTrack()) {
            subtotal += item.getSoldProduct().getPrice() * item.getProductAmount();
        }
        return subtotal;
    }
}
