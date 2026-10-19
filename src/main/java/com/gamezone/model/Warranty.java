package com.gamezone.model;

import java.time.LocalDate;

/**
 * Base class for all warranties given on products sold in the GameZone
 * Unicesar store. The end date is calculated automatically from the start
 * date and the duration defined by each subclass.
 *
 * @author jahdiel
 */
public abstract class Warranty {

    private String warrantyId;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new Warranty. The end date is calculated by adding the
     * duration of the warranty type to the start date.
     *
     * @param warrantyId the unique ID of the warranty
     * @param product the product covered by the warranty
     * @param sale the sale in which the product was bought
     * @param startDate the date the warranty starts (the sale date)
     */
    public Warranty(String warrantyId, Product product, Sale sale, LocalDate startDate) {
        this.warrantyId = warrantyId;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    /** @return the unique ID of the warranty */
    public String getWarrantyId() {
        return warrantyId;
    }

    /** @return the product covered by the warranty */
    public Product getProduct() {
        return product;
    }

    /** @return the sale in which the product was bought */
    public Sale getSale() {
        return sale;
    }

    /** @return the date the warranty starts */
    public LocalDate getStartDate() {
        return startDate;
    }

    /** @return the date the warranty ends */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Gets the duration of the warranty in months.
     *
     * @return the duration in months
     */
    public abstract int getDurationInMonths();

    /**
     * Gets the name of the warranty type.
     *
     * @return the warranty type name
     */
    public abstract String getWarrantyType();

    /**
     * Gets the extra cost this warranty adds to the sale.
     *
     * @return the additional cost
     */
    public abstract double getAdditionalCost();

    /**
     * Checks whether the warranty is valid on the given date, including
     * the start and end days.
     *
     * @param date the date to check
     * @return true if the date is between the start and end dates
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Builds a certificate in Spanish with the details of the warranty.
     *
     * @return the formatted warranty certificate
     */
    public String generateWarrantyCertificate() {
        StringBuilder certificate = new StringBuilder();
        certificate.append("========== GAMEZONE UNICESAR CERTIFICADO DE GARANTÍA ==========\n");
        certificate.append("IDENTIFICADOR     : ").append(warrantyId).append("\n");
        certificate.append("TIPO              : ").append(getWarrantyType()).append("\n");
        certificate.append("PRODUCTO          : ").append(product.getTitle()).append("\n");
        certificate.append("VENTA             : ").append(sale.getuId()).append("\n");
        certificate.append("FECHA DE INICIO   : ").append(startDate).append("\n");
        certificate.append("FECHA DE FIN      : ").append(endDate).append("\n");
        certificate.append("DURACIÓN          : ").append(getDurationInMonths()).append(" meses\n");
        certificate.append("COSTO ADICIONAL   : $").append(getAdditionalCost()).append("\n");
        return certificate.toString();
    }
}
