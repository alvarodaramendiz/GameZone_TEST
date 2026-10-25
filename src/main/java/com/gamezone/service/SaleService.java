package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SalePersistence;
import com.gamezone.model.Console;
import com.gamezone.validation.PersonValidator;
import com.gamezone.validation.ProductValidator;
import com.gamezone.validation.SaleValidator;
import java.util.Set;

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
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;
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
     * @param promotionService the service used to find the best active promotion
     * @param warrantyService the service used to create the warranties of a sale
     * @param sellerService the service used to resolve sellers
     * @param customerService the service used to resolve customers
     */
    public SaleService(SalePersistence repository, ProductService productService,
            AccessoryService accessoryService, PromotionService promotionService,
            WarrantyService warrantyService, SellerService sellerService,
            CustomerService customerService) {
        this.repository = repository;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;
        this.sellerService = sellerService;
        this.customerService = customerService;
        this.sales = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registers a new sale after validating that it contains at least one
     * item, that the customer, seller, and items (products or accessories)
     * exist, and that enough stock is available for every requested item.
     * Applies the active promotion with the highest discount, discounts the
     * sold units from the corresponding inventory, records the
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
    /**
     * Registers a new sale following a fixed order of operations:
     * <ol>
     *   <li>validates that the sale has at least one item;</li>
     *   <li>resolves each item as a product or accessory and validates its stock;</li>
     *   <li>creates the sale and calculates the subtotal;</li>
     *   <li>applies the best active promotion, calculated only over the items subtotal;</li>
     *   <li>generates the basic warranty of every console and the requested extended warranties;</li>
     *   <li>calculates the final total: subtotal - discount + extended warranties cost;</li>
     *   <li>updates the inventory through the service that owns each item;</li>
     *   <li>persists the sale (the warranties are persisted by the warranty service).</li>
     * </ol>
     *
     * @param customerId the id of the customer making the purchase
     * @param employeeCode the employee code of the seller handling the sale
     * @param productQuantities map of product or accessory id to quantity being sold
     * @param extendedWarrantyConsoleIds ids of the consoles in the sale that get an
     *                                   extended warranty, may be null or empty
     * @return the registered sale
     * @throws com.gamezone.exceptions.ResourceNotFoundException if the customer,
     *                                  the seller or an item cannot be found
     * @throws InvalidDataException     if a quantity is not positive
     * @throws BusinessRuleException    if the sale has no items, if stock is
     *                                  insufficient, or if an extended warranty is
     *                                  requested for something that is not a console
     *                                  of the sale
     */
    public Sale registerSale(long customerId, long employeeCode, Map<String, Integer> productQuantities,
            Set<String> extendedWarrantyConsoleIds) {

        Set<String> extendedIds = extendedWarrantyConsoleIds == null
            ? Collections.emptySet() : extendedWarrantyConsoleIds;

        Customer customer = customerService.searchCustomer(customerId);
        PersonValidator.validatePersonExists(customer, String.valueOf(customerId));
        Seller seller = sellerService.searchSeller(employeeCode);
        PersonValidator.validatePersonExists(seller, String.valueOf(employeeCode));

        List<Product> products = new ArrayList<>();
        if (productQuantities != null) {
            for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
                String productId = entry.getKey();
                int amount = entry.getValue();

                Product product = findProduct(productId);
                ProductValidator.validateProductExists(product, productId);
                if (amount <= 0) {
                    throw new InvalidDataException("cantidad",
                        "debe ser mayor que cero para el producto " + product.getTitle());
                }
                ProductValidator.validateStockAvailability(product, amount);
                products.add(product);
            }
        }
        SaleValidator.validateSaleData(customer, seller, products);
        validateExtendedWarrantyRequests(productQuantities, extendedIds);

        long uId = System.currentTimeMillis();
        Sale sale = new Sale(uId, LocalDate.now(), new ArrayList<>(), seller, customer, "", 0);
        sale.setProductTrack(buildProductTrack(sale, productQuantities));
        double subtotal = sale.getSubtotal();

        applyBestPromotion(sale);

        generateWarranties(sale, extendedIds);
        double extendedWarrantyCost = warrantyService.calculateAdditionalCost(sale);

        sale.setTotalValue(subtotal - sale.getDiscountAmount() + extendedWarrantyCost);

        for (Map.Entry<String, Integer> entry : productQuantities.entrySet()) {
            updateStock(entry.getKey(), -entry.getValue());
        }

        sales.add(sale);
        repository.saveAll(sales);
        return sale;
    }

    /**
     * Registers a sale without extended warranties. Equivalent to calling
     * {@link #registerSale(long, long, Map, Set)} with an empty set.
     *
     * @param customerId the id of the customer making the purchase
     * @param employeeCode the employee code of the seller handling the sale
     * @param productQuantities map of product or accessory id to quantity being sold
     * @return the registered sale
     */
    public Sale registerSale(long customerId, long employeeCode, Map<String, Integer> productQuantities) {
        return registerSale(customerId, employeeCode, productQuantities, Collections.emptySet());
    }

    /**
     * Applies to the sale the active promotion that gives the highest
     * discount, calculated only over the subtotal of the items. If no
     * promotion applies, the discount is zero. It does not set the total.
     *
     * @param sale the sale whose items are already loaded
     */
    private void applyBestPromotion(Sale sale) {
        double subtotal = sale.getSubtotal();
        Promotion best = promotionService.findBestPromotionFor(sale);
        if (best != null) {
            double discount = Math.min(best.calculateDiscount(sale), subtotal);
            sale.setAppliedPromotionName(best.getName());
            sale.setDiscountAmount(discount);
        } else {
            sale.setAppliedPromotionName("");
            sale.setDiscountAmount(0);
        }
    }

    /**
     * Generates the basic warranty of every console in the sale and the
     * extended warranty of the consoles whose id is in the given set.
     *
     * @param sale the sale whose items are already loaded
     * @param extendedIds ids of the consoles that get an extended warranty
     */
    private void generateWarranties(Sale sale, Set<String> extendedIds) {
        for (Sale.AmountOfProduct item : sale.getProductTrack()) {
            Product product = item.getSoldProduct();
            if (product instanceof Console) {
                warrantyService.assignBasicWarranty(product, sale, sale.getDate());
                if (extendedIds.contains(product.getProductId())) {
                    warrantyService.assignExtendedWarranty(product, sale, sale.getDate());
                }
            }
        }
    }

    /**
     * Checks that every extended warranty request refers to a console that
     * is part of the sale.
     *
     * @param productQuantities map of item id to quantity of the sale
     * @param extendedIds ids requested for an extended warranty
     * @throws BusinessRuleException if an id is not a console of the sale
     */
    private void validateExtendedWarrantyRequests(Map<String, Integer> productQuantities,
            Set<String> extendedIds) {
        for (String id : extendedIds) {
            if (!productQuantities.containsKey(id) || !(findProduct(id) instanceof Console)) {
                throw new BusinessRuleException(
                    "Solo las consolas de la venta pueden tener garantía extendida: " + id);
            }
        }
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