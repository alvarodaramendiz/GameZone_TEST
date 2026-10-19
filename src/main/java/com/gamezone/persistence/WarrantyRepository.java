package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.service.ProductService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import utilities.PlainArchives;

/**
 * Handles the persistence of every warranty in the file
 * data/warranties.csv.
 *
 * Each line starts with a discriminator that identifies the concrete type:
 * <pre>
 * BASIC|warrantyId|saleId|productId|startDate
 * EXTENDED|warrantyId|saleId|productId|startDate
 * </pre>
 * The end date is not stored because each warranty calculates it from its
 * start date and its duration. The sale and the product are stored by their
 * identifiers and resolved again when the file is loaded, using the injected
 * sale persistence and product service.
 *
 * @author jahdiel
 */
public class WarrantyRepository {

    private static final String FIELD_SEPARATOR = "|";

    private static final String BASIC_TYPE = "BASIC";
    private static final String EXTENDED_TYPE = "EXTENDED";

    private final String filePath;
    private final SalePersistence salePersistence;
    private final ProductService productService;

    /**
     * Creates a repository that works with the default warranties file.
     *
     * @param salePersistence used to resolve the sale of each warranty when loading
     * @param productService  used to resolve the product of each warranty when loading
     */
    public WarrantyRepository(SalePersistence salePersistence, ProductService productService) {
        this(PlainArchives.WARRANTIES, salePersistence, productService);
    }

    /**
     * Creates a repository that works with the given file.
     *
     * @param filePath        path of the file where the warranties are stored
     * @param salePersistence used to resolve the sale of each warranty when loading
     * @param productService  used to resolve the product of each warranty when loading
     */
    public WarrantyRepository(String filePath, SalePersistence salePersistence, ProductService productService) {
        this.filePath = filePath;
        this.salePersistence = salePersistence;
        this.productService = productService;
    }

    /**
     * Writes every warranty to the file, replacing its previous content.
     *
     * @param warranties the warranties to persist
     */
    public void saveAll(List<Warranty> warranties) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Warranty warranty : warranties) {
                writer.write(toLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo guardar el archivo de garantías.");
        }
    }

    /**
     * Reads every warranty stored in the file and resolves its sale and product.
     * Lines whose sale or product no longer exist are skipped.
     *
     * @return the list of warranties, or an empty list if the file does not exist
     */
    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return warranties;
        }
        Map<String, Sale> salesById = new HashMap<>();
        for (Sale sale : salePersistence.loadAll()) {
            salesById.put(String.valueOf(sale.getuId()), sale);
        }
        Map<String, Product> productsById = new HashMap<>();
        for (Product product : productService.listProducts()) {
            productsById.put(product.getProductId(), product);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Warranty warranty = fromLine(line, salesById, productsById);
                if (warranty != null) {
                    warranties.add(warranty);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo leer el archivo de garantías.");
        }
        return warranties;
    }

    /**
     * Converts a warranty into a line of the file.
     *
     * @param warranty the warranty to convert
     * @return the text line that represents the warranty
     */
    private String toLine(Warranty warranty) {
        String type = (warranty instanceof ExtendedWarranty) ? EXTENDED_TYPE : BASIC_TYPE;
        return type + FIELD_SEPARATOR
            + warranty.getWarrantyId() + FIELD_SEPARATOR
            + warranty.getSale().getuId() + FIELD_SEPARATOR
            + warranty.getProduct().getProductId() + FIELD_SEPARATOR
            + warranty.getStartDate();
    }

    /**
     * Rebuilds a warranty from a line of the file. The first field is the
     * discriminator that decides which concrete class is created.
     *
     * @param line         the text line to parse
     * @param salesById    stored sales indexed by identifier
     * @param productsById stored products indexed by identifier
     * @return the rebuilt warranty, or null if the line is not valid or its
     *         sale or product cannot be found
     */
    private Warranty fromLine(String line, Map<String, Sale> salesById, Map<String, Product> productsById) {
        String[] fields = line.split("\\|", -1);
        try {
            String type = fields[0];
            String warrantyId = fields[1];
            Sale sale = salesById.get(fields[2]);
            Product product = productsById.get(fields[3]);
            LocalDate startDate = LocalDate.parse(fields[4]);
            if (sale == null || product == null) {
                System.out.println("Aviso: garantía " + warrantyId + " sin venta o producto asociado, se omite.");
                return null;
            }
            switch (type) {
                case BASIC_TYPE:
                    return new BasicWarranty(warrantyId, product, sale, startDate);
                case EXTENDED_TYPE:
                    return new ExtendedWarranty(warrantyId, product, sale, startDate);
                default:
                    return null;
            }
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("Error: línea de garantía inválida: " + line);
            return null;
        }
    }
}
