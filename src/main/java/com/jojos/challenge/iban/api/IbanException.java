package com.jojos.challenge.iban.api;

/**
 * Application specific exception.
 * <p>
 * Created by karanikasg@gmail.com.
 */
public class IbanException extends RuntimeException {

    public IbanException(String message) {
        super(message);
    }
}
