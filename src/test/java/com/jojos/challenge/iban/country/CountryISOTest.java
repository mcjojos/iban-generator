package com.jojos.challenge.iban.country;

import org.junit.Assert;
import org.junit.Test;


/**
 * test class foe {@link CountryISO}
 * <p>
 * Created by karanikasg@gmail.com.
 */
public class CountryISOTest {

    @Test
    public void testForCaseInsensitiveCode() throws Exception {
        for (CountryISO countryISO : CountryISO.values()) {
            CountryISO newCountryISO = CountryISO.forCaseInsensitiveCode(countryISO.getCode());
            Assert.assertNotNull(newCountryISO);
            Assert.assertSame(countryISO, newCountryISO);
        }

    }
}