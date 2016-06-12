package com.jojos.challenge.iban.api;

import com.jojos.challenge.iban.country.CountryFormat;
import com.jojos.challenge.iban.country.CountryFormatProvider;
import com.jojos.challenge.iban.country.CountryFormatProviderFactory;
import com.jojos.challenge.iban.country.CountryISO;
import com.jojos.challenge.iban.util.Helper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * todo: create javadoc
 * <p>
 * Created by karanikasg@gmail.com.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ CountryFormatProviderFactory.class, Helper.class})
public class IbanApiTest {

    private static CountryFormatProvider countryFormatProvider;

    @BeforeClass
    public static void setup() {
        PowerMockito.mockStatic(CountryFormatProviderFactory.class);
        countryFormatProvider = mock(CountryFormatProvider.class);
        when(CountryFormatProviderFactory.getDefaultCountryFormatProvider()).thenReturn(countryFormatProvider);
    }

    @Test
    public void testConcurrentGenerateForAustriaSame() {
        int runs = 100_000;

        PowerMockito.mockStatic(Helper.class);
        when(countryFormatProvider.isCountrySupported(CountryISO.AT)).thenReturn(true);
        when(countryFormatProvider.getCountryFormat(CountryISO.AT)).thenReturn(CountryFormat.Austria);
        char[] bbanAustria = "1904300234573201".toCharArray();
        when(Helper.randomForCountry(CountryFormat.Austria)).thenReturn(bbanAustria);
        when(Helper.generateCheckDigits(bbanAustria, CountryISO.AT)).thenReturn(new int[]{6, 1});

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(1);

        final Set<Iban> ibans = Collections.newSetFromMap(new ConcurrentHashMap<>());
        IntStream.range(0, runs).forEachOrdered(value ->
            executorService.submit(() -> {
                try {
                    latch.await(10, TimeUnit.SECONDS);
                    Iban iban = IbanApi.generateFor(CountryISO.AT);
                    ibans.add(iban);
                } catch (InterruptedException e) {
                    Assert.fail("Concurrency test execution interrupted. Exiting.");
                }
            })
        );
        executorService.shutdown();
        latch.countDown();
        try {
            boolean terminated = executorService.awaitTermination(120, TimeUnit.SECONDS);
            if (!terminated) {
                Assert.fail("Concurrent test failed. Try increasing the timeout.");
            }
        } catch (InterruptedException e) {
            Assert.fail("Concurrent execution took way too long. Exiting");
        }
        Assert.assertEquals(1, ibans.size());
    }

    @Test
    public void testConcurrentGenerateDifferent() {
        int runs = 10_000;
        when(countryFormatProvider.isCountrySupported(CountryISO.NL)).thenReturn(true);
        when(countryFormatProvider.getCountryFormat(CountryISO.NL)).thenReturn(CountryFormat.Netherlands);
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(1);

        final Set<Iban> ibans = Collections.newSetFromMap(new ConcurrentHashMap<>());
        IntStream.range(0, runs).forEachOrdered(value ->
            executorService.submit(() -> {
                try {
                    latch.await(10, TimeUnit.SECONDS);
                    ibans.add(IbanApi.generateFor(CountryISO.NL));
                } catch (InterruptedException e) {
                    Assert.fail("Concurrency test execution interrupted. Exiting.");
                }
            })
        );
        executorService.shutdown();
        latch.countDown();
        try {
            boolean terminated = executorService.awaitTermination(180, TimeUnit.SECONDS);
            if (!terminated) {
                Assert.fail("Concurrent test failed. Try increasing the timeout.");
            }
        } catch (InterruptedException e) {
            Assert.fail("Concurrent execution was interrupted. Exiting");
        }
        Assert.assertEquals(runs, ibans.size());
    }
}