package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
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
    private final PromotionService promotionService;
    private final SellerService sellerService;
    private final CustomerService customerService;
    private final List<Sale> sales;

    /**
     * Creates a new service backed by the given repository and collaborating
     * services, loading the current sales history into memory.
     *
     * @param repository      the repository used to persist and load sales
     * @param productService  the service used to resolve and update products
     * @param promotionService the service used to find the best active promotion
     * @param sellerService   the service used to resolve sellers
     * @param customerService the service used to resolve customers
     */
    public SaleService(SalePersistence repository, ProductService productService,
            PromotionService promotionService, SellerService sellerService, CustomerService customerService) {
        this.repository = repository;
        this.productService = productService;
        this.promotionService = promotionService;
        this.sellerService = sellerService;
        this.customerService = customerService;
        this.sales = new ArrayList<>(repository.loadAll());
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
            if (!productService.hasEnoughStock(productId, amount)) {
                throw new IllegalArgumentException(
                    "Stock insuficiente para el producto: " + product.getTitle());
            }
        }

        long uId = System.currentTimeMillis();
        Sale sale = new Sale(uId, LocalDate.now(), new ArrayList<>(), seller, customer, "", 0);
        sale.setProductTrack(buildProductTrack(sale, productQuantities));
        applyBestPromotion(sale);

        // NOTA: a diferencia del SaleService de referencia, aquí no se
        // descuenta el stock todavía. ProductDAO solo sabe agregar productos
        // (saveProduct los añade al final del archivo) y no tiene un método
        // para actualizar uno existente. En cuanto ProductDAO/ProductService
        // tengan esa operación, aquí es donde se llamaría.

        sales.add(sale);
        repository.saveAll(sales);

        return sale;
    }
    
    /**
     * Applies to the sale the active promotion that gives the highest
     * discount, calculated only over the subtotal of the items, and sets the
     * final total (subtotal minus discount). If no promotion applies, the
     * total is the subtotal.
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
        sale.setTotalValue(subtotal - sale.getDiscountAmount());
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
}

