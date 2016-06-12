package com.jojos.challenge.iban.country;

import java.util.ArrayList;
import java.util.List;

import static com.jojos.challenge.iban.country.BBANFormat.BBANFormatElement;

/**
 * A builder for the {@link BBANFormat} class.
 */
public class BBANFormatBuilder {
    private List<BBANFormatElement> bbanFormatElements = new ArrayList<>();

    public BBANFormatBuilder withElement(BBANFormatElement bbanFormatElement) {
        this.bbanFormatElements.add(bbanFormatElement);
        return this;
    }

    public BBANFormatBuilder setBbanFormatElements(List<BBANFormatElement> bbanFormatElements) {
        this.bbanFormatElements = bbanFormatElements;
        return this;
    }

    public BBANFormat createBBANFormat() {
        return new BBANFormat(bbanFormatElements);
    }
}