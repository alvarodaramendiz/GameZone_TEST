package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SalePersistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
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
    private final SellerService sellerService;
    private final CustomerService customerService;
    private final WarrantyService warrantyService;
    private final List<Sale> sales;

    /**
     * Creates a new service backed by the given repository and collaborating
     * services, loading the current sales history into memory.
     *
     * @param repository      the repository used to persist and load sales
     * @param productService  the service used to resolve and update products
     * @param sellerService   the service used to resolve sellers
     * @param customerService the service used to resolve customers
     * @param warrantyService the service used to resolve warranties
     */
    public SaleService(SalePersistence repository, ProductService productService,
            SellerService sellerService, CustomerService customerService, WarrantyService warrantyService) {
        this.repository = repository;
        this.productService = productService;
        this.sellerService = sellerService;
        this.customerService = customerService;
        this.sales = new ArrayList<>(repository.loadAll());
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new sale after validating that it contains at least one
     * product, that the customer, seller, and products exist, and that
     * enough stock is available for every requested product. Records the
     * sale and persists the updated sales history.
     *
     * AmountOfProduct is a non-static inner class of Sale, so it can't be
     * built before a Sale instance exists: the sale is created first with an
     * empty track, then its AmountOfProduct entries are built from that
     * instance.
     *
     * @param customerId        the id of the customer making the purchase
     * @param employeeCode      the employee code of the seller handling the sale
     * @param productQuantities map of productId to quantity being sold
     * @return the registered sale
     * @throws IllegalArgumentException if the product map is empty, if the
     *                                  customer, seller, or a product cannot
     *                                  be found, or if stock is insufficient
     */
    public Sale registerSale(long customerId, long employeeCode, Map<String, Integer> productQuantities, List<String> productIdsWithExtendedWarranty) {
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
            if (amount <= 0) {
                throw new IllegalArgumentException(
                    "Cantidad inválida de producto, introdujo: " + amount);
            }
            Product product = findProduct(productId);
            if (product == null) {
                throw new IllegalArgumentException("Producto no encontrado: " + productId);
            }
            if (!productService.hasEnoughStock(productId, amount)) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para el producto: " + product.getTitle());
            }
        }

        long uId = System.currentTimeMillis();
        Sale sale = new Sale(uId, LocalDate.now(), new ArrayList<>(), seller, customer);
        ArrayList<Sale.AmountOfProduct> pA = buildProductTrack(sale, productQuantities);
        sale.setProductTrack(pA);

        sales.add(sale);
        repository.saveAll(sales);

        // Consoles always get a basic warranty; the extended one is added on request.
        for (Sale.AmountOfProduct item : pA) {
            Product product = item.getSoldProduct();
            if (product instanceof Console) {
                warrantyService.assignBasicWarranty(product, sale, sale.getDate());
                if (productIdsWithExtendedWarranty != null
                        && productIdsWithExtendedWarranty.contains(product.getProductId())) {
                    warrantyService.assignExtendedWarranty(product, sale, sale.getDate());
                }
            }
        }

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
     * Looks up a product by its ID using {@link ProductService#listProducts()},
     * since {@code ProductService} does not expose a direct search method.
     *
     * @param productId the ID of the product to find
     * @return the matching product, or {@code null} if none is found
     */
    private Product findProduct(String productId) {
        for (Product product : productService.listProducts()) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return null;
    }
    
        /**
     * Returns the sale total including the additional cost of its extended warranties.
     *
     * @param sale the sale to total
     * @return the products value plus the warranty cost
     */
    public double getSaleTotal(Sale sale) {
        return sale.saleTotalValue() + warrantyService.calculateAdditionalCost(sale);
    }

    /**
     * Filters the given product ids, keeping only those that are consoles.
     *
     * @param productIds identifiers of the products to check (e.g. the cart)
     * @return the ids of the products that are consoles
     */
    public List<String> findConsoleIds(Collection<String> productIds) {
        List<String> result = new ArrayList<>();
        for (String productId : productIds) {
            if (findProduct(productId) instanceof Console) {
                result.add(productId);
            }
        }
        return result;
    }
}
