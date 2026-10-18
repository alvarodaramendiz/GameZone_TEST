package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the business operations of the accessory module: registration of
 * controllers, cables and memories, inventory queries, compatibility queries
 * and stock updates. Every change is persisted through the
 * {@link AccessoryRepository}.
 *
 * @author jahdiel
 */
public class AccessoryService {

    private final AccessoryRepository repository;
    private final List<Accessory> accessories;

    /**
     * Creates the service and loads the stored accessories into memory.
     *
     * @param repository the repository used to persist and load accessories
     */
    public AccessoryService(AccessoryRepository repository) {
        this.repository = repository;
        this.accessories = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registers a new controller.
     *
     * @param productId          unique identifier of the accessory
     * @param title              name of the controller
     * @param price              unit price
     * @param stock              initial units in inventory
     * @param connectionType     connection type (wired or wireless)
     * @param compatibleConsoles identifiers of the compatible consoles, may be empty
     * @return the registered controller
     * @throws IllegalArgumentException if any value is not valid
     */
    public Controller registerController(String productId, String title, double price, int stock,
            String connectionType, List<String> compatibleConsoles) {
        validateCommonData(productId, title, price, stock);
        requireText(connectionType, "El tipo de conexión es obligatorio.");
        Controller controller = new Controller(productId, title, price, stock, connectionType);
        addCompatibility(controller, compatibleConsoles);
        save(controller);
        return controller;
    }

    /**
     * Registers a new cable.
     *
     * @param productId          unique identifier of the accessory
     * @param title              name of the cable
     * @param price              unit price
     * @param stock              initial units in inventory
     * @param lengthInMeters     length of the cable in meters
     * @param connectorType      connector type (HDMI, USB, optical, among others)
     * @param compatibleConsoles identifiers of the compatible consoles, may be empty
     * @return the registered cable
     * @throws IllegalArgumentException if any value is not valid
     */
    public Cable registerCable(String productId, String title, double price, int stock,
            double lengthInMeters, String connectorType, List<String> compatibleConsoles) {
        validateCommonData(productId, title, price, stock);
        if (lengthInMeters <= 0) {
            throw new IllegalArgumentException("La longitud del cable debe ser mayor que cero.");
        }
        requireText(connectorType, "El tipo de conector es obligatorio.");
        Cable cable = new Cable(productId, title, price, stock, lengthInMeters, connectorType);
        addCompatibility(cable, compatibleConsoles);
        save(cable);
        return cable;
    }

    /**
     * Registers a new memory.
     *
     * @param productId           unique identifier of the accessory
     * @param title               name of the memory
     * @param price               unit price
     * @param stock               initial units in inventory
     * @param capacityInGigabytes storage capacity in gigabytes
     * @param memoryType          memory type (SD, microSD, internal card)
     * @param compatibleConsoles  identifiers of the compatible consoles, may be empty
     * @return the registered memory
     * @throws IllegalArgumentException if any value is not valid
     */
    public Memory registerMemory(String productId, String title, double price, int stock,
            int capacityInGigabytes, String memoryType, List<String> compatibleConsoles) {
        validateCommonData(productId, title, price, stock);
        if (capacityInGigabytes <= 0) {
            throw new IllegalArgumentException("La capacidad de la memoria debe ser mayor que cero.");
        }
        requireText(memoryType, "El tipo de memoria es obligatorio.");
        Memory memory = new Memory(productId, title, price, stock, capacityInGigabytes, memoryType);
        addCompatibility(memory, compatibleConsoles);
        save(memory);
        return memory;
    }

    /**
     * Returns every registered accessory.
     *
     * @return an unmodifiable list with all the accessories
     */
    public List<Accessory> listAllAccessories() {
        return Collections.unmodifiableList(accessories);
    }

    /**
     * Returns the accessories of the given type.
     *
     * @param type the type to filter by: "CONTROLLER", "CABLE" or "MEMORY"
     *             (case insensitive)
     * @return the accessories of that type
     * @throws IllegalArgumentException if the type is not one of the three allowed
     */
    public List<Accessory> listAccessoriesByType(String type) {
        Class<? extends Accessory> accessoryClass = resolveType(type);
        List<Accessory> result = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (accessoryClass.isInstance(accessory)) {
                result.add(accessory);
            }
        }
        return result;
    }

