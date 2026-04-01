package com.lolc.api.rest.dto.request;

import java.math.BigDecimal;

public record TransferRequest(
        String fromAccountNumber,
        String toAccountNumber,
        BigDecimal amount,
        String description
) {
}
