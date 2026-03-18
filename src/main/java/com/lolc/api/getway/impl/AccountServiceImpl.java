package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.mapper.AccountMapper;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public Account create(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow();
    }

    @Override
    public Account update(Long accountId, AccountDTO accountDTO) {
        Account account = findById(accountId);
        accountMapper.updateAccountFromDto(accountDTO, account);
        return accountRepository.save(account);
    }

    @Override
    public void delete(Long accountId) {
        Account account = findById(accountId);
        accountRepository.delete(account);
    }
}
