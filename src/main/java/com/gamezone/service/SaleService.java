package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SalePersistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Provides the business operations available for registering and querying
 * sales, backed by a {@link SalePersistence}.
 */
public class SaleService {

    private final SalePersistence repository;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final SellerService sellerService;
    private final CustomerService customerService;
    private final List<Sale> sales;

    /**
     * Creates a new service backed by the given repository and collaborating
     * services, loading the current sales history into memory.
     *
     * @param repository the repository used to persist and load sales
     * @param productService the service used to resolve and update products
     * @param accessoryService the service used to resolve and update accessories
     * @param sellerService the service used to resolve sellers
     * @param customerService the service used to resolve customers
     */
    public SaleService(SalePersistence repository, ProductService productService,
            AccessoryService accessoryService, SellerService sellerService,
            CustomerService customerService) {
        this.repository = repository;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.sellerService = sellerService;
        this.customerService = customerService;
        this.sales = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registers a new sale after validating that it contains at least one
     * item, that the customer, seller, and items (products or accessories)
     * exist, and that enough stock is available for every requested item.
     * Discounts the sold units from the corresponding inventory, records the
     * sale and persists the updated sales history.
     *
     * AmountOfProduct is a non-static inner class of Sale, so it can't be
     * built before a Sale instance exists: the sale is created first with an
     * empty track, then its AmountOfProduct entries are built from that
     * instance.
     *
     * @param customerId the id of the customer making the purchase
     * @param employeeCode the employee code of the seller handling the sale
     * @param productQuantities map of product or accessory id to quantity being sold
     * @return the registered sale
     * @throws IllegalArgumentException if the map is empty, if the customer,
     *                                  seller, or an item cannot be found, if a
     *                                  quantity is not positive, or if stock is
     *                                  insufficient
     */
    public Sale registerSale(long customerId, long employeeCode, Map<String, Integer> productQuantities) {
        if (productQuantities == null || productQuantities.isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        }

        Customer customer = customerService.searchCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Cliente no encontrado: " + customerId);
        }

        Seller seller = sellerService.searchSeller(employeeCode);
        if (seller == null) {
            throw new IllegalArgumentException("Vendedor no encontrado: " + employeeCode);
        }

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            String productId = entry.getKey();
            int amount = entry.getValue();

            Product product = findProduct(productId);
            if (product == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + productId);
            }
            if (amount <= 0) {
                throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero para el producto: " + product.getTitle());
            }
            if (!hasEnoughStock(productId, amount)) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para el producto: " + product.getTitle());
            }
        }

        long uId = System.currentTimeMillis();
        Sale sale = new Sale(uId, LocalDate.now(), new ArrayList<>(), seller, customer);
        sale.setProductTrack(buildProductTrack(sale, productQuantities));

        // Every item was validated above, so discounting the stock cannot fail midway
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            updateStock(entry.getKey(), -entry.getValue());
        }

        sales.add(sale);
        repository.saveAll(sales);
        return sale;
    }

    /**
     * Returns an unmodifiable view of every registered sale.
     *
     * @return the list of all sales
     */
    public List<Sale> viewAllSales() {
        return Collections.unmodifiableList(sales);
    }

    /**
     * Returns the sales made by the customer with the given id.
     *
     * @param customerId the id of the customer to filter by
     * @return the list of matching sales
     */
    public List<Sale> viewSalesByCustomer(long customerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getCustomer().getiD() == customerId) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Returns the sales handled by the seller with the given employee code.
     *
     * @param employeeCode the employee code to filter by
     * @return the list of matching sales
     */
    public List<Sale> viewSalesBySeller(long employeeCode) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale.getSeller().getEmployeeCode() == employeeCode) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Builds the AmountOfProduct entries for a sale. Requires the Sale
     * instance to already exist, since AmountOfProduct is a non-static
     * inner class of Sale.
     *
     * @param sale              the sale these entries belong to
     * @param productQuantities map of productId to quantity
     * @return the resulting product track
     */
    private ArrayList<Sale.AmountOfProduct> buildProductTrack(Sale sale, Map<String, Integer> productQuantities) {
        ArrayList<Sale.AmountOfProduct> productTrack = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            Product product = findProduct(entry.getKey());
            productTrack.add(sale.new AmountOfProduct(product, entry.getValue()));
        }
        return productTrack;
    }

    /**
     * Looks up a sellable item by its ID: first among the regular products
     * (video games and consoles), then among the accessories.
     *
     * @param productId the ID of the product or accessory to find
     * @return the matching item, or {@code null} if none is found
     */
    private Product findProduct(String productId) {
        Product product = findRegularProduct(productId);
        if (product != null) {
            return product;
        }
        return accessoryService.findById(productId);
    }

    /**
     * Looks up a regular product (video game or console) using
     * {@link ProductService#listProducts()}, since {@code ProductService}
     * does not expose a direct search method.
     *
     * @param productId the ID of the product to find
     * @return the matching product, or {@code null} if none is found
     */
    private Product findRegularProduct(String productId) {
        for (Product product : productService.listProducts()) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }

    /**
     * Checks the stock of an item, delegating to the service that owns it.
     *
     * @param productId the ID of the product or accessory
     * @param amount the requested quantity
     * @return true if there are enough units
     */
    private boolean hasEnoughStock(String productId, int amount) {
        if (findRegularProduct(productId) != null) {
            return productService.hasEnoughStock(productId, amount);
        }
        return accessoryService.hasEnoughStock(productId, amount);
    }

    /**
     * Updates the stock of an item, delegating to the service that owns it.
     *
     * @param productId the ID of the product or accessory
     * @param quantity units to add (positive) or remove (negative)
     */
    private void updateStock(String productId, int quantity) {
        if (findRegularProduct(productId) != null) {
            productService.updateStock(productId, quantity);
        } else {
            accessoryService.updateStock(productId, quantity);
        }
    }
}
