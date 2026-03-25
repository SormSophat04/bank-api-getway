package com.lolc.api.getway.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanRequest(

        @NotNull
        @Positive
        Long customerId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal loanAmount,

        @NotNull
        @DecimalMin("0.01")
        Double interestRate,

        @NotNull
        @Min(1)
        Integer durationMonths,

        @NotBlank
        String loanStatus,

        LocalDateTime createAt,

        LocalDateTime updateAt,

        String createBy,

        String updateBy
) { }
