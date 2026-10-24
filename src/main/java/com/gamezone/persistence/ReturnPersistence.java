package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
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
 * Handles file-based persistence of {@link Return} instances in the file
 * defined by {@link PlainArchives#RETURNS}. Sale and product references are
 * resolved through the injected {@link SaleService} and {@link ProductService}.
 *
 * File format (one return per line):
 * idReturn;date;saleId;productId1|productId2|...;reason;reimbursedAmount
 */
public class ReturnPersistence {

    private static final String FILE_PATH = PlainArchives.RETURNS;
    private static final String FIELD_SEPARATOR = ";";

    private final SaleService saleService;
    private final ProductService productService;
    private final AccessoryService accessoryService;

    /**
     * Creates a new persistence object that resolves references through
     * the given services.
     *
     * @param saleService    the service used to resolve sale references
     * @param productService the service used to resolve product references
     * @param accessoryService the service used to resolve accessory references
     */
    public ReturnPersistence(SaleService saleService, ProductService productService,
            AccessoryService accessoryService) {
        this.saleService = saleService;
        this.productService = productService;
        this.accessoryService = accessoryService;
    }

    /**
     * Overwrites the returns file with the given list of returns.
     *
     * @param returns the complete list of returns to persist
     */
    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Return r : returns) {
                writer.write(toLine(r));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save returns to " + FILE_PATH, e);
        }
    }

    /**
     * Reads the returns file and rebuilds the list of returns.
     *
     * @return the list of returns, or an empty list if the file does not exist
     */
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        if (!Files.exists(Path.of(FILE_PATH))) {
            return returns;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                returns.add(fromLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load returns from " + FILE_PATH, e);
        }
        return returns;
    }

    private String toLine(Return r) {
        StringBuilder productIds = new StringBuilder();
        for (Product p : r.getReturnedProducts()) {
            if (productIds.length() > 0) {
                productIds.append("|");
            }
            productIds.append(p.getProductId());
        }
        return String.join(FIELD_SEPARATOR,
                r.getIdReturn(),
                r.getDate().toString(),
                String.valueOf(r.getSale().getuId()),
                productIds.toString(),
                r.getReturnReason().replace(FIELD_SEPARATOR, ","),
                String.valueOf(r.getReimbursedAmount()),
                String.valueOf(r.getWarrantyRefund()));
    }

    private Return fromLine(String line) {
        String[] fields = line.split(FIELD_SEPARATOR, -1);
        String idReturn = fields[0];
        LocalDate date = LocalDate.parse(fields[1]);
        long saleId = Long.parseLong(fields[2]);
        String reason = fields[4];
        double amount = Double.parseDouble(fields[5]);

        Sale sale = findSale(saleId);
        if (sale == null) {
            throw new RuntimeException("Failed to resolve sale " + saleId + " for return " + idReturn);
        }
        List<Product> products = new ArrayList<>();
        for (String productId : fields[3].split("\\|")) {
            Product product = findProduct(productId);
            if (product == null) {
                throw new RuntimeException("Failed to resolve product " + productId + " for return " + idReturn);
            }
            products.add(product);
        }
        Return loaded = new Return(idReturn, date, sale, products, reason, amount);
        if (fields.length > 6 && !fields[6].isBlank()) {
            loaded.setWarrantyRefund(Double.parseDouble(fields[6]));
        }
        return loaded;
    }

    private Sale findSale(long saleId) {
        for (Sale sale : saleService.viewAllSales()) {
            if (sale.getuId() == saleId) {
                return sale;
            }
        }
        return null;
    }

    private Product findProduct(String productId) {
        for (Product product : productService.listProducts()) {
            if (product.getProductId().equals(productId)) {
                return product;
            }
        }
        return accessoryService.findById(productId);
    }
}
