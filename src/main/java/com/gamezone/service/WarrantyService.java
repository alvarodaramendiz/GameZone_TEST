package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.service.WarrantyService;
import com.gamezone.persistence.SalePersistence;
import com.gamezone.persistence.WarrantyRecord;
import com.gamezone.persistence.WarrantyRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the business operations of the warranty module: assignment of
 * basic and extended warranties, search of the warranty of a product in a
 * sale, and queries of active warranties and warranties about to expire.
 * Every change is persisted through the {@link WarrantyRepository}.
 *
 * @author jahdiel
 */
public class WarrantyService {

    private static final String BASIC_SUFFIX = "B";
    private static final String EXTENDED_SUFFIX = "E";
    public static final int EXPIRING_SOON_DAYS = 30;
    
    private final WarrantyRepository repository;
    private final SalePersistence saleRepository;
    private final ProductService productService;
    private final List<Warranty> warranties;

    /**
     * Creates the service and loads the stored warranties into memory,
     * resolving the sale and the product of each one from their identifiers.
     *
     * @param repository     the repository used to persist and load warranties
     * @param saleRepository the repository used to resolve the sale of each warranty
     * @param productService the service used to resolve the product of each warranty
     */
    public WarrantyService(WarrantyRepository repository, SalePersistence saleRepository,
            ProductService productService) {
        this.repository = repository;
        this.saleRepository = saleRepository;
        this.productService = productService;
        this.warranties = new ArrayList<>(resolveAll(repository.loadAll()));
    }

    /**
     * Turns the stored records into warranties, looking up their sale and
     * product. Records whose sale or product no longer exist are skipped.
     *
     * @param records the raw records read by the repository
     * @return the resolved warranties
     */
    private List<Warranty> resolveAll(List<WarrantyRecord> records) {
        Map<String, Sale> salesById = new HashMap<>();
        for (Sale sale : saleRepository.loadAll()) {
            salesById.put(String.valueOf(sale.getuId()), sale);
        }
        Map<String, Product> productsById = new HashMap<>();
        for (Product product : productService.listProducts()) {
            productsById.put(product.getProductId(), product);
        }
        List<Warranty> result = new ArrayList<>();
        for (WarrantyRecord record : records) {
            Sale sale = salesById.get(record.getSaleId());
            Product product = productsById.get(record.getProductId());
            if (sale == null || product == null) {
                System.out.println("Aviso: garantía " + record.getWarrantyId()
                    + " sin venta o producto asociado, se omite.");
                continue;
            }
            if ("EXTENDED".equals(record.getType())) {
                result.add(new ExtendedWarranty(record.getWarrantyId(), product, sale, record.getStartDate()));
            } else {
                result.add(new BasicWarranty(record.getWarrantyId(), product, sale, record.getStartDate()));
            }
        }
        return result;
    }

    /**
     * Creates and persists a basic warranty, which has no additional cost.
     *
     * @param product   the product covered by the warranty
     * @param sale      the sale in which the product was sold
     * @param startDate the first day of coverage, usually the sale date
     * @return the created warranty
     * @throws IllegalArgumentException if any value is missing or the product
     *                                  already has a basic warranty in that sale
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        validateAssignment(product, sale, startDate);
        String warrantyId = buildWarrantyId(sale, product, BASIC_SUFFIX);
        if (findById(warrantyId) != null) {
            throw new IllegalArgumentException("El producto ya tiene garantía básica en esta venta.");
        }
        BasicWarranty warranty = new BasicWarranty(warrantyId, product, sale, startDate);
        save(warranty);
        return warranty;
    }

    /**
     * Creates and persists an extended warranty. Its additional cost is 10%
     * of the product price and can be read with getAdditionalCost.
     *
     * @param product   the product covered by the warranty
     * @param sale      the sale in which the product was sold
     * @param startDate the first day of coverage, usually the sale date
     * @return the created warranty
     * @throws IllegalArgumentException if any value is missing or the product
     *                                  already has an extended warranty in that sale
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        validateAssignment(product, sale, startDate);
        String warrantyId = buildWarrantyId(sale, product, EXTENDED_SUFFIX);
        if (findById(warrantyId) != null) {
            throw new IllegalArgumentException("El producto ya tiene garantía extendida en esta venta.");
        }
        ExtendedWarranty warranty = new ExtendedWarranty(warrantyId, product, sale, startDate);
        save(warranty);
        return warranty;
    }

    /**
     * Returns the warranty of a product in a specific sale. When the product
     * has both a basic and an extended warranty, the extended one is returned
     * because it gives the widest coverage.
     *
     * @param productId identifier of the product
     * @param saleId    identifier of the sale
     * @return the warranty, or null if the product has no warranty in that sale
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        if (productId == null || saleId == null) {
            return null;
        }
        Warranty found = null;
        for (Warranty warranty : warranties) {
            boolean sameProduct = warranty.getProduct().getProductId().equals(productId.trim());
            boolean sameSale = String.valueOf(warranty.getSale().getuId()).equals(saleId.trim());
            if (sameProduct && sameSale) {
                if (warranty instanceof ExtendedWarranty) {
                    return warranty;
                }
                found = warranty;
            }
        }
        return found;
    }

    /**
     * Returns every registered warranty.
     *
     * @return an unmodifiable list with all the warranties
     */
    public List<Warranty> listAllWarranties() {
        return Collections.unmodifiableList(warranties);
    }
    
