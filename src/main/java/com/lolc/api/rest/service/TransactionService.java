package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.TransactionDTO;
import com.lolc.api.rest.dto.request.TransferRequest;
import com.lolc.api.rest.dto.response.TransactionResponse;
import com.lolc.api.rest.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction create(TransactionDTO transactionDTO);
    List<TransactionResponse> list();
    Transaction findById(Long transactionId);
    TransactionResponse findResponseById(Long transactionId);
    Transaction update(Long transactionId, TransactionDTO transactionDTO);
    void delete(Long transactionId);

    TransactionResponse transfer(TransferRequest request);

    List<TransactionResponse> getByAccountId(Long accountId);
}