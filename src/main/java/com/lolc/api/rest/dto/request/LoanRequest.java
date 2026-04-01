package com.lolc.api.rest.dto.request;

import com.lolc.api.rest.enums.Currency;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record LoanRequest(

        @NotNull
        @Positive
        Long customerId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal loanAmount,

//        @NotBlank
        Currency currency,

        @NotNull
        @DecimalMin("0.01")
        Double interestRate,

        @NotNull
        @Min(1)
        Integer durationMonths,

        @Nullable
        String loanStatus
) { }
