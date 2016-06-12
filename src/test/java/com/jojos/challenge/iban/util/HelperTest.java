package com.jojos.challenge.iban.util;

import com.jojos.challenge.iban.api.IbanException;
import com.jojos.challenge.iban.country.CountryFormat;
import com.jojos.challenge.iban.country.CountryISO;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jojos.challenge.iban.country.BBANFormat.CharType;
/**
 * Test all helper functions like mod-97, generating the check digits etc
 * Created by karanikasg@gmail.com.
 */
public class HelperTest {

    private static String allCharTypes;
    private static String upperCaseAlphaChars;
    private static String numericCharacters;
    private static String allCharTypesAlphaToTwoDigits;
    private static String upperCaseAlphaCharsAlphaToTwoDigits;
    private static int length;

    @BeforeClass
    public static void setup() {
        allCharTypes = String.valueOf(Helper.ALPHANUM); // mixed, charType C
        upperCaseAlphaChars = String.valueOf(Helper.ALPHANUM, 0, 26); // upper case letters A-Z, charType A
        numericCharacters = String.valueOf(Helper.ALPHANUM, 52, 10); // digits 0-9, charType N
        upperCaseAlphaCharsAlphaToTwoDigits = "1011121314151617181920212223242526272829303132333435";
        allCharTypesAlphaToTwoDigits = upperCaseAlphaCharsAlphaToTwoDigits + upperCaseAlphaCharsAlphaToTwoDigits +
                        numericCharacters;
        length = 1_000_000;
    }

    /**
     * Some valid austrian IBAN numbers:
     *
     * AT61 1904 3002 3457 3201
     * AT02 2050 3021 0102 3600
     * AT78 4567 8123 4567 8912
     * AT80 7651 2436 5278 3471
     * AT89 5011 2034 8769 2301
     *
     */
    @Test
    public void testGenerateCheckDigitsAustria() {
        Assert.assertArrayEquals(new int[]{6, 1}, Helper.generateCheckDigits("1904300234573201".toCharArray(), CountryISO.AT));
        Assert.assertArrayEquals(new int[]{0, 2}, Helper.generateCheckDigits("2050302101023600".toCharArray(), CountryISO.AT));
        Assert.assertArrayEquals(new int[]{7, 8}, Helper.generateCheckDigits("4567812345678912".toCharArray(), CountryISO.AT));
        Assert.assertArrayEquals(new int[]{8, 0}, Helper.generateCheckDigits("7651243652783471".toCharArray(), CountryISO.AT));
        Assert.assertArrayEquals(new int[]{8, 9}, Helper.generateCheckDigits("5011203487692301".toCharArray(), CountryISO.AT));
    }

    /**
     * Some valid German IBAN numbers:
     *
     * DE12 5001 0517 0648 4898 90
     * DE89 3704 0044 0532 0130 00
     * DE59 1007 0024 0367 0040 00
     * DE24 1007 0000 0736 3335 00
     * DE03 1117 0000 1443 3335 00
     *
     */
    @Test
    public void testGenerateCheckDigitsGermany() {
        Assert.assertArrayEquals(new int[]{1, 2}, Helper.generateCheckDigits("500105170648489890".toCharArray(), CountryISO.DE));
        Assert.assertArrayEquals(new int[]{8, 9}, Helper.generateCheckDigits("370400440532013000".toCharArray(), CountryISO.DE));
        Assert.assertArrayEquals(new int[]{5, 9}, Helper.generateCheckDigits("100700240367004000".toCharArray(), CountryISO.DE));
        Assert.assertArrayEquals(new int[]{2, 4}, Helper.generateCheckDigits("100700000736333500".toCharArray(), CountryISO.DE));
        Assert.assertArrayEquals(new int[]{0, 3}, Helper.generateCheckDigits("111700001443333500".toCharArray(), CountryISO.DE));
    }

