package com.lolc.api.getway.mapper;

import com.lolc.api.getway.dto.response.BillPaymentResponse;
import com.lolc.api.getway.entity.BillPayment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface BillPaymentMapper {

    @Mapping(target = "paymentId", source = "billPayment.billPaymentId")
    @Mapping(target = "billId", source = "billPayment.bill.billId")
    @Mapping(target = "transactionId", expression = "java(parseTransactionId(billPayment.getTransactionId()))")
    @Mapping(target = "paidAmount", source = "billPayment.amount")
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "referenceNumber", source = "referenceNumber")
    @Mapping(target = "paidAt", expression = "java(toOffsetDateTime(billPayment.getCreateAt()))")
    BillPaymentResponse toResponse(BillPayment billPayment, String currency, String referenceNumber);

    default Long parseTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(transactionId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    default OffsetDateTime toOffsetDateTime(LocalDateTime createdAt) {
        if (createdAt == null) {
            return null;
        }
        return createdAt.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }
}
