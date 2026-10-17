package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Sale.AmountOfProduct;
import com.gamezone.model.Seller;

import com.gamezone.service.CustomerService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SellerService;

import utilities.PlainArchives;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence of {@link Sale} instances as CSV records in
 * the file defined by {@link PlainArchives#SALES}, resolving seller,
 * customer, and product references through the injected
 * {@link ProductService}, {@link SellerService}, and {@link CustomerService}.
 *
 * Line format:
 * <pre>
 * uId;date;employeeCode;customerId;totalValue;productId:amount|...;promotionName;discountAmount
 * </pre>
 * The last two fields are optional so older lines can still be loaded.
 */
public class SalePersistence {

    private static final String FILE_PATH = PlainArchives.SALES;
    private static final String FIELD_SEPARATOR = ";";
    private static final String PRODUCT_SEPARATOR = "\\|";
    private static final String PRODUCT_JOINER = "|";
    private static final String PRODUCT_AMOUNT_SEPARATOR = ":";

    private final ProductService productService;
    private final SellerService sellerService;
    private final CustomerService customerService;

    /**
     * Creates a new repository that resolves sale references through the
     * given services.
     *
     * @param productService  the service used to resolve product references
     * @param sellerService   the service used to resolve seller references
     * @param customerService the service used to resolve customer references
     */
    public SalePersistence(ProductService productService, SellerService sellerService, CustomerService customerService) {
        this.productService = productService;
        this.sellerService = sellerService;
        this.customerService = customerService;
    }

    /**
     * Overwrites the CSV file with the given list of sales.
     *
     * @param sales the complete list of sales to persist
     */
    public void saveAll(List<Sale> sales) {
        Path path = Path.of(FILE_PATH);
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directory for " + FILE_PATH, e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(toCsvLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save sales to " + FILE_PATH, e);
        }
    }

    /**
     * Reads the CSV file and reconstructs the list of sales, resolving each
     * sale's seller, customer, and products by id.
     *
     * @return the list of sales found in the file, or an empty list if the
     *         file does not exist
     */
    public List<Sale> loadAll() {
        List<Sale> sales = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return sales;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                sales.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load sales from " + FILE_PATH, e);
        }
        return sales;
    }

    private String toCsvLine(Sale sale) {
        return String.join(FIELD_SEPARATOR,
            String.valueOf(sale.getuId()),
            sale.getDate().toString(),
            String.valueOf(sale.getSeller().getEmployeeCode()),
            String.valueOf(sale.getCustomer().getiD()),
            String.valueOf(sale.getTotalValue()),
            productTrackToField(sale.getProductTrack()),
            sanitize(sale.getAppliedPromotionName()),
            String.valueOf(sale.getDiscountAmount()));
    }

    private String productTrackToField(List<AmountOfProduct> productTrack) {
        if (productTrack == null || productTrack.isEmpty()) {
            return "";
        }
        StringBuilder field = new StringBuilder();
        for (int i = 0; i < productTrack.size(); i++) {
            AmountOfProduct item = productTrack.get(i);
            field.append(item.getSoldProduct().getProductId())
                 .append(PRODUCT_AMOUNT_SEPARATOR)
                 .append(item.getProductAmount());
            if (i < productTrack.size() - 1) {
                field.append(PRODUCT_JOINER);
            }
        }
        return field.toString();
    }

    private Sale fromCsvLine(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        if (fields.length < 5) {
            throw new RuntimeException("Línea de venta inválida: " + line);
        }

        long uId = Long.parseLong(fields[0]);
        LocalDate date = LocalDate.parse(fields[1]);
        long employeeCode = Long.parseLong(fields[2]);
        long customerId = Long.parseLong(fields[3]);
        double totalValue = Double.parseDouble(fields[4]);
        String productsField = fields.length > 5 ? fields[5] : "";
        String promotionName = fields.length > 6 ? fields[6] : "";
        double discountAmount = fields.length > 7 && !fields[7].isBlank()
            ? Double.parseDouble(fields[7]) : 0;

        Seller seller = sellerService.searchSeller(employeeCode);
        Customer customer = customerService.searchCustomer(customerId);
        if (seller == null || customer == null) {
            throw new RuntimeException("Failed to resolve seller or customer for sale " + uId);
        }

        Sale sale = new Sale(uId, date, new ArrayList<>(), seller, customer, promotionName, discountAmount);
        sale.setProductTrack(fieldToProductTrack(sale, productsField));
        sale.setTotalValue(totalValue);
        return sale;
    }

    private ArrayList<AmountOfProduct> fieldToProductTrack(Sale sale, String productsField) {
        ArrayList<AmountOfProduct> productTrack = new ArrayList<>();
        if (productsField == null || productsField.isEmpty()) {
            return productTrack;
        }
        for (String entry : productsField.split(PRODUCT_SEPARATOR)) {
            String[] parts = entry.split(PRODUCT_AMOUNT_SEPARATOR);
            if (parts.length < 2) {
                continue;
            }
            String productId = parts[0];
            int amount = Integer.parseInt(parts[1]);
            Product product = findProduct(productId);
            if (product == null) {
                throw new RuntimeException("Failed to resolve product " + productId + " for sale " + sale.getuId());
            }
            productTrack.add(sale.new AmountOfProduct(product, amount));
        }
        return productTrack;
    }

    /**
     * Prepares a text value to be stored as a field, replacing the field
     * separator so it cannot break the line.
     *
     * @param value the text to store, may be null
     * @return the text without field separators, or an empty string
     */
    private String sanitize(String value) {
        return value == null ? "" : value.replace(FIELD_SEPARATOR, ",");
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

