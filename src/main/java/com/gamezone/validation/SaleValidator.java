package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import java.util.List;

/**
 * Validation rules of the sale module. All methods are static and throw a
 * custom exception when a rule is not met.
 *
 * @author jahdiel
 */
public class SaleValidator {

    /** Utility class: it is not meant to be instantiated. */
    private SaleValidator() {
    }

    /**
     * Validates the data needed to register a sale.
     *
     * @param customer the customer of the sale
     * @param seller   the seller of the sale
     * @param products the products of the sale
     * @throws InvalidDataException  if the customer or the seller are null
     * @throws BusinessRuleException if the sale has no products
     */
    public static void validateSaleData(Customer customer, Seller seller, List<Product> products) {
        if (customer == null) {
            throw new InvalidDataException("cliente", "es obligatorio");
        }
        if (seller == null) {
            throw new InvalidDataException("vendedor", "es obligatorio");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException("La venta debe contener al menos un producto.");
        }
    }

    /**
     * Validates that a searched sale exists.
     *
     * @param sale   the sale found, may be null
     * @param saleId the identifier that was searched
     * @throws ResourceNotFoundException if the sale is null
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("la venta", saleId);
        }
    }
}
