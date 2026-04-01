package com.lolc.api.rest.repository;

import com.lolc.api.rest.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomer_CustomerId(Long customerId);
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
