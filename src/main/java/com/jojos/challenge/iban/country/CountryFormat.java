package com.jojos.challenge.iban.country;

import static com.jojos.challenge.iban.country.BBANFormat.BBANFormatElement;
import static com.jojos.challenge.iban.country.BBANFormat.CharType;
import static com.jojos.challenge.iban.country.BBANFormat.BBANSubType;

/**
 * Enumeration of all IBAN formats by country
 * For the time being only Austria, Germany and the Netherlands are supported.
 *
 * TODO: enrich this class with more country format types
 *
 * Created by karanikasg@gmail.com.
 */
public enum CountryFormat {

    /**
     *  Austria - 20 chars, BBAN format: 16n,
     *  IBAN fields: ATkk bbbb bccc cccc cccc,
     *  b = National bank code c = Account number
     */
    Austria(CountryISO.AT, 20, new BBANFormatBuilder().
            withElement(BBANFormatElement.of(5, CharType.N, BBANSubType.BANK_CODE)).
            withElement(BBANFormatElement.of(11, CharType.N, BBANSubType.ACCOUNT_NUMBER)).
            createBBANFormat()),

    /**
     * Germany - 22 chars, BBAN format: 18n,
     * IBAN FIelds: DEkk bbbb bbbb cccc cccc cc,
     * b = Bank and branch identifier (de:Bankleitzahl or BLZ) c = Account number
     */
    Germany(CountryISO.DE, 22, new BBANFormatBuilder().
            withElement(BBANFormatElement.of(8, CharType.N, BBANSubType.BANK_CODE)).
            withElement(BBANFormatElement.of(10, CharType.N, BBANSubType.ACCOUNT_NUMBER)).
            createBBANFormat()),

    /**
     * Netherlands - 18 chars, BBAN Format: 4a,10n,
     * IBAN Fields: NLkk bbbb cccc cccc cc,
     * b = BIC Bank code c = Account number
     */
    Netherlands(CountryISO.NL, 18, new BBANFormatBuilder().
            withElement(BBANFormatElement.of(4, CharType.A, BBANSubType.BANK_CODE)).
            withElement(BBANFormatElement.of(10, CharType.N, BBANSubType.ACCOUNT_NUMBER)).
            createBBANFormat());

//    /**
//     * No strictly supported, only for test purposes
//     */
//    UnitedKingdom(CountryISO.GB, 22, new BBANFormatBuilder().
//            withElement(BBANFormatElement.of(4, CharType.A, BBANSubType.BANK_CODE)).
//            withElement(BBANFormatElement.of(6, CharType.N, BBANSubType.BRANCH_CODE)).
//            withElement(BBANFormatElement.of(8, CharType.N, BBANSubType.ACCOUNT_NUMBER)).
//            createBBANFormat());

    private final CountryISO countryISO;
    private final int numberOfChars;
    private final BBANFormat bbanFormat;

    CountryFormat(CountryISO countryISO, int numberOfChars, BBANFormat bbanFormat) {
        this.countryISO = countryISO;
        this.numberOfChars = numberOfChars;
        this.bbanFormat = bbanFormat;
    }

    public CountryISO getCountryISO() {
        return countryISO;
    }

    public int getNumberOfChars() {
        return numberOfChars;
    }

    public BBANFormat getBbanFormat() {
        return bbanFormat;
    }


    /**
     * Returns a new instance of this class based on the {@link CountryISO} or null if not supported
     * @param countryISO the two letter country
     * @return a country format
     */
    public static CountryFormat valueOf(CountryISO countryISO) {
        for (CountryFormat countryFormat : values()) {
            if (countryFormat.getCountryISO() == countryISO) {
                return countryFormat;
            }
        }
        return null;
    }
}
