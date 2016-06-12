package com.jojos.challenge.iban.format;

import com.jojos.challenge.iban.api.Iban;
import com.jojos.challenge.iban.util.SystemHelper;

/**
 * A default implementation of {@link IbanFormatter}
 *
 * Created by karanikasg@gmail.com.
 */
public class DefaultIbanFormatter implements IbanFormatter {

    /**
     * This wil generate a human readable String representing an {@link Iban}.
     * The format iserts one space between every four characters.
     *
     * @param iban the iban to format
     * @return a human readable string.
     */
    @Override
    public String asHumanReadableString(Iban iban) {
        String str = asString(iban);
        return str.replaceAll("....", "$0 ").trim();
    }

    /**
     * Generates a computer parsable format.
     * This format should be used when the iban is transmitted electronically.
     *
     * @param iban the iban to format
     * @return a computer parsable format.
     */
    @Override
    public String asString(Iban iban) {
        return String.format("%s%s%s",
                iban.getCountryFormat().getCountryISO().getCode(),
                SystemHelper.toString(iban.getCheckDigits()),
                SystemHelper.toString(iban.getBban()));
    }
}
