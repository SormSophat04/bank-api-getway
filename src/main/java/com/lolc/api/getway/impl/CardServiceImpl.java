package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.request.CardRequest;
import com.lolc.api.getway.dto.response.CardResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Card;
import com.lolc.api.getway.enums.Status;
import com.lolc.api.getway.mapper.CardMapper;
import com.lolc.api.getway.repository.CardRepository;
import com.lolc.api.getway.service.AccountService;
import com.lolc.api.getway.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;
    private final CardMapper cardMapper;


    @Override
    @Transactional
    public CardResponse createCard(CardRequest cardRequest) {
        Card card = new Card();
        card.setCardNumber(generateRandomCardNumber());
        card.setExpiryDate(LocalDate.now().plusYears(3));
        card.setCvv(generateRandomCvv());
        card.setCardType(cardRequest.cardType());
        card.setStatus(Status.ACTIVE);
        card.setAccount(accountService.findById(cardRequest.accountId()));
        return cardMapper.toResponse(cardRepository.save(card));
    }

    private String generateRandomCardNumber() {
        Random random = new Random();
        String part1 = String.format("%04d", random.nextInt(10000));
        String part2 = String.format("%04d", random.nextInt(10000));
        String part3 = String.format("%04d", random.nextInt(10000));
        String part4 = String.format("%04d", random.nextInt(10000));
        return part1 + part2 + part3 + part4;
    }

    private String generateRandomCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponse> getCardsByAccountId(Long accountId) {
        Account account = accountService.findById(accountId);
       return cardRepository.findAllByAccount(account).stream()
               .map(cardMapper::toResponse)
               .collect(Collectors.toList());
    }

    @Override
    public List<Card> findAll() {
        return List.of();
    }
}
