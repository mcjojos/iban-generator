package com.jojos.challenge.iban.api;

import com.jojos.challenge.iban.country.CountryFormat;
import com.jojos.challenge.iban.country.CountryFormatProviderLimitedImpl;
import com.jojos.challenge.iban.country.CountryISO;
import com.jojos.challenge.iban.country.CountryFormatProvider;
import com.jojos.challenge.iban.country.CountryFormatProviderFactory;
import com.jojos.challenge.iban.format.DefaultIbanFormatter;
import com.jojos.challenge.iban.format.IbanFormatter;
import com.jojos.challenge.iban.util.Helper;
import com.jojos.challenge.iban.util.SystemHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Clients of the IBAN generator should use the methods exposed in this class.
 *
 * The IBAN generator tool guarantees uniqueness of the generated IBAN's within a specific instance of the tool.
 *
 *  * Created by karanikasg@gmail.com.
 */
public class IbanApi {
    private static final Logger log = LoggerFactory.getLogger(IbanApi.class);

    private static final IbanFormatter IBAN_FORMATTER = new DefaultIbanFormatter();
    private static final CountryFormatProvider COUNTRY_PROVIDER =
            CountryFormatProviderFactory.getDefaultCountryFormatProvider();

    private final static ConcurrentMap<CountryISO, Map<String, Iban>> ibans = new ConcurrentHashMap<>();

    /**
     * Generate a syntactically valid IBAN according to the ISO standard for the specified country.
     * @see <a href="https://en.wikipedia.org/wiki/International_Bank_Account_Number">IBAN ISO</a>
     *
     * Use the {@link DefaultIbanFormatter} and the {@link CountryFormatProviderLimitedImpl}
     * implememtations for iban output formatting and provider for BBAN formats by country.
     * {@link CountryFormatProviderLimitedImpl} only supports three countries: AT, DE and NL.
     *
     * @param country The country for which a valid IBAN is generated for
     * @return a syntactically valid IBAN
     * @throws IbanException if the country is not supported.
     */
    public static Iban generateFor(CountryISO country) throws IbanException {
        return generateFor(country, IBAN_FORMATTER, COUNTRY_PROVIDER);
    }

    /**
     * Same as {@link #generateFor(CountryISO)} with the difference that the {@link IbanFormatter}
     * and the {@link CountryFormatProvider} are explicitly declared. Clients can declare custom implementations
     * of the aforementioned classes.
     *
     * @param country The country for which a valid IBAN is generated for
     * @param ibanFormatter implementation of iban output formatter
     * @param countryFormatProvider implementation of the {@link CountryFormatProvider} interface.
     * @return a syntactically valid IBAN
     * @throws IbanException if the country is not supported.
     *
     * @see IbanFormatter
     * @see CountryFormatProvider
     */
    public static Iban generateFor(CountryISO country,
                                    IbanFormatter ibanFormatter,
                                    CountryFormatProvider countryFormatProvider) throws IbanException {
        return generateFor(country, ibanFormatter, countryFormatProvider, 0);
    }

    /**
     * Same as {@link #generateFor(CountryISO, IbanFormatter, CountryFormatProvider)} with the difference that
     * a counter is also declared.
     * The IBAN generator tool must guarantee uniqueness of the generated IBAN's within a specific instance of the tool
     * and therefore this method might be called multiple times recursively if the generated IBAN is the same.
     * Don't allow to be called more than 3 consecutive times.
     *
     * @param country The country for which a valid IBAN is generated for
     * @param ibanFormatter implementation of iban output formatter
     * @param countryFormatProvider implementation of the {@link CountryFormatProvider} interface.
     * @param counter maximum 5 times is allowed to for this method to be called recursively.
     * @return a syntactically valid IBAN or null if the tool failed to generate a unique within the specific instance
     * of the tool IBAN number.
     * @throws IbanException if the country is not supported.
     *
     * @see IbanFormatter
     * @see CountryFormatProvider
     */
    private static Iban generateFor(CountryISO country,
                                    IbanFormatter ibanFormatter,
                                    CountryFormatProvider countryFormatProvider,
                                    int counter) throws IbanException {
        if (counter > 2) {
            return null;
        }
        if (countryFormatProvider.isCountrySupported(country)) {
            CountryFormat countryFormat = countryFormatProvider.getCountryFormat(country);
            char[] bban = Helper.randomForCountry(countryFormat);
            int[] checkDigits = Helper.generateCheckDigits(bban, country);
            Iban iban = new Iban(countryFormat, checkDigits, bban, ibanFormatter);
            if (store(iban) != null) {
                log.warn(String.format("It seems that %s was generated twice. Re-generate!", iban.asHumanReadableString()));
                // at this point we know that what we stored replaced an different instance of the same Iban
                // i.e. our random generator managed to produce two identical ibans.
                // Attempt to generate it one more time. Hopefully this time we are luckier
                generateFor(country, ibanFormatter, countryFormatProvider, ++counter);
            }
            log.info(String.format("Iban generated %s", iban.asHumanReadableString()));
            return iban;
        } else {
            String errorMsg = String.format("%s country not supported.", country);
            log.error(errorMsg);
            throw new IbanException(errorMsg);
        }
    }

    private static Iban store(Iban iban) {
        // the actual point of synchronization is inside the helper function
        return SystemHelper.addToContainedMap(ibans, iban.getCountryFormat().getCountryISO(), iban.asKey(), iban);
    }
}
