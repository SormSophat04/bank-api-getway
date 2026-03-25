package com.lolc.api.getway.converter;

import com.lolc.api.getway.enums.TransactionStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionStatusConverter implements AttributeConverter<TransactionStatus, String> {

    @Override
    public String convertToDatabaseColumn(TransactionStatus status) {
        if (status == null) {
            return null;
        }
        return status.name();
    }

    @Override
    public TransactionStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return TransactionStatus.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            // Handle legacy ordinal values
            if ("0".equals(dbData)) {
                return TransactionStatus.SUCCESS;
            } else if ("1".equals(dbData)) {
                return TransactionStatus.PENDING;
            } else if ("2".equals(dbData)) {
                return TransactionStatus.FAILED;
            }
            throw e;
        }
    }
}
