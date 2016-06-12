package com.jojos.challenge.iban.country;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of {@link CountryFormatProvider} interface providing a limited set
 * of {@link CountryFormat}s.
 *
 * It supports only the following countries:
 * Austria, Germany, Netherlands
 *
 * Created by karanikasg@gmail.com.
 */
public class CountryFormatProviderLimitedImpl implements CountryFormatProvider {
    private static final Logger log = LoggerFactory.getLogger(CountryFormatProviderLimitedImpl.class);

    private final Map<CountryISO, CountryFormat> countries;

    public CountryFormatProviderLimitedImpl() {
        countries = new HashMap<>();
        countries.put(CountryISO.AT, CountryFormat.Austria);
        countries.put(CountryISO.DE, CountryFormat.Germany);
        countries.put(CountryISO.NL, CountryFormat.Netherlands);
        log.info(String.format("Provider for %d countries.", countries.size()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<CountryISO, CountryFormat> getSupportedCountries() {
        return countries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCountrySupported(CountryISO countryISO) {
        return getSupportedCountries().containsKey(countryISO);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CountryFormat getCountryFormat(CountryISO countryISO) {
        return getSupportedCountries().get(countryISO);
    }
}
