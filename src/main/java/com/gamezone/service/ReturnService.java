package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.persistence.ReturnPersistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Provides the business operations for registering and querying product
 * returns, and the monthly balance report (sales minus returns).
 */
public class ReturnService {

    private final ReturnPersistence repository;
    private final SaleService saleService;
    private final ProductService productService;
    private final List<Return> returns;

    /**
     * Creates a new service and loads the stored returns into memory.
     *
     * @param repository     the persistence used to save and load returns
     * @param saleService    the service used to look up sales
     * @param productService the service used to restore stock
     */
    public ReturnService(ReturnPersistence repository, SaleService saleService, ProductService productService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registers a new return. Validates that the sale exists, that it is
     * within the 30-day period and that every product belongs to the sale.
     * Then calculates the refund, restores the stock and saves the return.
     *
     * @param saleId     the ID of the original sale
     * @param productIds the IDs of the products being returned
     * @param reason     the reason for the return
     * @return the registered return
     * @throws IllegalArgumentException if any validation fails
     */
    public Return registerReturn(long saleId, List<String> productIds, String reason, double amount) {
        Sale sale = findSale(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("La venta indicada no existe: " + saleId);
        }
        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("La venta supera el plazo de 30 días para devoluciones.");
        }     
        if (amount<0) {
            throw new IllegalArgumentException("La venta contiene reembolso negativo.");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un producto a devolver.");
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            Product product = findProductInSale(sale, productId);
            if (product == null) {
                throw new IllegalArgumentException("El producto " + productId + " no pertenece a la venta " + saleId + ".");
            }
            returnedProducts.add(product);
        }

        String idReturn = String.valueOf(System.currentTimeMillis());
        Return newReturn = new Return(idReturn, LocalDate.now(), sale, returnedProducts, reason, 0);
        newReturn.calculateRefundAmount();

        for (Product product : returnedProducts) {
            productService.restoreStock(product.getProductId(), 1);
        }

        returns.add(newReturn);
        repository.saveAll(returns);
        return newReturn;
    }

    /**
     * Returns every registered return.
     *
     * @return the list of all returns
     */
    public List<Return> viewAllReturns() {
        return Collections.unmodifiableList(returns);
    }

    /**
     * Returns the returns whose original sale belongs to the given customer.
     *
     * @param customerId the ID of the customer
     * @return the list of matching returns
     */
    public List<Return> viewReturnsByCustomer(String customerId) {
        List<Return> result = new ArrayList<>();
        for (Return r : returns) {
            if (String.valueOf(r.getSale().getCustomer().getiD()).equals(customerId)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Returns the returns associated with the given sale.
     *
     * @param saleId the ID of the sale
     * @return the list of matching returns
     */
    public List<Return> viewReturnsBySale(String saleId) {
        List<Return> result = new ArrayList<>();
        for (Return r : returns) {
            if (String.valueOf(r.getSale().getuId()).equals(saleId)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Calculates the net balance of a month: total sales minus total refunds.
     *
     * @param month the month (1-12)
     * @param year  the year
     * @return the net balance of the period
     */
    public double generateMonthlyBalance(int month, int year) {
        double totalSales = 0;
        for (Sale sale : saleService.viewAllSales()) {
            if (sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year) {
                totalSales += sale.getTotalValue();
            }
        }
        double totalReturns = 0;
        for (Return r : returns) {
            if (r.getDate().getMonthValue() == month && r.getDate().getYear() == year) {
                totalReturns += r.getReimbursedAmount();
            }
        }
        return totalSales - totalReturns;
    }

    private Sale findSale(long saleId) {
        for (Sale sale : saleService.viewAllSales()) {
            if (sale.getuId() == (saleId)) {
                return sale;
            }
        }
        return null;
    }

    private Product findProductInSale(Sale sale, String productId) {
        for (Sale.AmountOfProduct item : sale.getProductTrack()) {
            if (item.getSoldProduct().getProductId().equals(productId)) {
                return item.getSoldProduct();
            }
        }
        return null;
    }
}
