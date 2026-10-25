package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;
import java.util.List;

/**
 * Validation rules of the person module (customers and sellers). All methods
 * are static and throw a custom exception when a rule is not met.
 *
 * @author jahdiel
 */
public class PersonValidator {

    /** Utility class: it is not meant to be instantiated. */
    private PersonValidator() {
    }

    /**
     * Validates the basic data of a person.
     *
     * @param id    identifier of the person
     * @param name  name of the person
     * @param email email of the person
     * @param phone contact number of the person
     * @throws InvalidDataException if a required field is empty or the email
     *                              does not contain "@" and "."
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("identificador", "no puede estar vacío");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("nombre", "no puede estar vacío");
        }
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new InvalidDataException("correo", "debe contener @ y un punto");
        }
        if (phone == null || phone.isBlank()) {
            throw new InvalidDataException("teléfono", "no puede estar vacío");
        }
    }

    /**
     * Validates that a searched person exists.
     *
     * @param person   the person found, may be null
     * @param personId the identifier that was searched
     * @throws ResourceNotFoundException if the person is null
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("la persona", personId);
        }
    }

    /**
     * Validates that no other person has the same identifier.
     *
     * @param existing the registered people
     * @param newId    the identifier of the new person
     * @throws BusinessRuleException if a person with that identifier already exists
     */
    public static void validateUniquePerson(List<? extends Person> existing, String newId) {
        for (Person person : existing) {
            if (String.valueOf(person.getiD()).equals(newId)) {
                throw new BusinessRuleException("Ya existe una persona con el identificador " + newId);
            }
        }
    }
}
