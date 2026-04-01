package com.lolc.api.rest.repository;

import com.lolc.api.rest.entity.Account;
import com.lolc.api.rest.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByFromAccountIdOrToAccountIdOrderByCreateAtDesc(Account fromAccountId, Account toAccountId);
}
