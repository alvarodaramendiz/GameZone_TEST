package com.gamezone.service;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.persistence.PromotionRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides the business operations of the promotion module: registration of
 * the three promotion types, listing of registered and active promotions,
 * and selection of the promotion that gives the highest discount to a sale.
 * Every change is persisted through the {@link PromotionRepository}.
 *
 * @author jahdiel
 */
public class PromotionService {

    private final PromotionRepository repository;
    private final List<Promotion> promotions;

    /**
     * Creates the service and loads the stored promotions into memory.
     *
     * @param repository the repository used to persist and load promotions
     */
    public PromotionService(PromotionRepository repository) {
        this.repository = repository;
        this.promotions = new ArrayList<>(repository.loadAll());
    }

    /**
     * Registers a new promotion that applies a percentage to the whole sale.
     *
     * @param promotionId unique identifier of the promotion
     * @param name        name shown on the receipt
     * @param startDate   first day the promotion is active
     * @param endDate     last day the promotion is active
     * @param percentage  discount percentage, greater than 0 and up to 100
     * @return the registered promotion
     * @throws IllegalArgumentException if any value is not valid
     */
    public PercentageDiscount registerPercentageDiscount(String promotionId, String name,
            LocalDate startDate, LocalDate endDate, double percentage) {
        validateCommonData(promotionId, name, startDate, endDate);
        validatePercentage(percentage);
        PercentageDiscount promotion = new PercentageDiscount(promotionId, name, startDate, endDate, percentage);
        save(promotion);
        return promotion;
    }

    /**
     * Registers a new promotion that applies a percentage only to the
     * products of one category.
     *
     * @param promotionId    unique identifier of the promotion
     * @param name           name shown on the receipt
     * @param startDate      first day the promotion is active
     * @param endDate        last day the promotion is active
     * @param percentage     discount percentage, greater than 0 and up to 100
     * @param targetCategory category that receives the discount: "VIDEOGAME", "CONSOLE" or "ACCESSORY"
     * @return the registered promotion
     * @throws IllegalArgumentException if any value is not valid
     */
    public CategoryDiscount registerCategoryDiscount(String promotionId, String name,
            LocalDate startDate, LocalDate endDate, double percentage, String targetCategory) {
        validateCommonData(promotionId, name, startDate, endDate);
        validatePercentage(percentage);
        String category = targetCategory == null ? "" : targetCategory.trim().toUpperCase();
        if (!category.equals("VIDEOGAME") && !category.equals("CONSOLE") && !category.equals("ACCESSORY")) {
            throw new IllegalArgumentException("Categoría inválida: " + targetCategory
                + ". Use VIDEOGAME, CONSOLE o ACCESSORY.");
        }
        CategoryDiscount promotion = new CategoryDiscount(promotionId, name, startDate, endDate,
            percentage, category);
        save(promotion);
        return promotion;
    }

    /**
     * Registers a new promotion that applies a percentage to the whole sale
     * when it includes a minimum number of units.
     *
     * @param promotionId     unique identifier of the promotion
     * @param name            name shown on the receipt
     * @param startDate       first day the promotion is active
     * @param endDate         last day the promotion is active
     * @param minimumQuantity minimum number of units the sale must include
     * @param percentage      discount percentage, greater than 0 and up to 100
     * @return the registered promotion
     * @throws IllegalArgumentException if any value is not valid
     */
    public BulkPurchaseDiscount registerBulkPurchaseDiscount(String promotionId, String name,
            LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage) {
        validateCommonData(promotionId, name, startDate, endDate);
        validatePercentage(percentage);
        if (minimumQuantity < 1) {
            throw new IllegalArgumentException("La cantidad mínima debe ser al menos 1.");
        }
        BulkPurchaseDiscount promotion = new BulkPurchaseDiscount(promotionId, name, startDate, endDate,
            minimumQuantity, percentage);
        save(promotion);
        return promotion;
    }

    /**
     * Returns every registered promotion, including expired and future ones.
     *
     * @return an unmodifiable list with all the promotions
     */
    public List<Promotion> listAllPromotions() {
        return Collections.unmodifiableList(promotions);
    }

    /**
     * Returns the promotions that are active on the current date.
     *
     * @return the active promotions
     */
    public List<Promotion> listActivePromotions() {
        return listActivePromotions(LocalDate.now());
    }

    /**
     * Among the active promotions, returns the one that gives the highest
     * discount to the given sale. The sale date is used to decide which
     * promotions are active; if the sale has no date, the current date is used.
     *
     * @param sale the sale to evaluate
     * @return the best promotion, or null if no promotion applies or the
     *         highest discount is zero
     */
    public Promotion findBestPromotionFor(Sale sale) {
        if (sale == null) {
            return null;
        }
        LocalDate date = sale.getDate() != null ? sale.getDate() : LocalDate.now();
        Promotion best = null;
        double bestDiscount = 0;
        for (Promotion promotion : listActivePromotions(date)) {
            double discount = promotion.calculateDiscount(sale);
            if (discount > bestDiscount) {
                bestDiscount = discount;
                best = promotion;
            }
        }
        return best;
    }

    /**
     * Searches a promotion by its identifier.
     *
     * @param id identifier of the promotion
     * @return the promotion, or null if it does not exist
     */
    public Promotion findById(String id) {
        if (id == null) {
            return null;
        }
        for (Promotion promotion : promotions) {
            if (promotion.getPromotionId().equals(id.trim())) {
                return promotion;
            }
        }
        return null;
    }

    /**
     * Returns the promotions that are active on the given date.
     *
     * @param date the date to check
     * @return the active promotions on that date
     */
    private List<Promotion> listActivePromotions(LocalDate date) {
        List<Promotion> result = new ArrayList<>();
        for (Promotion promotion : promotions) {
            if (promotion.isActive(date)) {
                result.add(promotion);
            }
        }
        return result;
    }

    /**
     * Validates the data shared by every promotion.
     *
     * @param promotionId identifier of the promotion
     * @param name        name of the promotion
     * @param startDate   first day the promotion is active
     * @param endDate     last day the promotion is active
     * @throws IllegalArgumentException if any value is not valid
     */
    private void validateCommonData(String promotionId, String name, LocalDate startDate, LocalDate endDate) {
        if (promotionId == null || promotionId.isBlank()) {
            throw new IllegalArgumentException("El identificador de la promoción es obligatorio.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la promoción es obligatorio.");
        }
        if (findById(promotionId) != null) {
            throw new IllegalArgumentException("Ya existe una promoción con el identificador " + promotionId + ".");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }

    /**
     * Validates that a discount percentage is greater than 0 and up to 100.
     *
     * @param percentage the percentage to check
     * @throws IllegalArgumentException if the percentage is out of range
     */
    private void validatePercentage(double percentage) {
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("El porcentaje debe ser mayor que 0 y máximo 100.");
        }
    }

    /**
     * Adds a promotion to the list and persists the whole list.
     *
     * @param promotion the promotion to store
     */
    private void save(Promotion promotion) {
        promotions.add(promotion);
        repository.saveAll(promotions);
    }
}
