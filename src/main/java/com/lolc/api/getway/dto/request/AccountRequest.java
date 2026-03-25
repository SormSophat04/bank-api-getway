package com.lolc.api.getway.dto.request;

import com.lolc.api.getway.enums.AccountType;
import com.lolc.api.getway.enums.Currency;
import com.lolc.api.getway.enums.Status;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AccountRequest(

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        String accountNumber,

        @NotNull
        AccountType accountType,

        @NotNull
        @DecimalMin("0")
        BigDecimal balance,

        @NotNull
        Currency currency,

        @NotNull
        Status status,

        @NotNull
        @Positive
        Long customerId
) {
}
