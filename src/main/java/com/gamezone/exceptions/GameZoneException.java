package com.gamezone.exceptions;

/**
 * Root of the custom exception hierarchy of GameZone Unicesar. Every error
 * of the system is represented by a subclass of this exception.
 *
 * It extends RuntimeException (unchecked), so services and repositories do
 * not need to declare it in their signatures.
 *
 * @author jahdiel
 */
public abstract class GameZoneException extends RuntimeException {

    private String errorCode;

    /**
     * Creates the exception with a message.
     *
     * @param message description of the error
     */
    public GameZoneException(String message) {
        super(message);
    }

    /**
     * Creates the exception with a message and an error code.
     *
     * @param message   description of the error
     * @param errorCode short code that identifies the type of error
     */
    public GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Returns the error code of the exception.
     *
     * @return the error code, or null if none was given
     */
    public String getErrorCode() {
        return errorCode;
    }
}
