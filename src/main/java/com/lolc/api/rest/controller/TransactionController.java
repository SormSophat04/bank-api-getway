package com.lolc.api.rest.controller;

import com.lolc.api.rest.dto.request.TransferRequest;
import com.lolc.api.rest.dto.response.TransactionResponse;
import com.lolc.api.rest.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransferRequest request) {
        TransactionResponse transfer = transactionService.transfer(request);
        return ResponseEntity.ok(transfer);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransaction(){
        List<TransactionResponse> list = transactionService.list();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId){
        TransactionResponse byId = transactionService.findResponseById(transactionId);
        return ResponseEntity.ok(byId);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionByAccountId(@PathVariable Long accountId){
        List<TransactionResponse> transactions = transactionService.getByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
}
