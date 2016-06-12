package com.jojos.challenge.iban.util;

import com.jojos.challenge.iban.api.IbanException;
import com.jojos.challenge.iban.country.BBANFormat;
import com.jojos.challenge.iban.country.CountryFormat;
import com.jojos.challenge.iban.country.CountryISO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

import static com.jojos.challenge.iban.country.BBANFormat.BBANFormatElement;
import static com.jojos.challenge.iban.country.BBANFormat.CharType;

/**
 * Utility class providing helper static methods.
 *
 * Created by karanikasg@gmail.com.
 */
public class Helper {
    private static final Logger log = LoggerFactory.getLogger(Helper.class);

    final static char[] ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private final static Random random = new Random();

    /**
     * Extract from the wikipedia:
     * "According to the ECBS "generation of the IBAN shall be the exclusive responsibility of the bank/branch
     * servicing the account".  The ECBS document replicates part of the ISO/IEC 7064:2003 standard as a method
     * for generating check digits in the range 02 to 98.
     *
     * The preferred algorithm is:
     * <ul>
     *     <li>Check that the total IBAN length is correct as per the country. If not, the IBAN is invalid</li>
     *     <li>Replace the two check digits by 00 (e.g. GB00 for the UK)</li>
     *     <li>Move the four initial characters to the end of the string</li>
     *     <li>Replace the letters in the string with digits, expanding the string as necessary,
     *          such that A or a = 10, B or b = 11, and Z or z = 35.
     *          Each alphabetic character is therefore replaced by 2 digits
     *     </li>
     *     <li>Convert the string to an integer (i.e. ignore leading zeroes)</li>
     *     <li>Calculate mod-97 of the new number, which results in the remainder</li>
     *     <li>Subtract the remainder from 98, and use the result for the two check digits.
     *          If the result is a single digit number, pad it with a leading 0 to make a two-digit number
     *     </li>
     * </ul>
     *

     * @param bbanSequence the bban sequence
     * @param countryISO the two letter country code
     * @return an int character containing the two check digits
     * @throws IbanException in case the IBAN is invalid or something went wrong with the calculations.
     */
    public static int[] generateCheckDigits(char[] bbanSequence, CountryISO countryISO) throws IbanException {
        // step one: Check that the total IBAN length is correct as per the country. If not, the IBAN is invalid
        ensureCorrectIbanLengthOrThrow(countryISO, bbanSequence.length + 4);

        char[] digitSequence = invertZeroPadAndReplaceLettersWithDigits(bbanSequence, countryISO);
        int remainder = mod97(digitSequence);

        int checkNumber = 98 - remainder;

        int[] result = new int[2];

        result[0] = checkNumber / 10;
        result[1] = checkNumber % 10;

        return result;
    }

