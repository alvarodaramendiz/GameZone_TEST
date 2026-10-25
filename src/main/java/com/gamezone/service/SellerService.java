package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Seller;
import com.gamezone.model.Shift;
import com.gamezone.persistence.SellerPersistence;
import com.gamezone.validation.PersonValidator;
import java.util.ArrayList;

/**
 *
 * Service class responsible for managing seller operations. It validates
 * seller information and coordinates the interaction between the application
 * logic and the seller persistence layer.
 *
 *
 * @author EstefaniaMarquez
 */

public class SellerService {

    private SellerPersistence sellerDAO = new SellerPersistence();
    /**
     * Validates the information provided for a seller.
     *
     * @param employeeCode the unique employee code of the seller.
     * @param shift the work shift assigned to the seller.
     * @param name the name of the seller.
     * @param iD the identification number of the seller.
     * @param contactNumber the contact number of the seller.
     * @throws InvalidDataException if any of the provided information
     * is invalid.
     */
    
    public void validateSeller(long employeeCode, Shift shift, String name,
            long iD, long contactNumber) {

        if (employeeCode <= 0) {
            throw new InvalidDataException("código de empleado", "debe ser mayor que cero");
        }

        if (shift == null) {
            throw new InvalidDataException("turno", "es obligatorio");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("nombre", "no puede estar vacío");
        }

        if (name.matches(".*\\d.*")) {
            throw new InvalidDataException("nombre", "no puede contener números");
        }

        if (iD <= 0) {
            throw new InvalidDataException("identificador", "debe ser mayor que cero");
        }

        if (contactNumber <= 0) {
            throw new InvalidDataException("teléfono", "debe ser mayor que cero");
        }
    }

    /**
     * Registers a new seller after validating the provided information.
     * The seller is stored through the persistence layer if all information
     * is valid and the employee code is not already registered.
     *
     * @param employeeCode the unique employee code of the seller.
     * @param shift the work shift assigned to the seller.
     * @param name the name of the seller.
     * @param iD the identification number of the seller.
     * @param contactNumber the contact number of the seller.
     * @throws InvalidDataException if any of the provided information is invalid.
     * @throws BusinessRuleException if a seller with the given employee code
     * already exists.
     */
    
    public void registerSeller(long employeeCode, Shift shift, String name,
            long iD, long contactNumber) {

        validateSeller(employeeCode, shift, name, iD, contactNumber);

        if (searchSeller(employeeCode) != null) {
            throw new BusinessRuleException(
                    "Ya existe un vendedor con el código de empleado " + employeeCode);
        }

        Seller seller = new Seller(employeeCode, shift, name, iD, contactNumber);
        sellerDAO.saveSeller(seller);
    }

    /**
     * Updates an existing seller with the provided information.
     *
     * @param employeeCode the employee code of the seller to update.
     * @param shift the new work shift of the seller.
     * @param name the new name of the seller.
     * @param iD the new identification number of the seller.
     * @param contactNumber the new contact number of the seller.
     * @throws com.gamezone.exceptions.ResourceNotFoundException if the seller
     * does not exist.
     * @throws InvalidDataException if the provided information is invalid.
     */
    
    public void updateSeller(long employeeCode, Shift shift, String name,
            long iD, long contactNumber) {

        Seller seller = searchSeller(employeeCode);
        PersonValidator.validatePersonExists(seller, String.valueOf(employeeCode));

        validateSeller(employeeCode, shift, name, iD, contactNumber);

        seller.setShift(shift);
        seller.setName(name);
        seller.setiD(iD);
        seller.setContactNumber(contactNumber);

        sellerDAO.updateSeller(seller);
    }

    /**
     * Deletes an existing seller from the persistence layer.
     *
     * @param employeeCode the employee code of the seller to delete.
     * @throws com.gamezone.exceptions.ResourceNotFoundException if no seller
     * with the given employee code exists.
     */
    
    public void deleteSeller(long employeeCode) {

        Seller seller = searchSeller(employeeCode);
        PersonValidator.validatePersonExists(seller, String.valueOf(employeeCode));

        sellerDAO.deleteSeller(employeeCode);
    }

    /**
     * Retrieves all sellers stored in the persistence layer.
     *
     * @return an ArrayList containing all stored sellers.
     */
    
    public ArrayList<Seller> listSellers() {
        return sellerDAO.listSellers();
    }

    /**
     * Searches for a seller by its employee code.
     *
     * @param employeeCode the employee code of the seller to search for.
     * @return the seller with the specified employee code, or {@code null}
     * if no matching seller is found.
     */
    
    public Seller searchSeller(long employeeCode) {
        return sellerDAO.searchSeller(employeeCode);
    }
}
