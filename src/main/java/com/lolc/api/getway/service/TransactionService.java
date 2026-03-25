package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.TransactionDTO;
import com.lolc.api.getway.dto.request.TransferRequest;
import com.lolc.api.getway.dto.response.TransactionResponse;
import com.lolc.api.getway.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction create(TransactionDTO transactionDTO);
    List<TransactionResponse> list();
    Transaction findById(Long transactionId);
    Transaction update(Long transactionId, TransactionDTO transactionDTO);
    void delete(Long transactionId);

    TransactionResponse transfer(TransferRequest request);

    List<TransactionResponse> getByAccountId(Long accountId);
}
