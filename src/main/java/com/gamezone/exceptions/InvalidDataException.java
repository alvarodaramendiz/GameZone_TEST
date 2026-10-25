package com.gamezone.exceptions;

/**
 * Thrown when an input value does not meet the expected format, range or
 * type: negative price, negative quantity, empty text, among others.
 *
 * @author jahdiel
 */
public class InvalidDataException extends GameZoneException {

    /**
     * Creates the exception with a message in Spanish built from the name of
     * the invalid field and the description of the problem.
     *
     * @param fieldName   the name of the invalid field
     * @param description what is wrong with the value
     */
    public InvalidDataException(String fieldName, String description) {
        super("El campo " + fieldName + " no es válido: " + description, "INVALID_DATA");
    }
}
