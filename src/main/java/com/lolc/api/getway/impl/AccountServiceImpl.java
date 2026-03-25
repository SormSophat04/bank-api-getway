package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.exception.ResourceNotFoundException;
import com.lolc.api.getway.mapper.AccountMapper;
import com.lolc.api.getway.repository.AccountRepository;
import com.lolc.api.getway.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    @Transactional
    public List<AccountDTO> findAll() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AccountDTO> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomer_CustomerId(customerId).stream()
                .map(accountMapper::toAccountDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Account findById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(()->
                new ResourceNotFoundException("Account not found with id " + accountId));
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
