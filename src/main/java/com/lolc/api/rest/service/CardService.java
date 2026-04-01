package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.request.CardRequest;
import com.lolc.api.rest.dto.response.CardResponse;
import com.lolc.api.rest.entity.Card;

import java.util.List;

public interface CardService {
    CardResponse createCard(CardRequest cardRequest);
    List<Card> findAll();
    List<CardResponse> getCardsByAccountId(Long accountId);
}
