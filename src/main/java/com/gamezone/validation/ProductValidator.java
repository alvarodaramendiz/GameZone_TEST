package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;

/**
 * Validation rules of the product module. All methods are static and throw
 * a custom exception when a rule is not met.
 *
 * @author jahdiel
 */
public class ProductValidator {

    /** Utility class: it is not meant to be instantiated. */
    private ProductValidator() {
    }

    /**
     * Validates the basic data of a product.
     *
     * @param id    identifier of the product
     * @param title name of the product
     * @param price unit price
     * @param stock units in inventory
     * @throws InvalidDataException if the id or title are empty, the price is
     *                              not positive or the stock is negative
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("identificador", "no puede estar vacío");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidDataException("título", "no puede estar vacío");
        }
        if (price <= 0) {
            throw new InvalidDataException("precio", "debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "no puede ser negativo");
        }
    }

    /**
     * Validates that a product has enough units for the requested quantity.
     *
     * @param product           the product to check
     * @param requestedQuantity units requested
     * @throws BusinessRuleException if the stock is not enough
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new BusinessRuleException("Stock insuficiente para el producto: " + product.getTitle());
        }
    }

    /**
     * Validates that a searched product exists.
     *
     * @param product   the product found, may be null
     * @param productId the identifier that was searched
     * @throws ResourceNotFoundException if the product is null
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("el producto", productId);
        }
    }
}
