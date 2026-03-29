package com.lolc.api.getway.repository;

import com.lolc.api.getway.dto.response.TransactionResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountIdOrToAccountIdOrderByCreateAtDesc(Account fromAccountId, Account toAccountId);

    Page<TransactionResponse> pagination(Pageable pageable);
}
