package com.lolc.api.rest.converter;

import com.lolc.api.rest.enums.Currency;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        if (currency == null) {
            return null;
        }
        return currency.name();
    }

    @Override
    public Currency convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return Currency.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            // Handle legacy ordinal values
            if ("0".equals(dbData)) {
                return Currency.USD;
            } else if ("1".equals(dbData)) {
                return Currency.KHR;
            }
            throw e;
        }
    }
}
