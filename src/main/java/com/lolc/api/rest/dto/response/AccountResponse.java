package com.lolc.api.rest.dto.response;

import com.lolc.api.rest.enums.Currency;

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
