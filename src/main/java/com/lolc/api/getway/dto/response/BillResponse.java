package com.lolc.api.getway.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lolc.api.getway.enums.BillStatus;
import com.lolc.api.getway.enums.BillType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BillResponse(
        Long billId,
        BillType billType,
        String billCode,
        String customerName,
        String address,
        String phoneNumber,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate periodFrom,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate periodTo,
        BigDecimal feeAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        String currency,
        BillStatus status,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate dueDate
) {}