    /**
     * Returns the accessories that are compatible with the given console.
     *
     * @param consoleId identifier of the console
     * @return the compatible accessories, empty if there are none
     */
    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
        List<Accessory> result = new ArrayList<>();
        if (consoleId == null) {
            return result;
        }
        for (Accessory accessory : accessories) {
            if (accessory.isCompatibleWith(consoleId.trim())) {
                result.add(accessory);
            }
        }
        return result;
    }

    /**
     * Searches an accessory by its identifier.
     *
     * @param id identifier of the accessory
     * @return the accessory, or null if it does not exist
     */
    public Accessory findById(String id) {
        if (id == null) {
            return null;
        }
        for (Accessory accessory : accessories) {
            if (accessory.getProductId().equals(id.trim())) {
                return accessory;
            }
        }
        return null;
    }

    /**
     * Checks whether an accessory has enough units to sell the requested quantity.
     *
     * @param accessoryId identifier of the accessory
     * @param quantity    requested quantity
     * @return true if the accessory exists and has enough stock
     */
    public boolean hasEnoughStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        return accessory != null && accessory.getStock() >= quantity;
    }

    /**
     * Modifies the stock of an accessory and persists the change. A positive
     * quantity adds units and a negative one removes them.
     *
     * @param accessoryId identifier of the accessory
     * @param quantity    units to add (positive) or remove (negative)
     * @throws IllegalArgumentException if the accessory does not exist or the
     *                                  resulting stock would be negative
     */
    public void updateStock(String accessoryId, int quantity) {
        Accessory accessory = findById(accessoryId);
        if (accessory == null) {
            throw new IllegalArgumentException("Accesorio no encontrado: " + accessoryId);
        }
        int newStock = accessory.getStock() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException("Stock insuficiente para el accesorio: " + accessory.getTitle());
        }
        accessory.setStock(newStock);
        repository.saveAll(accessories);
    }

    /**
     * Validates the data shared by every accessory.
     *
     * @param productId identifier of the accessory
     * @param title     name of the accessory
     * @param price     unit price
     * @param stock     units in inventory
     * @throws IllegalArgumentException if any value is not valid
     */
    private void validateCommonData(String productId, String title, double price, int stock) {
        requireText(productId, "El identificador del accesorio es obligatorio.");
        requireText(title, "El nombre del accesorio es obligatorio.");
        if (findById(productId) != null) {
            throw new IllegalArgumentException("Ya existe un accesorio con el identificador " + productId + ".");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor que cero.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo.");
        }
    }

    /**
     * Throws an exception with the given message when the text is null or blank.
     *
     * @param value   the text to check
     * @param message the message of the exception
     */
    private void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Registers the compatible consoles of an accessory, ignoring blank values.
     *
     * @param accessory          the accessory to update
     * @param compatibleConsoles identifiers of the compatible consoles, may be null
     */
    private void addCompatibility(Accessory accessory, List<String> compatibleConsoles) {
        if (compatibleConsoles == null) {
            return;
        }
        for (String consoleId : compatibleConsoles) {
            if (consoleId != null && !consoleId.isBlank()) {
                accessory.addCompatibleConsole(consoleId.trim());
            }
        }
    }

    /**
     * Adds an accessory to the inventory and persists the whole list.
     *
     * @param accessory the accessory to store
     */
    private void save(Accessory accessory) {
        accessories.add(accessory);
        repository.saveAll(accessories);
    }

    /**
     * Translates a type name into its accessory class.
     *
     * @param type the type name
     * @return the class that represents the type
     * @throws IllegalArgumentException if the type is not valid
     */
    private Class<? extends Accessory> resolveType(String type) {
        if (type != null) {
            switch (type.trim().toUpperCase()) {
                case "CONTROLLER":
                    return Controller.class;
                case "CABLE":
                    return Cable.class;
                case "MEMORY":
                    return Memory.class;
                default:
                    break;
            }
        }
        throw new IllegalArgumentException("Tipo de accesorio inválido: " + type
            + ". Use CONTROLLER, CABLE o MEMORY.");
    }
}
