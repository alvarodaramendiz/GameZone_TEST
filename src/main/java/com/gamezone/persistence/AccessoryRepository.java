package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utilities.PlainArchives;

/**
 * Handles the persistence of every accessory in the file
 * data/accessories.csv.
 *
 * Each line starts with a discriminator that identifies the concrete type:
 * <pre>
 * CONTROLLER|productId|title|price|stock|connectionType|compatibleConsoleIds
 * CABLE|productId|title|price|stock|lengthInMeters|connectorType|compatibleConsoleIds
 * MEMORY|productId|title|price|stock|capacityInGigabytes|memoryType|compatibleConsoleIds
 * </pre>
 * The compatible console identifiers are separated by semicolons and the
 * field is left empty when the accessory has no registered compatibility.
 *
 * @author jahdiel
 */
public class AccessoryRepository {

    private static final String FIELD_SEPARATOR = "|";
    private static final String CONSOLE_SEPARATOR = ";";

    private static final String CONTROLLER_TYPE = "CONTROLLER";
    private static final String CABLE_TYPE = "CABLE";
    private static final String MEMORY_TYPE = "MEMORY";

    private final String filePath;

    /**
     * Creates a repository that works with the default accessories file.
     */
    public AccessoryRepository() {
        this(PlainArchives.ACCESSORIES);
    }

    /**
     * Creates a repository that works with the given file.
     *
     * @param filePath path of the file where the accessories are stored
     */
    public AccessoryRepository(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes every accessory to the file, replacing its previous content.
     *
     * @param accessories the accessories to persist
     */
    public void saveAll(List<Accessory> accessories) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Accessory accessory : accessories) {
                writer.write(toLine(accessory));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo guardar el archivo de accesorios.");
        }
    }

    /**
     * Reads every accessory stored in the file.
     *
     * @return the list of accessories, or an empty list if the file does not exist
     */
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return accessories;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Accessory accessory = fromLine(line);
                if (accessory != null) {
                    accessories.add(accessory);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: no se pudo leer el archivo de accesorios.");
        }
        return accessories;
    }

    /**
     * Converts an accessory into a line of the file, using the discriminator
     * that corresponds to its concrete type.
     *
     * @param accessory the accessory to convert
     * @return the text line that represents the accessory
     */
    private String toLine(Accessory accessory) {
        StringBuilder line = new StringBuilder();
        if (accessory instanceof Controller controller) {
            line.append(CONTROLLER_TYPE).append(FIELD_SEPARATOR)
                .append(commonFields(accessory)).append(FIELD_SEPARATOR)
                .append(controller.getConnectionType());
        } else if (accessory instanceof Cable cable) {
            line.append(CABLE_TYPE).append(FIELD_SEPARATOR)
                .append(commonFields(accessory)).append(FIELD_SEPARATOR)
                .append(cable.getLengthInMeters()).append(FIELD_SEPARATOR)
                .append(cable.getConnectorType());
        } else if (accessory instanceof Memory memory) {
            line.append(MEMORY_TYPE).append(FIELD_SEPARATOR)
                .append(commonFields(accessory)).append(FIELD_SEPARATOR)
                .append(memory.getCapacityInGigabytes()).append(FIELD_SEPARATOR)
                .append(memory.getMemoryType());
        }
        line.append(FIELD_SEPARATOR)
            .append(String.join(CONSOLE_SEPARATOR, accessory.getCompatibleConsoleIds()));
        return line.toString();
    }

    /**
     * Builds the fields shared by every accessory: id, title, price and stock.
     *
     * @param accessory the accessory whose common fields are written
     * @return the common fields joined by the field separator
     */
    private String commonFields(Accessory accessory) {
        return accessory.getProductId() + FIELD_SEPARATOR
            + accessory.getTitle() + FIELD_SEPARATOR
            + accessory.getPrice() + FIELD_SEPARATOR
            + accessory.getStock();
    }

    /**
     * Rebuilds an accessory from a line of the file. The first field is the
     * discriminator that decides which concrete class is created.
     *
     * @param line the text line to parse
     * @return the rebuilt accessory, or null if the line is not valid
     */
    private Accessory fromLine(String line) {
        String[] fields = line.split("\\|", -1);
        try {
            String type = fields[0];
            String productId = fields[1];
            String title = fields[2];
            double price = Double.parseDouble(fields[3]);
            int stock = Integer.parseInt(fields[4]);

            Accessory accessory;
            String compatibleConsoles;
            switch (type) {
                case CONTROLLER_TYPE:
                    accessory = new Controller(productId, title, price, stock, fields[5]);
                    compatibleConsoles = fields[6];
                    break;
                case CABLE_TYPE:
                    accessory = new Cable(productId, title, price, stock,
                        Double.parseDouble(fields[5]), fields[6]);
                    compatibleConsoles = fields[7];
                    break;
                case MEMORY_TYPE:
                    accessory = new Memory(productId, title, price, stock,
                        Integer.parseInt(fields[5]), fields[6]);
                    compatibleConsoles = fields[7];
                    break;
                default:
                    return null;
            }
            for (String consoleId : compatibleConsoles.split(CONSOLE_SEPARATOR)) {
                if (!consoleId.isBlank()) {
                    accessory.addCompatibleConsole(consoleId.trim());
                }
            }
            return accessory;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.out.println("Error: línea de accesorio inválida: " + line);
            return null;
        }
    }
}
