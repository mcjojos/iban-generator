package com.jojos.challenge.iban.country;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class encapsulating the definition of BBAN format instances.
 *
 * For instance Greece BBAn format consists of the following:
 * 27 characters in total out of which 3 numeric characters for the national bank code,
 * 4 numeric characters for the branch code and 16 mixed case alphanumeric characters for the account number.
 *
 * 7n,16c
 * GRkk bbbs sssc cccc cccc cccc ccc
 * 	b = National bank code
 * 	s = Branch code
 * 	c = Account number
 *
 * Several static classes help with the definition of alla the encapsulated fields.
 *
 * A {@link BBANFormat} consists of a collection of
 * {@link BBANFormat.BBANFormatElement} objects
 *
 * Each {@link BBANFormat.BBANFormatElement} object defines the number of characters,
 * the class of characters (numeric, mixed etc)
 *
 * {@see CharType} and the sub-type (branch code, account code etc.)
 *
 * @author karanikasg@gmail.com.
 */
public class BBANFormat {

    private final List<BBANFormatElement> bbanFormatElements;

    public BBANFormat(List<BBANFormatElement> bbanFormatElements) {
        this.bbanFormatElements = bbanFormatElements;
    }

    public List<BBANFormatElement> getBbanFormatElements() {
        return bbanFormatElements;
    }

    /**
     * The total number of characters. The first 4 characters, namely the ones representing
     * the country code and the check digits are not included in this count.
     *
     * @see {@link CountryFormat#getNumberOfChars()} if you want to return the IBAN's length.
     *
     * @return the total number of characters.
     */
    public int getNumberOfCharacters() {
        AtomicInteger sum = new AtomicInteger();
        bbanFormatElements.forEach(bbanFormatElement -> sum.addAndGet(bbanFormatElement.numberOfChars));
        return sum.get();
    }

    /**
     * Each {@link BBANFormat.BBANFormatElement} object defines the number of characters,
     * the class of characters (numeric, mixed etc) {@see CharType} and the sub-type (branch code, account code etc.)     *
     */

    public final static class BBANFormatElement {
		private final int numberOfChars;
		private final CharType charType;
		private final BBANSubType bbanSubType;

		private BBANFormatElement(int numberOfChars, CharType charType, BBANSubType bbanSubType) {
			this.numberOfChars = numberOfChars;
			this.charType = charType;
            this.bbanSubType = bbanSubType;
		}

        public static BBANFormatElement of(int numberOfChars, CharType charType, BBANSubType bbanSubType) {
            return new BBANFormatElement(numberOfChars, charType, bbanSubType);
        }

        public int getNumberOfChars() {
            return numberOfChars;
        }

        public CharType getCharType() {
            return charType;
        }

    }

    /**
     * Character types.
     */
	public enum CharType {
        /**
         * upper case alpha characters (A–Z)
         */
		A,
        /**
         * numeric characters (0–9)
         */
		N,
        /**
         * mixed case alphanumeric characters (a–z, A–Z, 0–9)
         */
		C,
        /**
         * Some countries like MAURITIUS include this special type.
         */
        ZEROES
	}

    /**
     * Each BBAN consists of several sub types like Bank Code, account number etc.
     *
     * @see <a href="https://en.wikipedia.org/wiki/International_Bank_Account_Number#IBAN_formats_by_country">IBAN formats by country</a>
     *
     * TODO expand this enumeration to include all possible sub types
     */
    public enum BBANSubType {
        BANK_CODE,
        BRANCH_CODE,
        NATIONAL_CHECK_DIGIT,
        ACCOUNT_TYPE,
        ACCOUNT_NUMBER,
        NATIONAL_CHECK_DIGITS,
        CURRENCY,
        ZEROES,
        NATIONAL_IDENTIFICATION_NUMBER
    }

}
