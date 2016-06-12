package com.jojos.challenge.iban.api;

import com.jojos.challenge.iban.country.CountryFormat;
import com.jojos.challenge.iban.format.IbanFormatter;
import com.jojos.challenge.iban.util.SystemHelper;

import java.util.Arrays;

/**
 * The base IBAN class consisting of three parts:
 * 1. countryFormat code using ISO 3166-1 alpha-2 - two letters,
 * 2. check digits - two digits, and
 * 3. Basic Bank Account Number (BBAN) - up to 30 alphanumeric characters that are countryFormat-specific.[1]
 *
 * Created by karanikasg@gmail.com.
 */
public class Iban {

    private final CountryFormat countryFormat;
    private final int[] checkDigits;
    private final char[] bban;
    private final IbanFormatter formatter;

    public Iban(CountryFormat countryFormat, int[] checkDigits, char[] bban, IbanFormatter formatter) {
        this.countryFormat = countryFormat;
        this.checkDigits = checkDigits;
        this.bban = bban;
        this.formatter = formatter;
    }

    public CountryFormat getCountryFormat() {
        return countryFormat;
    }

    public int[] getCheckDigits() {
        return checkDigits;
    }

    public char[] getBban() {
        return bban;
    }

    /**
     * It will utilize the underlying {@link IbanFormatter#asHumanReadableString(Iban)} result
     * @return a human readable iban
     */
    public String asHumanReadableString() {
        return formatter.asHumanReadableString(this);
    }

    /**
     * This will utilize the underlying {@link IbanFormatter#asString(Iban)} result
     * @return an iban in a computer readable format
     */
    public String asString() {
        return formatter.asString(this);
    }

    /**
     * It will not depend on the underlying IbanFormatter. It will return the string representation of the iban
     * which will be the same each time no matter which
     * @return a string to be used as a key in a map
     */
    public String asKey() {
        return String.format("%s%s%s",
                getCountryFormat().getCountryISO().getCode(),
                SystemHelper.toString(getCheckDigits()),
                SystemHelper.toString(getBban()));
    }

    @Override
    public String toString() {
        String str = asKey();
        return str.replaceAll("....", "$0 ").trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Iban iban = (Iban) o;

        if (countryFormat != iban.countryFormat) return false;
        if (!Arrays.equals(checkDigits, iban.checkDigits)) return false;
        if (!Arrays.equals(bban, iban.bban)) return false;
        return !(formatter != null ? !formatter.equals(iban.formatter) : iban.formatter != null);

    }

    @Override
    public int hashCode() {
        int result = countryFormat != null ? countryFormat.hashCode() : 0;
        result = 31 * result + (checkDigits != null ? Arrays.hashCode(checkDigits) : 0);
        result = 31 * result + (bban != null ? Arrays.hashCode(bban) : 0);
        result = 31 * result + (formatter != null ? formatter.hashCode() : 0);
        return result;
    }
}
