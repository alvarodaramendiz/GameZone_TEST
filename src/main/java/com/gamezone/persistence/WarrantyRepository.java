package com.gamezone.persistence;

import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Warranty;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
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
 * start date and its duration. Only the identifiers of the sale and the
 * product are stored and loaded; resolving them into objects is the job of
 * WarrantyService, so this class does not depend on any service.
 *
 * @author jahdiel
 */
public class WarrantyRepository {

    private static final String FIELD_SEPARATOR = "|";

    private static final String BASIC_TYPE = "BASIC";
    private static final String EXTENDED_TYPE = "EXTENDED";

    private final String filePath;

    /**
     * Creates a repository that works with the default warranties file.
     */
    public WarrantyRepository() {
        this(PlainArchives.WARRANTIES);
    }

    /**
     * Creates a repository that works with the given file.
     *
     * @param filePath path of the file where the warranties are stored
     */
    public WarrantyRepository(String filePath) {
        this.filePath = filePath;
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
     * Reads every warranty stored in the file as raw records with the
     * identifiers of its sale and product.
     *
     * @return the list of records, or an empty list if the file does not exist
     */
    public List<WarrantyRecord> loadAll() {
        List<WarrantyRecord> records = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return records;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                WarrantyRecord record = fromLine(line);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo leer el archivo de garantías.");
        }
        return records;
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
     * Reads a line of the file into a record.
     *
     * @param line the text line to parse
     * @return the record, or null if the line is not valid
     */
    private WarrantyRecord fromLine(String line) {
        String[] fields = line.split("\\|", -1);
        try {
            String type = fields[0];
            if (!BASIC_TYPE.equals(type) && !EXTENDED_TYPE.equals(type)) {
                return null;
            }
            return new WarrantyRecord(type, fields[1], fields[2], fields[3], LocalDate.parse(fields[4]));
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            System.out.println("Error: línea de garantía inválida: " + line);
            return null;
        }
    }
}
