package com.lolc.api.rest.dto.response;

import java.math.BigDecimal;

public record KhqrResponse(
        Long accountId,
        String accountNumber,
        String bakongAccountId,
        BigDecimal amount,
        String payload,
        String qrCodeBase64
) {
}
