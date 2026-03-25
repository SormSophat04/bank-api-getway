package com.lolc.api.getway.impl;

import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.mapper.AccountMapper;
import com.lolc.api.getway.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void findByCustomerIdShouldDelegateToRepository() {
        Account account = new Account();
        List<Account> expectedAccounts = List.of(account);

        when(accountRepository.findByCustomer_CustomerId(1L))
                .thenReturn(expectedAccounts);

        List<Account> accounts = accountService.findByCustomerId(1L);

        assertSame(expectedAccounts, accounts);
        verify(accountRepository).findByCustomer_CustomerId(1L);
    }
}
