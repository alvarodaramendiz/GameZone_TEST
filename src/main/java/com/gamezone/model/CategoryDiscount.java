package com.gamezone.model;

import java.time.LocalDate;

/**
 * Promotion that applies a percentage discount only to the products of
 * one category ("VIDEOGAME" or "CONSOLE").
 *
 * @author jahdiel
 */
public class CategoryDiscount extends Promotion {

    private double percentage;
    private String targetCategory;

    /**
     * Creates a new CategoryDiscount.
     *
     * @param promotionId the unique ID of the promotion
     * @param name the name of the promotion
     * @param startDate the first day the promotion is valid
     * @param endDate the last day the promotion is valid
     * @param percentage the discount percentage (between 0 and 100)
     * @param targetCategory the category that gets the discount ("VIDEOGAME" or "CONSOLE")
     */
    public CategoryDiscount(String promotionId, String name, LocalDate startDate, LocalDate endDate,
            double percentage, String targetCategory) {
        super(promotionId, name, startDate, endDate);
        this.percentage = percentage;
        this.targetCategory = targetCategory;
    }

    /** @return the discount percentage */
    public double getPercentage() {
        return percentage;
    }

    /** @return the category that gets the discount */
    public String getTargetCategory() {
        return targetCategory;
    }

    /** @param percentage the discount percentage to set */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /** @param targetCategory the target category to set ("VIDEOGAME" or "CONSOLE") */
    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }

    /**
     * Adds up only the products of the target category and applies the
     * percentage to that amount.
     *
     * @param sale the sale to evaluate
     * @return the discount amount
     */
    @Override
    public double calculateDiscount(Sale sale) {
        double categoryTotal = 0;
        for (Sale.AmountOfProduct item : sale.getProductTrack()) {
            Product product = item.getSoldProduct();
            if (belongsToCategory(product)) {
                categoryTotal += product.getPrice() * item.getProductAmount();
            }
        }
        return categoryTotal * percentage / 100;
    }

    /**
     * Checks whether a product belongs to the target category.
     *
     * @param product the product to check
     * @return true if the product is of the target category
     */
    private boolean belongsToCategory(Product product) {
        if ("VIDEOGAME".equals(targetCategory)) {
            return product instanceof VideoGame;
        }
        if ("CONSOLE".equals(targetCategory)) {
            return product instanceof Console;
        }
        return false;
    }
}
