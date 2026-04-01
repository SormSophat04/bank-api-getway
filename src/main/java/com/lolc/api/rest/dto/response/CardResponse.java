package com.lolc.api.rest.dto.response;

import com.lolc.api.rest.dto.AccountDTO;
import com.lolc.api.rest.enums.CardType;
import com.lolc.api.rest.enums.Status;

import java.time.LocalDate;

public record CardResponse(
        Long cardId,
        String cardNumber,
        LocalDate expiryDate,
        String cvv,
        CardType cardType,
        Status status,
        AccountDTO account
) {
}
