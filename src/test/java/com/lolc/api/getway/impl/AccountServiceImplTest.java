package com.lolc.api.getway.impl;

import com.lolc.api.getway.dto.AccountDTO;
import com.lolc.api.getway.dto.response.KhqrResponse;
import com.lolc.api.getway.entity.Account;
import com.lolc.api.getway.enums.Currency;
import com.lolc.api.getway.mapper.AccountMapper;
import com.lolc.api.getway.repository.AccountRepository;
import kh.gov.nbc.bakong_khqr.BakongKHQR;
import kh.gov.nbc.bakong_khqr.model.CRCValidation;
import kh.gov.nbc.bakong_khqr.model.KHQRResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void findByCustomerIdShouldMapToAccountDto() {
        Account account = new Account();
        AccountDTO accountDTO = new AccountDTO();

        when(accountRepository.findByCustomer_CustomerId(1L))
                .thenReturn(List.of(account));
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        List<AccountDTO> accounts = accountService.findByCustomerId(1L);

        assertEquals(List.of(accountDTO), accounts);
        verify(accountRepository).findByCustomer_CustomerId(1L);
        verify(accountMapper).toAccountDTO(account);
    }

    @Test
    void generateKhqrShouldBuildPayableDynamicKhqr() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setAccountNumber("001122334455");
        account.setCurrency(Currency.USD);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        KhqrResponse response = accountService.generateKhqr(1L, new BigDecimal("10.50"), "001122334455@aclb");

        assertNotNull(response);
        assertEquals(1L, response.accountId());
        assertEquals("001122334455@aclb", response.bakongAccountId());
        assertEquals(0, new BigDecimal("10.5").compareTo(response.amount()));
        assertTrue(response.payload().startsWith("000201"));
        assertTrue(response.payload().contains("010212"));
        assertTrue(response.payload().contains("5303840"));
        assertTrue(response.payload().matches(".*6304[0-9A-F]{4}$"));
        assertNotNull(response.qrCodeBase64());
        assertFalse(response.qrCodeBase64().isBlank());

        KHQRResponse<CRCValidation> verify = BakongKHQR.verify(response.payload());
        assertEquals(0, verify.getKHQRStatus().getCode());
        assertNotNull(verify.getData());
        assertTrue(verify.getData().isValid());
    }

    @Test
    void generateKhqrShouldBuildPayableStaticKhqr() {
        Account account = new Account();
        account.setAccountId(2L);
        account.setAccountNumber("998877665544");
        account.setCurrency(Currency.KHR);

        when(accountRepository.findById(2L)).thenReturn(Optional.of(account));

        KhqrResponse response = accountService.generateKhqr(2L, null, "998877665544@aclb");

        assertNotNull(response);
        assertEquals("998877665544@aclb", response.bakongAccountId());
        assertNull(response.amount());
        assertTrue(response.payload().contains("010211"));
        assertTrue(response.payload().contains("5303116"));
        assertTrue(response.payload().matches(".*6304[0-9A-F]{4}$"));

        KHQRResponse<CRCValidation> verify = BakongKHQR.verify(response.payload());
        assertEquals(0, verify.getKHQRStatus().getCode());
        assertNotNull(verify.getData());
        assertTrue(verify.getData().isValid());
    }

    @Test
    void generateKhqrShouldRejectMissingBakongIdFormat() {
        Account account = new Account();
        account.setAccountId(3L);
        account.setAccountNumber("123456789");
        account.setCurrency(Currency.USD);

        when(accountRepository.findById(3L)).thenReturn(Optional.of(account));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountService.generateKhqr(3L, null, null)
        );

        assertTrue(exception.getMessage().contains("Bakong account ID"));
    }
}
