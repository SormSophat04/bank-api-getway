package com.lolc.api.rest.service;

import com.lolc.api.rest.dto.AccountDTO;
import com.lolc.api.rest.dto.response.KhqrResponse;
import com.lolc.api.rest.entity.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    Account create(Account account);
    List<AccountDTO> findAll();
    List<AccountDTO> findByCustomerId(Long customerId);
    Account findById(Long accountId);
    KhqrResponse generateKhqr(Long accountId, BigDecimal amount, String bakongAccountId);
    Account update(Long accountId, AccountDTO accountDTO);
    void delete(Long accountId);
}
