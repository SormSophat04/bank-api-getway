package com.lolc.api.getway.rest;

import com.lolc.api.getway.dto.TransactionDTO;
import com.lolc.api.getway.dto.request.TransferRequest;
import com.lolc.api.getway.dto.response.TransactionResponse;
import com.lolc.api.getway.entity.Transaction;
import com.lolc.api.getway.service.TransactionService;
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
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long transactionId){
        Transaction byId = transactionService.findById(transactionId);
        return ResponseEntity.ok(byId);
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<List<TransactionResponse>> getTransactionByAccountId(@PathVariable Long accountId){
        List<TransactionResponse> transactions = transactionService.getByAccountId(accountId);
        return ResponseEntity.ok(transactions);
    }
}