    /**
     * Some valid iban numbers:
     *
     * NL18 ABNA 0484 8698 68
     * NL39 RABO 0300 0652 64
     * NL91 ABNA 0417 1643 00
     * NL53 TRWE 6542 4601 12
     * NL55 ZOON 7109 8124 34
     *
     */
    @Test
    public void testGenerateCheckDigitsNetherlands() {
        Assert.assertArrayEquals(new int[]{1, 8}, Helper.generateCheckDigits("ABNA0484869868".toCharArray(), CountryISO.NL));
        Assert.assertArrayEquals(new int[]{3, 9}, Helper.generateCheckDigits("RABO0300065264".toCharArray(), CountryISO.NL));
        Assert.assertArrayEquals(new int[]{9, 1}, Helper.generateCheckDigits("ABNA0417164300".toCharArray(), CountryISO.NL));
        Assert.assertArrayEquals(new int[]{5, 3}, Helper.generateCheckDigits("TRWE6542460112".toCharArray(), CountryISO.NL));
        Assert.assertArrayEquals(new int[]{5, 5}, Helper.generateCheckDigits("ZOON7109812434".toCharArray(), CountryISO.NL));
    }

    @Test
    public void testMod97() {
        // check some online big numbers calculators
        // one of them can be found here
        // http://www.javascripter.net/math/calculators/100digitbigintcalculator.htm
        // 99999999 mod 97 = 80
        // 987654321987654272 mod 97 = 31
        // 987654321987654321987654321987654321987654321987654321987654321 mod 97 = 64
        // 987654321987654321987654321987654321987654321987654321 mod 97 = 1
        // 987654321987654321987654321987654321987654321 mod 97 = 2
        // 987654321987654321987654321987654321 mod 97 = 22
        // 987654321987654321987654321 mod 97 = 34
        // 987654321987654321 mod 97 = 80
        // 987654321 mod 97 = 30
        // 11111111111111110656 mod 97 = 38
        // 111111111111111111111111111111 mod 97 = 74
        // 1 mod 97 = 1
        for (int i = 0; i < 97; i++) {
            Assert.assertEquals(i, Helper.mod97(String.valueOf(i).toCharArray()));
        }
        for (int i = 97; i < 194; i++) {
            Assert.assertEquals(i-97, Helper.mod97(String.valueOf(i).toCharArray()));
        }
        Assert.assertEquals(80, Helper.mod97(String.valueOf(99999999).toCharArray()));
        Assert.assertEquals(31, Helper.mod97("987654321987654272".toCharArray()));
        Assert.assertEquals(64, Helper.mod97("987654321987654321987654321987654321987654321987654321987654321".
                toCharArray()));
        Assert.assertEquals(1, Helper.mod97("987654321987654321987654321987654321987654321987654321".toCharArray()));
        Assert.assertEquals(2, Helper.mod97("987654321987654321987654321987654321987654321".toCharArray()));
        Assert.assertEquals(22, Helper.mod97("987654321987654321987654321987654321".toCharArray()));
        Assert.assertEquals(34, Helper.mod97("987654321987654321987654321".toCharArray()));
        Assert.assertEquals(80, Helper.mod97("987654321987654321".toCharArray()));
        Assert.assertEquals(30, Helper.mod97("987654321".toCharArray()));
        Assert.assertEquals(38, Helper.mod97("11111111111111110656".toCharArray()));
        Assert.assertEquals(74, Helper.mod97("111111111111111111111111111111".toCharArray()));
        Assert.assertEquals(83, Helper.mod97("555555555".toCharArray()));
        Assert.assertEquals(82, Helper.mod97("555555554".toCharArray()));
        Assert.assertEquals(92, Helper.mod97("555555555555555555".toCharArray()));
        Assert.assertEquals(6, Helper.mod97("333333333333333333333333333".toCharArray()));
    }

    @Test
    public void testInvertZeroPadAndReplaceLettersWithDigitsAustria() {
        char[] austria = Helper.invertZeroPadAndReplaceLettersWithDigits(allCharTypes.toCharArray(), CountryISO.AT);
        String at = "1029"; // A corresponds to 10 and T to 29
        Assert.assertEquals(allCharTypesAlphaToTwoDigits + at + "00", String.valueOf(austria));
    }

    @Test
    public void testInvertZeroPadAndReplaceLettersWithDigitsGermany() {
        char[] germany = Helper.invertZeroPadAndReplaceLettersWithDigits(
                upperCaseAlphaChars.toCharArray(), CountryISO.DE);
        String de = "1314"; // D corresponds to 13 and D to 14
        Assert.assertEquals(upperCaseAlphaCharsAlphaToTwoDigits + de + "00", String.valueOf(germany));
    }

    @Test
    public void testInvertZeroPadAndReplaceLettersWithDigitsNetherlands() {
        char[] netherlands = Helper.invertZeroPadAndReplaceLettersWithDigits(
                numericCharacters.toCharArray(), CountryISO.NL);
        String nl = "2321"; // N corresponds to 23 and L to 21
        Assert.assertEquals(numericCharacters + nl + "00", String.valueOf(netherlands));
    }

