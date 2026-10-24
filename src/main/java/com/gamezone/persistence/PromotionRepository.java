package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
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
 * Handles the persistence of every promotion in the file
 * data/promotions.csv.
 *
 * Each line starts with a discriminator that identifies the concrete type:
 * <pre>
 * PERCENTAGE|promotionId|name|startDate|endDate|percentage
 * CATEGORY|promotionId|name|startDate|endDate|percentage|targetCategory
 * BULK|promotionId|name|startDate|endDate|minimumQuantity|percentage
 * </pre>
 * Dates are stored in ISO format (yyyy-MM-dd).
 *
 * @author jahdiel
 */
public class PromotionRepository {

    private static final String FIELD_SEPARATOR = "|";

    private static final String PERCENTAGE_TYPE = "PERCENTAGE";
    private static final String CATEGORY_TYPE = "CATEGORY";
    private static final String BULK_TYPE = "BULK";

    private final String filePath;

    /**
     * Creates a repository that works with the default promotions file.
     */
    public PromotionRepository() {
        this(PlainArchives.PROMOTIONS);
    }

    /**
     * Creates a repository that works with the given file.
     *
     * @param filePath path of the file where the promotions are stored
     */
    public PromotionRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes every promotion to the file, replacing its previous content.
     *
     * @param promotions the promotions to persist
     */
    public void saveAll(List<Promotion> promotions) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Promotion promotion : promotions) {
                writer.write(toLine(promotion));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo guardar el archivo de promociones.");
        }
    }

    /**
     * Reads every promotion stored in the file.
     *
     * @return the list of promotions, or an empty list if the file does not exist
     */
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return promotions;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Promotion promotion = fromLine(line);
                if (promotion != null) {
                    promotions.add(promotion);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo leer el archivo de promociones.");
        }
        return promotions;
    }

    /**
     * Converts a promotion into a line of the file, using the discriminator
     * that corresponds to its concrete type.
     *
     * @param promotion the promotion to convert
     * @return the text line that represents the promotion
     */
    private String toLine(Promotion promotion) {
        StringBuilder line = new StringBuilder();
        if (promotion instanceof PercentageDiscount percentage) {
            line.append(PERCENTAGE_TYPE).append(FIELD_SEPARATOR)
                .append(commonFields(promotion)).append(FIELD_SEPARATOR)
                .append(percentage.getPercentage());
        } else if (promotion instanceof CategoryDiscount category) {
            line.append(CATEGORY_TYPE).append(FIELD_SEPARATOR)
                .append(commonFields(promotion)).append(FIELD_SEPARATOR)
                .append(category.getPercentage()).append(FIELD_SEPARATOR)
                .append(category.getTargetCategory());
        } else if (promotion instanceof BulkPurchaseDiscount bulk) {
            line.append(BULK_TYPE).append(FIELD_SEPARATOR)
                .append(commonFields(promotion)).append(FIELD_SEPARATOR)
                .append(bulk.getMinimumQuantity()).append(FIELD_SEPARATOR)
                .append(bulk.getPercentage());
        }
        return line.toString();
    }

    /**
     * Builds the fields shared by every promotion: id, name, start date and end date.
     *
     * @param promotion the promotion whose common fields are written
     * @return the common fields joined by the field separator
     */
    private String commonFields(Promotion promotion) {
        return promotion.getPromotionId() + FIELD_SEPARATOR
            + promotion.getName() + FIELD_SEPARATOR
            + promotion.getStartDate() + FIELD_SEPARATOR
            + promotion.getEndDate();
    }

    /**
     * Rebuilds a promotion from a line of the file. The first field is the
     * discriminator that decides which concrete class is created.
     *
     * @param line the text line to parse
     * @return the rebuilt promotion, or null if the line is not valid
     */
    private Promotion fromLine(String line) {
        String[] fields = line.split("\\|", -1);
        try {
            String type = fields[0];
            String promotionId = fields[1];
            String name = fields[2];
            LocalDate startDate = LocalDate.parse(fields[3]);
            LocalDate endDate = LocalDate.parse(fields[4]);

            switch (type) {
                case PERCENTAGE_TYPE:
                    return new PercentageDiscount(promotionId, name, startDate, endDate,
                        Double.parseDouble(fields[5]));
                case CATEGORY_TYPE:
                    return new CategoryDiscount(promotionId, name, startDate, endDate,
                        Double.parseDouble(fields[5]), fields[6]);
                case BULK_TYPE:
                    return new BulkPurchaseDiscount(promotionId, name, startDate, endDate,
                        Integer.parseInt(fields[5]), Double.parseDouble(fields[6]));
                default:
                    return null;
            }
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException | DateTimeParseException e) {
            System.out.println("Error: línea de promoción inválida: " + line);
            return null;
        }
    }
}
