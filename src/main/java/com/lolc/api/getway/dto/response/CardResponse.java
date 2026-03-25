package com.lolc.api.getway.dto.response;

import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.enums.CardType;
import com.lolc.api.getway.enums.Status;

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