        /**
     * Returns the status of a warranty on the current date.
     *
     * @param warranty the warranty to evaluate
     * @return "Vencida", "Por expirar" (ends within EXPIRING_SOON_DAYS) or "Vigente"
     */
    public String getStatus(Warranty warranty) {
        LocalDate today = LocalDate.now();
        if (!warranty.isActive(today)) {
            return "Vencida";
        }
        if (!warranty.getEndDate().isAfter(today.plusDays(EXPIRING_SOON_DAYS))) {
            return "Por expirar";
        }
        return "Vigente";
    }

    /**
     * Returns the warranties that are active on the current date.
     *
     * @return the active warranties
     */
    public List<Warranty> listActiveWarranties() {
        LocalDate today = LocalDate.now();
        List<Warranty> result = new ArrayList<>();
        for (Warranty warranty : warranties) {
            if (warranty.isActive(today)) {
                result.add(warranty);
            }
        }
        return result;
    }

    /**
     * Returns the active warranties whose end date falls within the next
     * given number of days, counting from today.
     *
     * @param daysAhead number of days to look ahead
     * @return the warranties about to expire
     * @throws IllegalArgumentException if daysAhead is negative
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        if (daysAhead < 0) {
            throw new IllegalArgumentException("Los días de anticipación no pueden ser negativos.");
        }
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);
        List<Warranty> result = new ArrayList<>();
        for (Warranty warranty : warranties) {
            LocalDate endDate = warranty.getEndDate();
            if (warranty.isActive(today) && !endDate.isAfter(limit)) {
                result.add(warranty);
            }
        }
        return result;
    }

    /**
     * Searches a warranty by its identifier.
     *
     * @param warrantyId identifier of the warranty
     * @return the warranty, or null if it does not exist
     */
    private Warranty findById(String warrantyId) {
        for (Warranty warranty : warranties) {
            if (warranty.getWarrantyId().equals(warrantyId)) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * Builds the identifier of a warranty from its sale, its product and its type.
     *
     * @param sale    the sale of the warranty
     * @param product the product of the warranty
     * @param suffix  B for basic or E for extended
     * @return the warranty identifier
     */
    private String buildWarrantyId(Sale sale, Product product, String suffix) {
        return "GAR-" + sale.getuId() + "-" + product.getProductId() + "-" + suffix;
    }

    /**
     * Validates the data needed to assign a warranty.
     *
     * @param product   the product covered by the warranty
     * @param sale      the sale in which the product was sold
     * @param startDate the first day of coverage
     * @throws IllegalArgumentException if any value is missing
     */
    private void validateAssignment(Product product, Sale sale, LocalDate startDate) {
        if (product == null) {
            throw new IllegalArgumentException("La garantía debe tener un producto.");
        }
        if (sale == null) {
            throw new IllegalArgumentException("La garantía debe tener una venta.");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("La garantía debe tener fecha de inicio.");
        }
    }
    
    /**
     * Sums the additional cost of every warranty of a sale. Basic warranties
     * cost 0, so the result is the cost of the extended warranties.
     *
     * @param sale the sale to evaluate
     * @return the total additional cost
     */
    public double calculateAdditionalCost(Sale sale) {
        double total = 0;
        for (Warranty warranty : warranties) {
            if (warranty.getSale().getuId() == sale.getuId()) {
                total += warranty.getAdditionalCost();
            }
        }
        return total;
    }

    /**
     * Adds a warranty to the list and persists the whole list.
     *
     * @param warranty the warranty to store
     */
    private void save(Warranty warranty) {
        warranties.add(warranty);
        repository.saveAll(warranties);
    }
}
