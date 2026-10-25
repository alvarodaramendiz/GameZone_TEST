package com.gamezone.exceptions;

/**
 * Thrown when a resource (product, customer, seller, sale) is searched by
 * its identifier and does not exist.
 *
 * @author jahdiel
 */
public class ResourceNotFoundException extends GameZoneException {

    /**
     * Creates the exception with a message in Spanish built from the type of
     * resource and the identifier that was searched.
     *
     * @param resourceType the type of resource with its article, for example "el producto"
     * @param id           the identifier that was searched
     */
    public ResourceNotFoundException(String resourceType, String id) {
        super("No se encontró " + resourceType + " con identificador " + id, "NOT_FOUND");
    }
}
