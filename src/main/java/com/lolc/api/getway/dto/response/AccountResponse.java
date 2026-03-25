package com.lolc.api.getway.dto.response;

import com.lolc.api.getway.enums.Currency;

import java.math.BigDecimal;

public record AccountResponse(
        Long accountId,
        Long customerId,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        Currency currency
) {
}
