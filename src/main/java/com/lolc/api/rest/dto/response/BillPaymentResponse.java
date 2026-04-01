package com.lolc.api.rest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lolc.api.rest.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record BillPaymentResponse(
        Long paymentId,
        Long billId,
        Long transactionId,
        String receiptNo,
        PaymentStatus status,
        BigDecimal paidAmount,
        String currency,
        String referenceNumber,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime paidAt
) {}
