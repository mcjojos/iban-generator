package com.jojos.challenge.iban.country;

/**
 * Classic factory producing implementations of {@link CountryFormatProvider} interface.
 *
 * Created by karanikasg@gmail.com.
 */
public class CountryFormatProviderFactory {
    private static volatile CountryFormatProvider defaultCountryFormatProvider;

    public static CountryFormatProvider getDefaultCountryFormatProvider() {
        if (defaultCountryFormatProvider == null) {
            synchronized (CountryFormatProviderFactory.class) {
                if (defaultCountryFormatProvider == null) {
                    defaultCountryFormatProvider = new CountryFormatProviderLimitedImpl();
                }
            }
        }
        return defaultCountryFormatProvider;
    }
}
