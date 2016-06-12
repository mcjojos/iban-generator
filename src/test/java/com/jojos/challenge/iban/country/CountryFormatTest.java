package com.jojos.challenge.iban.country;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link CountryFormat}
 *
 * Created by karanikasg@gmail.com.
 */
public class CountryFormatTest {

    @Test
    public void testValueOf() {
        for (CountryFormat countryFormat : CountryFormat.values()) {
            CountryFormat newCountryFormat = CountryFormat.valueOf(countryFormat.getCountryISO());
            Assert.assertNotNull(newCountryFormat);
            Assert.assertSame(countryFormat, newCountryFormat);
        }
        CountryFormat newCountryFormat = CountryFormat.valueOf(CountryISO.AF);
        Assert.assertNull(newCountryFormat);
        newCountryFormat = CountryFormat.valueOf(CountryISO.ZW);
        Assert.assertNull(newCountryFormat);
    }
}