package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.GameZoneException;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Console;
import com.gamezone.persistence.ProductPersistence;
import com.gamezone.validation.ProductValidator;
import java.util.List;

/**
 * Provides the business rules for managing products: registration,
 * listing, and stock validation.
 *
 * @author jahdiel
 */
public class ProductService {

    private ProductPersistence productDAO;

    /**
     * Creates a new ProductService with its own product persistence.
     */
    public ProductService() {
        this.productDAO = new ProductPersistence();
    }

    /**
     * Registers a new video game after validating its data.
     *
     * @param videoGame the video game to register
     * @return a message indicating success or the validation error
     */
    public String registerVideoGame(VideoGame videoGame) {
        try {
            validateProduct(videoGame);
        } catch (GameZoneException e) {
            return "Error: " + e.getMessage();
        }
        if (!isValidAgeRating(videoGame.getAgeRating())) {
            return "Error: invalid age rating. Must be E, E10+, T, M, or AO.";
        }
        productDAO.saveProduct(videoGame);
        return "Video game registered successfully.";
    }

    /**
     * Registers a new console after validating its data.
     *
     * @param console the console to register
     * @return a message indicating success or the validation error
     */
    public String registerConsole(Console console) {
        try {
            validateProduct(console);
        } catch (GameZoneException e) {
            return "Error: " + e.getMessage();
        }
        productDAO.saveProduct(console);
        return "Console registered successfully.";
    }

    /**
     * Returns the list of all registered products.
     *
     * @return a list of all products in inventory
     */
    public List<Product> listProducts() {
        return productDAO.listProducts();
    }

    /**
     * Checks whether there is enough stock to sell a given quantity of a product.
     *
     * @param productId the ID of the product to check
     * @param quantity the requested quantity
     * @return true if there is enough stock, false otherwise
     */
    public boolean hasEnoughStock(String productId, int quantity) {
        Product product = productDAO.searchProduct(productId);
        return product != null && product.getStock() >= quantity;
    }

    /**
     * Updates the stock of a product by the given quantity and saves the
     * change. A positive quantity adds units and a negative one removes them.
     *
     * @param productId the ID of the product to update
     * @param quantity the number of units to add (positive) or remove (negative)
     * @throws com.gamezone.exceptions.ResourceNotFoundException if the product does not exist
     * @throws BusinessRuleException if the resulting stock would be negative
     */
    public void updateStock(String productId, int quantity) {
        Product product = productDAO.searchProduct(productId);
        ProductValidator.validateProductExists(product, productId);
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new BusinessRuleException("Stock insuficiente para el producto: " + product.getTitle());
        }
        product.setStock(newStock);
        productDAO.updateProduct(product);
    }

    /**
     * Validates the common attributes shared by all products.
     *
     * @param product the product to validate
     * @throws com.gamezone.exceptions.InvalidDataException if a value is not valid
     * @throws BusinessRuleException if a product with the same ID already exists
     */
    private void validateProduct(Product product) {
        ProductValidator.validateProductData(product.getProductId(), product.getTitle(),
                product.getPrice(), product.getStock());
        if (productDAO.existsProduct(product.getProductId())) {
            throw new BusinessRuleException("Ya existe un producto con el identificador " + product.getProductId());
        }
    }

    /**
     * Checks if a given age rating is a valid ESRB rating.
     *
     * @param ageRating the age rating to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidAgeRating(String ageRating) {
        return ageRating != null && (
                ageRating.equals("E") || ageRating.equals("E10+") ||
                ageRating.equals("T") || ageRating.equals("M") || ageRating.equals("AO"));
    }
    
    public String restoreStock(String productId, int quantity) {
        Product p = productDAO.searchProduct(productId);
        
        if (p != null) {
            if (quantity>=1) {
                p.setStock(p.getStock()+quantity);
                productDAO.updateProduct(p);
                return "Producto restockeado.";
            } else {
                return "Cantidad no válida. Intente de nuevo.";
            }
        } else {
            return "Producto no válido. Intente de nuevo.";
        }
    }
}