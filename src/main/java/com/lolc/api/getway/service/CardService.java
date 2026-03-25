package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.request.CardRequest;
import com.lolc.api.getway.dto.response.CardResponse;
import com.lolc.api.getway.entity.Card;

import java.util.List;

public interface CardService {
    CardResponse createCard(CardRequest cardRequest);
    List<Card> findAll();
    List<CardResponse> getCardsByAccountId(Long accountId);
}
