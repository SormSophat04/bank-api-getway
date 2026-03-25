package com.lolc.api.getway.dto.response;

import com.lolc.api.getway.entity.Customer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponse(
        Long loanId,
        Customer customer,
        BigDecimal loanAmount,
        Double interestRate,
        Integer durationMonths,
        BigDecimal monthlyPayment,
        String loanStatus,
        LocalDateTime createAt,
        LocalDateTime updateAt,
        String createBy,
        String updateBy
)  { }
