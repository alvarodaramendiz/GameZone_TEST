package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a product return in the GameZone Unicesar store.
 * A return references an original sale and contains some or all of the
 * products bought in that sale, the reason and the refunded amount.
 *
 * @author jahdiel
 */
public class Return {

    private String idReturn;
    private LocalDate date;
    private Sale sale;
    private List<Product> returnedProducts;
    private String returnReason;
    private double reimbursedAmount;

    /**
     * Creates a new Return.
     *
     * @param idReturn the unique ID of the return
     * @param date the date the return was made
     * @param sale the original sale being returned
     * @param returnedProducts the products returned from the sale
     * @param returnReason the reason for the return
     * @param reimbursedAmount the amount refunded to the customer
     * @throws IllegalArgumentException if the sale is null or no products are given
     */
    public Return(String idReturn, LocalDate date, Sale sale, List<Product> returnedProducts,
            String returnReason, double reimbursedAmount) {
        if (sale == null) {
            throw new IllegalArgumentException("La devolución debe tener una venta original.");
        }
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            throw new IllegalArgumentException("La devolución debe tener al menos un producto.");
        }
        this.idReturn = idReturn;
        this.date = date;
        this.sale = sale;
        this.returnedProducts = returnedProducts;
        this.returnReason = returnReason;
        this.reimbursedAmount = reimbursedAmount;
    }

    /** @return the unique ID of the return */
    public String getIdReturn() {
        return idReturn;
    }

    /** @return the date the return was made */
    public LocalDate getDate() {
        return date;
    }

    /** @return the original sale being returned */
    public Sale getSale() {
        return sale;
    }

    /** @return the products returned from the sale */
    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    /** @return the reason for the return */
    public String getReturnReason() {
        return returnReason;
    }

    /** @return the amount refunded to the customer */
    public double getReimbursedAmount() {
        return reimbursedAmount;
    }

    /**
     * Calculates the refund of the returned products. If the original sale
     * had a promotion, each product is refunded in proportion to the discount:
     * price x (1 - discount / subtotal). Stores the result and returns it.
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double finalAmount = 0;
        for (Product product : returnedProducts) {
            finalAmount += product.getPrice() * getPaidFactor();
        }
        this.reimbursedAmount = finalAmount;
        return finalAmount;
    }

    /**
     * Returns the part of the list price that the customer actually paid,
     * after the discount of the original sale: 1 - discount / subtotal.
     * Returns 1 when the sale had no discount or its subtotal is zero.
     *
     * @return the paid factor, between 0 and 1
     */
    private double getPaidFactor() {
        double subtotal = sale.getSubtotal();
        if (subtotal <= 0) {
            return 1;
        }
        return 1 - (sale.getDiscountAmount() / subtotal);
    }

    /**
     * Builds a receipt in Spanish with the details of the return: ID, date,
     * original sale, and for each returned product its list price, its share
     * of the sale discount and the refunded amount; then the reason and the
     * total refund.
     *
     * @return the formatted return receipt
     */
    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("============ GAMEZONE UNICESAR DEVOLUCIÓN ============\n");
        receipt.append("IDENTIFICADOR     : ").append(idReturn).append("\n");
        receipt.append("FECHA DEVOLUCIÓN  : ").append(date).append("\n");
        receipt.append("VENTA ORIGINAL    : ").append(sale.getuId()).append("\n");
        receipt.append("================ PRODUCTOS DEVUELTOS =================\n");
        int index = 0;
        for (Product product : returnedProducts) {
            index++;
            double refund = product.getPrice() * getPaidFactor();
            receipt.append("PRODUCTO #").append(index).append("       : ")
                    .append(product.getTitle()).append("\n");
            receipt.append("  PRECIO DE LISTA : $").append(String.format("%.2f", product.getPrice())).append("\n");
            receipt.append("  DESCUENTO       : $").append(String.format("%.2f", product.getPrice() - refund)).append("\n");
            receipt.append("  REEMBOLSO       : $").append(String.format("%.2f", refund)).append("\n");
        }
        receipt.append("MOTIVO            : ").append(returnReason).append("\n");
        receipt.append("MONTO REEMBOLSADO : $").append(reimbursedAmount).append("\n");
        return receipt.toString();
    }
}
