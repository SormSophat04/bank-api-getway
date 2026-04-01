package com.lolc.api.rest.dto.request;

import com.lolc.api.rest.enums.CardType;
import jakarta.validation.constraints.NotNull;

public record CardRequest(
        @NotNull(message = "Account ID is required")
        Long accountId,

        @NotNull(message = "Card type is required")
        CardType cardType
) {
}