    /**
     * Calculate mod-97 of the sequence as if it was converted, which results in the remainder.
     * Make a so called piece-wise calculation D mod 97
     * 1. Starting from the leftmost digit of D, construct a number using the first 9 digits and call it N.
     * 2. Calculate N mod 97.
     * 3. Construct a new 9-digit N by concatenating above result (step 2) with the next 7 digits of D.
     *  If there are fewer than 7 digits remaining in D but at least one, then construct a new N,
     *  which will have less than 9 digits, from the above result (step 2) followed by the remaining digits of D
     * 4. Repeat steps 2–3 until all the digits of D have been processed.
     *
     * The result of the final calculation in step 2 will be D mod 97 = N mod 97.
     *
     * @param sequence the array fir which the mod-97 shall be calculated
     * @return the mod-97 calculation of the array.
     */
    static int mod97(char[] sequence) {
        int remainingDigits = sequence.length;
        int digitsTaken = 9;
        // if the digits are less than 9 return a normal modulo
        if (remainingDigits < digitsTaken) {
            int number = Integer.parseInt(String.valueOf(sequence));
            return number % 97;
        }
        char[] N = new char[digitsTaken];
        try {
            System.arraycopy(sequence, 0, N, 0, digitsTaken);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        int fractionDigits = Integer.parseInt(String.valueOf(N));

        int remainder;

        while (true) {
            remainder = fractionDigits % 97;
            remainingDigits = remainingDigits - digitsTaken;
            if (remainingDigits > 0) {
                digitsTaken = remainingDigits > 7 ? 7 : remainingDigits;
                char[] moduloArray = String.valueOf(remainder).toCharArray();
                char[] restSequence = new char[moduloArray.length + digitsTaken];
                System.arraycopy(moduloArray, 0, restSequence, 0, moduloArray.length);
                System.arraycopy(sequence, sequence.length - remainingDigits, restSequence, moduloArray.length, digitsTaken);
                fractionDigits = Integer.parseInt(String.valueOf(restSequence));
            } else {
                break;
            }
        }

        return remainder;
    }

    /**
     * It will basically calculate steps 2-4 of the algorithm described in the beginning of the class,
     * that is
     * <ul>
     *     <li>Replace the two check digits by 00 (e.g. GB00 for the UK)</li>
     *     <li>Move the four initial characters to the end of the string</li>
     *     <li>Replace the letters in the string with digits, expanding the string as necessary,
     *          such that A or a = 10, B or b = 11, and Z or z = 35.
     *          Each alphabetic character is therefore replaced by 2 digits
     *     </li>
     * </ul>
     *
     * The only difference is that the character array passed does not contain the first 4 characters,
     * namely the country code and the check digits.
     *
     * It will Calculate the new sequence of a char array when each character is replaced by two digits
     * as if it's calling {@link Character#getNumericValue(char)}, such that A or a = 10, B or b = 11, and Z or z = 35
     * @param bbanSequence the character sequence
     * @param countryISO the country for which the sequence shall be generated
     * @return the new sequence if each letter is replaced by two digits
     * @throws IbanException if at least one illegal character is found.
     */
    static char[] invertZeroPadAndReplaceLettersWithDigits(char[] bbanSequence, CountryISO countryISO)
            throws IbanException {

        char[] sequence = new char[bbanSequence.length + 4];
        char[] countryCode = countryISO.getCode().toCharArray();

        // copy the bban sequence to the beginning of the sequence
        System.arraycopy(bbanSequence, 0, sequence, 0, bbanSequence.length);

        // the last 4 characters must be the country's code followed
        // by the two check digits which should be 0 at the first steps
        System.arraycopy(countryCode, 0, sequence, bbanSequence.length, countryCode.length);
        System.arraycopy(new char[]{'0', '0'}, 0, sequence, bbanSequence.length + countryCode.length, 2);

        // calculate the length of the new array as if we are to replace the a-z and A-Z with two digits from 10-35
        int length = sequence.length;
        for (char c : sequence) {
            int numericValue = Character.getNumericValue(c);
            if (numericValue < 0  || numericValue > 35) {
                String errorMsg = String.format("Invalid character %c found", c);
                log.error(errorMsg);
                throw new IbanException(errorMsg);
            }
            if (numericValue >= 10 && numericValue <= 35) {
                // add one to the length if the char is between a-z or A-Z
                length++;
            }
        }
        // after we have calculated the length of the new sequence
        char[] newSequence = new char[length];
        for (int i = 0, j = 0; i < sequence.length; i++, j++) {
            char c = sequence[i];
            int numericValue = Character.getNumericValue(c);
            if (numericValue >= 10 && numericValue <= 35) {
                char[] numArray = String.valueOf(numericValue).toCharArray();
                newSequence[j++] = numArray[0];
                newSequence[j] = numArray[1];
            } else {
                newSequence[j] = sequence[i];
            }
        }
        return newSequence;
    }

    /**
     * Check that the total IBAN length is correct as per the country. If not, the IBAN is invalid
     * @param countryISO the 2 letters country code
     * @param ibanLength the length of the generated IBAN
     * @throws IbanException in case the iban length is invalid or the country is not currently supported
     */
    static void ensureCorrectIbanLengthOrThrow(CountryISO countryISO, int ibanLength) throws IbanException{
        CountryFormat countryFormat = CountryFormat.valueOf(countryISO);
        if (countryFormat == null) {
            // this should normally never happen.
            String errorMsg = String.format("No country format found for %s country.", countryISO);
            log.error(errorMsg);
            throw new IbanException(errorMsg);
        }

        if (countryFormat.getNumberOfChars() != ibanLength) {
            // this should only happen when something went wrong with the previous IBAN generation
            String errorMsg = String.format("Invalid IBAN. Total IBAN length %d. Expected length %d for country %s.",
                    ibanLength, countryFormat.getNumberOfChars(), countryISO.getName());
            log.error(errorMsg);
            throw new IbanException(errorMsg);
        }
    }

    /**
     * Returns a character array containing random characters based on the individual
     * {@link CharType} of each {@link BBANFormatElement}.
     *
     * @param countryFormat the format of the country in question
     * @return a character array contains randomly generated elements according to the CharType of each sub-element.
     */
    public static char[] randomForCountry(CountryFormat countryFormat) {
        BBANFormat bbanFormat = countryFormat.getBbanFormat();

        StringBuilder sb = new StringBuilder();
        List<BBANFormatElement> formatElements = bbanFormat.getBbanFormatElements();
        for (BBANFormatElement formatElement : formatElements) {
            sb.append(SystemHelper.toString(randomForCharType(formatElement.getCharType(), formatElement.getNumberOfChars())));
        }

        return sb.toString().toCharArray();
    }

    /**
     * Generate a random sequence of char array based on the specific {@link CharType}.
     * The length of the array is controlled by the passed parameter numberOfChars.
     * @param charType the type of char, A, C or N
     * @param numberOfChars the length of the returned char array
     * @return a sequence of characters containing random characters based on the char type
     */
    public static char[] randomForCharType(CharType charType, int numberOfChars) {
        char[] chars = new char[numberOfChars];

        switch (charType) {
            case A:
                for (int i = 0; i < numberOfChars; i++) {
                    chars[i] = ALPHANUM[random.nextInt(26)];
                }
                break;
            case C:
                for (int i = 0; i < numberOfChars; i++) {
                    chars[i] = ALPHANUM[random.nextInt(ALPHANUM.length)];
                }
                break;
            case N:
                for (int i = 0; i < numberOfChars; i++) {
                    chars[i] = Character.forDigit(random.nextInt(10), 10);
                }
                break;
            case ZEROES:
                for (int i = 0; i < numberOfChars; i++) {
                    // not really random is it?
                    chars[i] = '0';
                }
        }

        return chars;
    }

}