    @Test (expected = IbanException.class)
    public void testInvertZeroPadAndReplaceLettersWithDigitsThrowIllegalCharacterFound() {
        Helper.invertZeroPadAndReplaceLettersWithDigits("()*&^%$#@!".toCharArray(), CountryISO.DE);
    }


    @Test
    public void testEnsureCorrectIbanLengthOrThrowAustria() {
        Helper.ensureCorrectIbanLengthOrThrow(CountryISO.AT, CountryFormat.Austria.getNumberOfChars());
    }

    @Test
    public void testEnsureCorrectIbanLengthOrThrowGermany() {
        Helper.ensureCorrectIbanLengthOrThrow(CountryISO.DE, CountryFormat.Germany.getNumberOfChars());
    }

    @Test
    public void testEnsureCorrectIbanLengthOrThrowNetherlands() {
        Helper.ensureCorrectIbanLengthOrThrow(CountryISO.NL, CountryFormat.Netherlands.getNumberOfChars());
    }

    @Test (expected = IbanException.class)
    public void testThrowIncorrectIbanLengthNull() {
        Helper.ensureCorrectIbanLengthOrThrow(CountryISO.ZW, CountryFormat.Austria.getNumberOfChars());
    }

    @Test (expected = IbanException.class)
    public void testThrowIncorrectIbanLengthAustria() {
        Helper.ensureCorrectIbanLengthOrThrow(CountryISO.AT, CountryFormat.Austria.getNumberOfChars() + 1);
    }

    @Test (expected = IbanException.class)
    public void testThrowIncorrectIbanLengthGermany() {
        Helper.ensureCorrectIbanLengthOrThrow(CountryISO.DE, CountryFormat.Germany.getNumberOfChars() + 1);
    }

    @Test (expected = IbanException.class)
    public void testThrowIncorrectIbanLengthNetherlands() {
        Helper.ensureCorrectIbanLengthOrThrow(CountryISO.NL, CountryFormat.Netherlands.getNumberOfChars() + 1);
    }

    @Test
    public void testRandomForCountryAustria() {
        char[] austria = Helper.randomForCountry(CountryFormat.Austria);
        Assert.assertEquals(CountryFormat.Austria.getBbanFormat().getNumberOfCharacters(), austria.length);
    }

    @Test
    public void testRandomForCountryGermany() {
        char[] germany = Helper.randomForCountry(CountryFormat.Germany);
        Assert.assertEquals(CountryFormat.Germany.getBbanFormat().getNumberOfCharacters(), germany.length);
    }

    @Test
    public void testRandomForCountryNetherlands() {
        char[] netherlands = Helper.randomForCountry(CountryFormat.Netherlands);
        Assert.assertEquals(CountryFormat.Netherlands.getBbanFormat().getNumberOfCharacters(), netherlands.length);
    }

    @Test
    public void testRandomForCharTypeC() {
        // Test 1 million randomly generated mixed characters - CharType.C
        char[] randomC = Helper.randomForCharType(CharType.C, length);
        Assert.assertEquals(length, randomC.length);
        for (char c : randomC) {
            Assert.assertTrue(allCharTypes.contains(String.valueOf(c)));
        }
    }

    @Test
    public void testRandomForCharTypeA() {
        // Test 1 million randomly generated upper case letters - CharType.A
        char[] randomA = Helper.randomForCharType(CharType.A, length);
        Assert.assertEquals(length, randomA.length);
        for (char c : randomA) {
            Assert.assertTrue(upperCaseAlphaChars.contains(String.valueOf(c)));
        }
    }

    @Test
    public void testRandomForCharTypeN() {
        // Test 1 million randomly generated numeric characters - CharType.N
        char[] randomN = Helper.randomForCharType(CharType.N, length);
        Assert.assertEquals(length, randomN.length);
        for (char c : randomN) {
            Assert.assertTrue(numericCharacters.contains(String.valueOf(c)));
        }
    }
    @Test
    public void testRandomForCharTypeZEROES() {
        // Test 3 randomly generated zeroes - CharType.ZEROES
        char[] randomZ = Helper.randomForCharType(CharType.ZEROES, 3);
        Assert.assertEquals(3, randomZ.length);
        Assert.assertArrayEquals(new char[]{'0', '0', '0'}, randomZ);


    }
}