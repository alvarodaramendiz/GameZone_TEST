package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Dominio de venta que incorpora las transacciones de venta entre vendedor y cliente
 * @author ALVARO
 */
public final class Sale {
    
    public class AmountOfProduct { // Clase que agrupa unidades y producto en una misma estructura
        
        private Product soldProduct; // Producto vendido
        private int productAmount; // Unidades del producto vendido

        public AmountOfProduct() {
        }

        public AmountOfProduct(Product soldProduct, int productAmount) {
            this.soldProduct = soldProduct;
            this.productAmount = productAmount;
        }

        public Product getSoldProduct() {
            return soldProduct;
        }

        public void setSoldProduct(Product soldProduct) {
            this.soldProduct = soldProduct;
        }

        public int getProductAmount() {
            return productAmount;
        }

        public void setProductAmount(int productAmount) {
            this.productAmount = productAmount;
        }
        
        
    }
    
    private long uId; // Identificador único de transacción
    private LocalDate date; // Guardado de fecha local en formato año - mes - día
    private ArrayList<AmountOfProduct> productTrack; // 
    private Seller seller; // Vendedor que genera la venta
    private Customer customer; // Consumidor o Cliente que realiza la compra
    private String appliedPromotionName; // Name of the promotion applied to the sale
    private double discountAmount; // Discount granted by the applied promotion, in pesos
    private double totalValue; // Valor total monetario de la venta
    
    // Constructores para inicialización parametrizada
    
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
    
    // Getters y setters para acceder a las variables de instancia

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
        saleReceipt.append("SUBTOTAL         : $").append(getSubtotal()).append("\n");
        boolean hasPromotion = appliedPromotionName != null && !appliedPromotionName.isBlank();
        saleReceipt.append("PROMOCIÓN        : ").append(hasPromotion ? appliedPromotionName : "Ninguna").append("\n");
        saleReceipt.append("DESCUENTO        : $").append(discountAmount).append("\n");
        saleReceipt.append("TOTAL A PAGAR    : $").append(totalValue).append("\n");
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
