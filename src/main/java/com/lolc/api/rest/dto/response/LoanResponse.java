package com.lolc.api.rest.dto.response;

import com.lolc.api.rest.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponse(
        Long loanId,
        CustomerSnapshot customer,
        BigDecimal loanAmount,
        Currency currency,
        Double interestRate,
        Integer durationMonths,
        BigDecimal monthlyPayment,
        BigDecimal principal,
        BigDecimal total_interest,
        BigDecimal totalRepayment,
        LocalDateTime createAt,
        LocalDateTime updateAt
)  {
    public record CustomerSnapshot(
            Long customerId,
            String firstName,
            String lastName,
            String phone,
            String email
    ) { }
}
