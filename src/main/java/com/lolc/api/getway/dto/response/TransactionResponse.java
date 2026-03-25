package com.lolc.api.getway.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.lolc.api.getway.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(
        Long transactionId,
        String type,                  // transfer, deposit, bill_payment, ...
        BigDecimal amount,
        String description,
        String status,                // SUCCESS, FAILED, PENDING
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createAt,
        String referenceNumber,
        String currency,              // USD, KHR
        AccountSnapshot fromAccountId,
        AccountSnapshot toAccountId
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record AccountSnapshot(
            Long accountId,
            String accountNumber,
            String currency,
            CustomerSnapshot customer
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record CustomerSnapshot(
            Long customerId,
            String firstName,
            String lastName,
            String phone
    ) {}
}
