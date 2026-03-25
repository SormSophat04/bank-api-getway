package com.lolc.api.getway.service;

import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.entity.Account;

import java.util.List;

public interface AccountService {
    Account create(Account account);
    List<AccountDTO> findAll();
    List<AccountDTO> findByCustomerId(Long customerId);
    Account findById(Long accountId);
    Account update(Long accountId, AccountDTO accountDTO);
    void delete(Long accountId);
}
