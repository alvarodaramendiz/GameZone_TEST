package com.gamezone.exceptions;

/**
 * Thrown when an operation breaks a business rule: insufficient stock,
 * duplicated customer, sale without products, among others.
 *
 * @author jahdiel
 */
public class BusinessRuleException extends GameZoneException {

    /**
     * Creates the exception with the description of the broken rule.
     *
     * @param message description of the broken rule
     */
    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE");
    }
}
