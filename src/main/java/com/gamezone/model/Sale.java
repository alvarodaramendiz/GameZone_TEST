package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Domain class that represents a sale transaction between a seller and a
 * customer. It keeps the sold items, the applied promotion, the discount and
 * the final total of the sale.
 *
 * @author ALVARO
 */
public final class Sale {

    /**
     * Groups a sold product (or accessory) with the number of units sold.
     * It is a non-static inner class, so it can only be created from an
     * existing {@link Sale} instance.
     */
    public class AmountOfProduct {
        
        

        private Product soldProduct; // Sold product or accessory
        private int productAmount; // Units sold of the product
        
        /**
         * Creates an empty item. Its product and amount must be set afterwards.
         */
        public AmountOfProduct() {
        }

        /**
         * Creates an item with the given product and quantity.
         *
         * @param soldProduct the sold product or accessory
         * @param productAmount the units sold
         */
        public AmountOfProduct(Product soldProduct, int productAmount) {
            this.soldProduct = soldProduct;
            this.productAmount = productAmount;
        }

        public Product getSoldProduct() {
            return soldProduct;
        }

        /**
         * Sets the sold product.
         *
         * @param soldProduct the sold product or accessory
         */
        public void setSoldProduct(Product soldProduct) {
            this.soldProduct = soldProduct;
        }

        public int getProductAmount() {
            return productAmount;
        }

        /**
         * Sets the units sold of the product.
         *
         * @param productAmount the units sold
         */
        public void setProductAmount(int productAmount) {
            this.productAmount = productAmount;
        }
        
        
    }
    
    private long uId; // Unique transaction identifier
    private LocalDate date; // Sale date (year-month-day)
    private ArrayList<AmountOfProduct> productTrack; // Sold items with their quantities
    private Seller seller; // Seller who handled the sale
    private Customer customer; // Customer who made the purchase
    private String appliedPromotionName; // Name of the promotion applied to the sale
    private double discountAmount; // Discount granted by the applied promotion, in pesos
    private double totalValue; // Final total of the sale, in pesos
    
    // Constructors
    
    // → sobre: public Sale(long uId, LocalDate date, ...)
    /**
     * Creates a new sale. The total value starts at zero and is set by the
     * service once the discount and the warranties are calculated.
     *
     * @param uId the unique transaction identifier
     * @param date the sale date
     * @param productTrack the sold items with their quantities
     * @param seller the seller who handled the sale
     * @param customer the customer who made the purchase
     * @param appliedPromotionName the name of the applied promotion, empty if
     * none
     * @param discountAmount the discount granted by the promotion, in pesos
     */
    public Sale(long uId, LocalDate date, ArrayList<AmountOfProduct> productTrack, Seller seller, Customer customer, String appliedPromotionName, double discountAmount) {
        this.uId = uId;
        this.date = date;
        this.productTrack = productTrack;
        this.seller = seller;
        this.customer = customer;
        this.appliedPromotionName = appliedPromotionName;
        this.discountAmount = discountAmount;
        this.totalValue = 0;
    }
    
    // Getters and setters

    public long getuId() {
        return uId;
    }

    public void setuId(long uId) {
        this.uId = uId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ArrayList<AmountOfProduct> getProductTrack() {
        return productTrack;
    }

    public void setProductTrack(ArrayList<AmountOfProduct> productTrack) {
        this.productTrack = productTrack;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCostumer(Customer costumer) {
        this.customer = costumer;
    }
    
    public double getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(double total) {
        this.totalValue = total;
    }

    /**
     * Returns the name of the promotion applied to the sale.
     *
     * @return the promotion name, or an empty string if no promotion was applied
     */
    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    /**
     * Sets the name of the promotion applied to the sale.
     *
     * @param appliedPromotionName the promotion name
     */
    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }

    /**
     * Returns the discount granted by the applied promotion.
     *
     * @return the discount amount in pesos
     */
    public double getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Sets the discount granted by the applied promotion.
     *
     * @param discountAmount the discount amount in pesos
     */
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    /**
     * Calculates the subtotal of the sale: the sum of the price of every
     * sold item multiplied by its quantity, before any discount.
     *
     * @return the subtotal of the sale
     */
    public double getSubtotal() {
        double subtotal = 0;
        for (AmountOfProduct item : productTrack) {
            subtotal += item.getSoldProduct().getPrice() * item.getProductAmount();
        }
        return subtotal;
    }
    
        /**
     * Returns the cost of the extended warranties included in the sale. It is
     * derived from the stored values (total = subtotal - discount + extended
     * warranty cost), so it survives a reload from the sales file without
     * adding a new persisted field.
     *
     * @return the extended warranty cost in pesos, never negative
     */
    public double getExtendedWarrantyCost() {
        double cost = totalValue - getSubtotal() + discountAmount;
        return Math.max(0, Math.round(cost * 100) / 100.0);
    }

    /**
     * Builds the receipt of the sale in Spanish, showing the sold items, the
     * subtotal, the applied promotion with its discount and the final total.
     *
     * @return the formatted receipt
     */
    public String receiptGen() {
        StringBuilder saleReceipt = new StringBuilder();
        int index = 0;
        saleReceipt.append("============== GAMEZONE UNICESAR FACTURA ==============\n");
        saleReceipt.append("IDENTIFICADOR    : ").append(uId).append("\n");
        saleReceipt.append("FECHA DE VENTA   : ").append(date).append("\n");
        saleReceipt.append("CLIENTE          : ").append(customer.getName()).append("\n");
        saleReceipt.append("VENDEDOR         : ").append(seller.getName()).append("\n");
        saleReceipt.append("================ PRODUCTOS ADQUIRIDOS =================\n");
        for (AmountOfProduct e : productTrack) {
            index++;
            saleReceipt.append("PRODUCTO #").append(index).append("      : ")
                    .append(e.getSoldProduct().getTitle())
                    .append(" - $").append(e.getSoldProduct().getPrice())
                    .append(" x ").append(e.getProductAmount()).append("\n");
        }
        saleReceipt.append("===================== VALOR TOTAL =====================\n");
        saleReceipt.append("SUBTOTAL            : $").append(getSubtotal()).append("\n");
        boolean hasPromotion = appliedPromotionName != null && !appliedPromotionName.isBlank();
        saleReceipt.append("DESCUENTO           : ")
                .append(hasPromotion ? appliedPromotionName : "Ninguna")
                .append(" (-$").append(discountAmount).append(")\n");
        saleReceipt.append("GARANTÍA EXTENDIDA  : +$").append(getExtendedWarrantyCost()).append("\n");
        saleReceipt.append("TOTAL A PAGAR       : $").append(totalValue).append("\n");
        return saleReceipt.toString();
    }

    /**
    * Checks if the sale is eligible for a return within 30 calendar days.
    *
    * @return true if the sale can be returned, false otherwise
    */
    public boolean canBeReturned() {
    LocalDate today = LocalDate.now();
    long days = java.time.temporal.ChronoUnit.DAYS.between(date, today);

    return days >= 0 && days <= 30;
}
    
}
