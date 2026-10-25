package com.gamezone.exceptions;

/**
 * Thrown when an error happens while reading or writing a file: corrupted
 * file, permission denied, full disk, among others.
 *
 * @author jahdiel
 */
public class PersistenceException extends GameZoneException {

    /**
     * Creates the exception with a message and the original cause, so the
     * real error is not lost.
     *
     * @param message description of the error, including the file and the operation
     * @param cause   the original exception, for example an IOException
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, "PERSISTENCE");
        initCause(cause);
    }
}
