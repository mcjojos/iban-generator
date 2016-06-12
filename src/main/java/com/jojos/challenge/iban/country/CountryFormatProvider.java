package com.jojos.challenge.iban.country;

import java.util.Map;

/**
 * Provide BBAN formats by country. This is encapsulated inside the {@link CountryFormat}s
 *
 * Created by karanikasg@gmail.com.
 */
public interface CountryFormatProvider {
    /**
     * The supported countries in the form of a map of CountryISO codes - CountryFormats
     * @return the supported countries and country formats.
     */
    Map<CountryISO, CountryFormat> getSupportedCountries();

    /**
     * Checks if the specific country is supported
     * @param countryISO the country in question
     * @return true if it's supported false otherwise.
     */
    boolean isCountrySupported(CountryISO countryISO);

    /**
     * Gets the corresponding to the {@link CountryISO} instance {@link CountryFormat}.
     *
     * @param countryISO the country in question
     * @return The country format that corresponds to the specific country. Null of no mapping is found.
     */
    CountryFormat getCountryFormat(CountryISO countryISO);
}
