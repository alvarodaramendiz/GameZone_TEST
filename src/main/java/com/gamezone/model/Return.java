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
     * Adds up the prices of the returned products, stores the result as the
     * refunded amount and returns it.
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double finalAmount = 0;
        for (Product product : returnedProducts) {
            finalAmount += product.getPrice();
        }
        this.reimbursedAmount = finalAmount;
        return finalAmount;
    }

    /**
     * Builds a receipt in Spanish with the details of the return: ID, date,
     * original sale, returned products with their prices, reason and refund.
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
            receipt.append("PRODUCTO #").append(index).append("       : ")
                    .append(product.getTitle())
                    .append(" - $").append(product.getPrice()).append("\n");
        }
        receipt.append("MOTIVO            : ").append(returnReason).append("\n");
        receipt.append("MONTO REEMBOLSADO : $").append(reimbursedAmount).append("\n");
        return receipt.toString();
    }
}
