package com.lolc.api.rest.controller;

import com.lolc.api.rest.dto.request.CardRequest;
import com.lolc.api.rest.dto.response.CardResponse;
import com.lolc.api.rest.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request) {
        return ResponseEntity.ok(cardService.createCard(request));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardResponse>> getCardsByAccountId(@PathVariable Long accountId) {
        return ResponseEntity.ok(cardService.getCardsByAccountId(accountId));
    }
}
