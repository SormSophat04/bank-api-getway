package com.lolc.api.getway.dto.request;

import com.lolc.api.getway.enums.CardType;
import jakarta.validation.constraints.NotNull;

public record CardRequest(
        @NotNull(message = "Account ID is required")
        Long accountId,

        @NotNull(message = "Card type is required")
        CardType cardType
) {
}
