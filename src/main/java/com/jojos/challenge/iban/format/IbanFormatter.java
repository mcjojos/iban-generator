package com.jojos.challenge.iban.format;

import com.jojos.challenge.iban.api.Iban;

/**
 * Generic interface defining two ways of formatting an Iban.
 * One for humans and one for computers.
 *
 * Created by karanikasg@gmail.com.
 */
public interface IbanFormatter {

    /**
     * The format that makes sense for a human being.
     * @param iban the iban to format
     * @return a string for humans
     */
    String asHumanReadableString(Iban iban);

    /**
     * Compoter parsable format
     * @param iban the iban to format
     * @return A string for computers
     */
    String asString(Iban iban);

}
