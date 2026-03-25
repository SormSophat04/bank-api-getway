package com.lolc.api.getway.dto.request;

import java.math.BigDecimal;

public record TransferRequest(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        String description
) {
}
